package com.seu.magicfilter.filter.base;

import android.opengl.GLES20;

import com.seu.magicfilter.R;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;
import com.seu.magicfilter.utils.OpenGlUtils;

public class MagicLookupFilter extends GPUImageFilter {

    protected String table;

    public MagicLookupFilter(String table) {
        super(R.raw.lookup);
        this.table = table;
    }

    public int mLookupTextureUniform;
    public int mLookupSourceTexture = OpenGlUtils.NO_TEXTURE;

    protected void onInit() {
        super.onInit();
        mLookupTextureUniform = GLES20.glGetUniformLocation(getProgram(), "inputImageTexture2");
    }

    protected void onInitialized() {
        super.onInitialized();
        runOnDraw(new Runnable() {
            public void run() {
                mLookupSourceTexture = OpenGlUtils.loadTexture(mContext, table);
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        int[] texture = new int[]{mLookupSourceTexture};
        GLES20.glDeleteTextures(1, texture, 0);
        mLookupSourceTexture = -1;
    }

    protected void onDrawArraysAfter() {
        if (mLookupSourceTexture != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        }
    }

    protected void onDrawArraysPre() {
        if (mLookupSourceTexture != -1) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mLookupSourceTexture);
            GLES20.glUniform1i(mLookupTextureUniform, 3);
        }
    }
}
