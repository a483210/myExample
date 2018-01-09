package com.seu.magicfilter.camera;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 摄像头帮助类
 *
 * @author Created by jz on 2017/5/2 11:21
 */
public class CameraHelper {

    private static final int FPS = 24;//默认帧
    private static final String TAG = "CameraHelper";

    private Camera mCamera;
    private SurfaceTexture mSurfaceTexture;//从openGl获得
    private SurfaceHolder mSurfaceHolder;//从surfaceView获得

    private int mCameraId;//摄像头状态
    private boolean mIsFlashOpen;//闪光灯状态

    private int mRecordWidth;//录制宽
    private int mRecordHeight;// 录制高
    private int mPreviewWidth;
    private int mPreviewHeight;

    public CameraHelper() {
        this(CameraInfo.CAMERA_FACING_FRONT);
    }

    public CameraHelper(int cameraId) {
        this(cameraId, CameraGlSurfaceView.RECORD_WIDTH, CameraGlSurfaceView.RECORD_HEIGHT);
    }

    public CameraHelper(int cameraId, int recordWidth, int recordHeight) {
        this.mCameraId = cameraId;
        this.mRecordWidth = recordWidth;
        this.mRecordHeight = recordHeight;
    }

    /**
     * 打开摄像头
     *
     * @return true成功
     */
    public boolean openCamera() {
        if (mCamera != null)
            return false;
        try {
            mCamera = Camera.open(mCameraId);
            setDefaultParameters();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            stopCamera();
            return false;
        }
    }

    /**
     * 释放摄像头
     */
    public void stopCamera() {
        if (mCamera == null)
            return;
        try {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 是否打开摄像头
     */
    public boolean isOpenCamera() {
        return mCamera != null;
    }

    /**
     * 切换闪光灯
     */
    public void switchFlash() {
        switchFlash(!mIsFlashOpen);
    }

    /**
     * 切换闪光灯
     *
     * @param isOpen 是否打开
     */
    public void switchFlash(boolean isOpen) {
        if (mCamera == null)
            return;
        try {
            Parameters params = mCamera.getParameters();
            if (isOpen)
                params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            else
                params.setFlashMode(Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(params);
            mIsFlashOpen = isOpen;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换前后摄像头
     */
    public boolean switchCamera() {
        return switchCamera(mCameraId == CameraInfo.CAMERA_FACING_BACK ?
                CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * 切换前后摄像头
     *
     * @param id CameraInfo.CAMERA_FACING_BACK &  CameraInfo.CAMERA_FACING_FRONT
     */
    public boolean switchCamera(int id) {
        mIsFlashOpen = false;
        mCameraId = id;
        stopCamera();
        boolean rel = openCamera();
        startPreview();
        return rel;
    }

    /**
     * 选择焦点
     *
     * @param rect 矩阵
     */
    public boolean selectCameraFocus(Rect rect, Camera.AutoFocusCallback callBack) {
        if (mCamera == null)
            return false;
        try {
            mCamera.autoFocus(callBack);
            Parameters params = mCamera.getParameters();
            if (params != null && params.getMaxNumFocusAreas() > 0) {
                List<Camera.Area> focusAreas = new ArrayList<>();
                focusAreas.add(new Camera.Area(rect, 1000));
                params.setFocusAreas(focusAreas);
                mCamera.setParameters(params);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 开始浏览，surfaceView
     *
     * @param surfaceHolder surface
     */
    public void startPreview(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        startPreview();
    }

    /**
     * 开始浏览，openGl
     *
     * @param surfaceTexture surface
     */
    public void startPreview(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        startPreview();
    }

    //开始浏览，内部调用
    public void startPreview() {
        if (mCamera == null)
            return;
        try {
            if (mSurfaceTexture != null) {
                mCamera.setPreviewTexture(mSurfaceTexture);
            } else {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.setDisplayOrientation(90);
            }
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaultParameters() {
        if (mCamera == null)
            return;

        Parameters params = mCamera.getParameters();

//        params.setRecordingHint(true);//设置为多媒体录制，加快打开的速度
//        params.set("video-size", "640x480");//配合setRecordingHint函数，不然会发生变形

        //自动对焦
        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (!supportedFocusModes.isEmpty()) {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            else
                params.setFocusMode(supportedFocusModes.get(0));
        }

        //闪光灯
        if (mIsFlashOpen)
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
        else
            params.setFlashMode(Parameters.FLASH_MODE_OFF);

        params.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);//自动白平衡
        params.setSceneMode(Parameters.SCENE_MODE_AUTO);//自动相机场景类型

        params.setPreviewFormat(ImageFormat.NV21);//帧类型

        //设置帧Fps区间
        int[] range = adaptFpsRange(FPS, params);
        params.setPreviewFpsRange(range[0], range[1]);

        //设置大小
        Size previewSize = adaptPreviewSize(params);
        params.setPreviewSize(previewSize.width, previewSize.height);
        Size pictureSize = adaptPictureSize(params);
        params.setPictureSize(pictureSize.width, pictureSize.height);
        mCamera.setParameters(params);
    }

    /**
     * 是否后置摄像头
     */
    public boolean isBackCamera() {
        return mCameraId == CameraInfo.CAMERA_FACING_BACK;
    }

    /**
     * 是否前置摄像头
     */
    public boolean isFrontCamera() {
        return mCameraId == CameraInfo.CAMERA_FACING_FRONT;
    }

    /**
     * 获得方向
     */
    public int getOrientation() {
        return getCameraInfo().orientation;
    }

    /**
     * 是否水平翻转
     */
    public boolean isFlipHorizontal() {
        return getCameraInfo().facing == CameraInfo.CAMERA_FACING_FRONT;
    }

    /**
     * 获得摄像头旋转信息
     */
    public CameraItem getCameraAngleInfo() {
        return new CameraItem(getCameraInfo().orientation, mCameraId == 1);
    }

    /**
     * 获得摄像头信息
     */
    public CameraInfo getCameraInfo() {
        CameraInfo cameraInfo = new CameraInfo();
        try {
            Camera.getCameraInfo(mCameraId, cameraInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cameraInfo;
    }

    public static class CameraItem {
        public int orientation;
        public boolean isFront;

        public CameraItem(int orientation, boolean isFront) {
            this.orientation = orientation;
            this.isFront = isFront;
        }
    }

    /**
     * 获得录制宽度
     */
    public int getRecordWidth() {
        return mRecordWidth;
    }

    /**
     * 获得录制高度
     */
    public int getRecordHeight() {
        return mRecordHeight;
    }

    /**
     * 获得浏览宽度
     */
    public int getPreviewWidth() {
        return mPreviewWidth;
    }

    /**
     * 获得浏览高度
     */
    public int getPreviewHeight() {
        return mPreviewHeight;
    }

    /**
     * 闪光灯是否打开
     */
    public boolean isFlashOpen() {
        return mIsFlashOpen;
    }

    /**
     * 获得surfaceTexture最新数据帧的时间截,ns
     */
    public long getTimestamp() {
        if (mSurfaceTexture == null)
            return 0;
        return mSurfaceTexture.getTimestamp();
    }

    //适配fps区间
    private int[] adaptFpsRange(int expectedFps, Parameters params) {
        List<int[]> ranges = params.getSupportedPreviewFpsRange();
        expectedFps *= 1000;
        int[] closestRange = ranges.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        int count = ranges.size();
        for (int i = 1; i < count; i++) {
            int[] range = ranges.get(i);
            int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
            if (curMeasure < measure) {
                closestRange = range;
                measure = curMeasure;
            }
        }
        return closestRange;
    }

    private Size adaptPreviewSize(Parameters params) {
        List<Size> sizes = params.getSupportedPreviewSizes();
        double minDiffW = Double.MAX_VALUE;
        double minDiffH = Double.MAX_VALUE;
        Size optimalSize = null;
        for (int index = sizes.size() - 1; index >= 0; index--) {
            Size size = sizes.get(index);
            if (size.height >= mRecordWidth && size.width >= mRecordHeight) {
                if (size.height - mRecordWidth < minDiffW || size.width - mRecordHeight < minDiffH) {
                    optimalSize = size;
                    minDiffW = size.height - mRecordWidth;
                    minDiffH = size.width - mRecordHeight;
                }
            }
        }
        if (optimalSize == null) {
            mPreviewWidth = sizes.get(sizes.size() - 1).height;
            mPreviewHeight = sizes.get(sizes.size() - 1).width;
        } else {
            mPreviewWidth = optimalSize.height;
            mPreviewHeight = optimalSize.width;
        }
        return optimalSize;
    }

    private Size adaptPictureSize(Parameters params) {
        List<Size> sizes = params.getSupportedPictureSizes();
        Collections.sort(sizes, new ResolutionComparator());
        Size size = null;
        for (int i = 0; i < sizes.size(); i++) {
            size = sizes.get(i);
            if (size != null
                    && size.width >= mRecordHeight
                    && size.height >= mRecordWidth)
                return size;
        }
        return size;
    }

    // 排序
    private class ResolutionComparator implements Comparator<Size> {
        @Override
        public int compare(Size size1, Size size2) {
            if (size1.height != size2.height)
                return size1.height - size2.height;
            else
                return size1.width - size2.width;
        }
    }

}