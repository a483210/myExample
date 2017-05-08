package com.seu.magicfilter.camera.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.filter.helper.MagicFilterFactory;
import com.seu.magicfilter.filter.helper.MagicFilterType;
import com.seu.magicfilter.utils.OpenGlUtils;
import com.seu.magicfilter.utils.Rotation;
import com.seu.magicfilter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * BaseGlSurfaceView
 *
 * @author Created by jz on 2017/5/2 16:56
 */
public abstract class BaseGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public static final int CENTER_INSIDE = 0, CENTER_CROP = 1, FIT_XY = 2;

    /**
     * 所选择的滤镜，类型为MagicBaseGroupFilter
     * 1.mCameraInputFilter将SurfaceTexture中YUV数据绘制到FrameBuffer
     * 2.filter将FrameBuffer中的纹理绘制到屏幕中
     */
    protected GPUImageFilter mFilter;//滤镜
    protected MagicFilterType mType = MagicFilterType.NONE;//滤镜类型
    protected int mTextureId = OpenGlUtils.NO_TEXTURE;//SurfaceTexture纹理id
    protected final FloatBuffer mGLCubeBuffer;//顶点坐标
    protected final FloatBuffer mGLTextureBuffer;//纹理坐标
    protected int mSurfaceWidth;//surface宽度
    protected int mSurfaceHeight;//surface高度
    protected int mPreviewWidth;//摄像头宽度
    protected int mPreviewHeight;//摄像头宽度
    protected int mRecordWidth;//录制宽度
    protected int mRecordHeight;//录制高度
    protected int mScaleType = FIT_XY;//显示类型

    public BaseGlSurfaceView(Context context) {
        this(context, null);
    }

    public BaseGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGLCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_NO_ROTATION).position(0);

        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().addCallback(this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glDisable(GL10.GL_DITHER);
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GL10.GL_CULL_FACE);
        GLES20.glEnable(GL10.GL_DEPTH_TEST);

        if (mFilter == null) {
            mFilter = new GPUImageFilter();
            mFilter.init(getContext());
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        onFilterChanged();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    protected void onFilterChanged() {
        if (mFilter != null) {
            mFilter.initFrameBuffer(mPreviewWidth, mPreviewHeight);
            mFilter.onInputSizeChanged(mPreviewWidth, mPreviewHeight);
            mFilter.onDisplaySizeChanged(mSurfaceWidth, mSurfaceHeight);
        }
    }

    /**
     * 设置滤镜
     *
     * @param type 类型
     */
    public void setFilter(final MagicFilterType type) {
        mType = type;
        queueEvent(new Runnable() {
            @Override
            public void run() {
                if (mFilter != null)
                    mFilter.destroy();
                mFilter = MagicFilterFactory.initFilters(type);
                if (mFilter != null)
                    mFilter.init(getContext());
                onFilterChanged();
            }
        });
        requestRender();
    }

    protected void deleteTextures() {
        if (mTextureId != OpenGlUtils.NO_TEXTURE) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    GLES20.glDeleteTextures(1, new int[]{mTextureId}, 0);
                    mTextureId = OpenGlUtils.NO_TEXTURE;
                }
            });
        }
    }

    protected void adjustSize(int rotation, boolean flipHorizontal, boolean flipVertical) {
        float[][] data = adjustSize(mSurfaceWidth, mSurfaceHeight, rotation,
                flipHorizontal, flipVertical);

        mGLCubeBuffer.clear();
        mGLCubeBuffer.put(data[0]).position(0);
        mGLTextureBuffer.clear();
        mGLTextureBuffer.put(data[1]).position(0);
    }

    /**
     * 调整画面大小
     *
     * @param width          宽
     * @param height         高
     * @param rotation       角度
     * @param flipHorizontal 是否水平翻转
     * @param flipVertical   是否垂直翻转
     */
    protected float[][] adjustSize(int width, int height, int rotation, boolean flipHorizontal, boolean flipVertical) {
        float[] textureCords = TextureRotationUtil.getRotation(Rotation.fromInt(rotation),
                flipHorizontal, flipVertical);
        float[] cube = TextureRotationUtil.CUBE;
        float ratio1 = (float) width / mPreviewWidth;
        float ratio2 = (float) height / mPreviewHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(mPreviewWidth * ratioMax);
        int imageHeightNew = Math.round(mPreviewHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float) width;
        float ratioHeight = imageHeightNew / (float) height;

        switch (mScaleType) {
            case CENTER_INSIDE:
                cube = new float[]{
                        TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                        TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                        TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                        TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
                };
                break;
            case CENTER_CROP:
                float distHorizontal = (1 - 1 / ratioWidth) / 2;
                float distVertical = (1 - 1 / ratioHeight) / 2;
                textureCords = new float[]{
                        addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                        addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                        addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                        addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
                };
                break;
            case FIT_XY:

                break;
        }
        return new float[][]{cube, textureCords};
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }
}
