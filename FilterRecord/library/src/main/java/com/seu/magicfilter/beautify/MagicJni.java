package com.seu.magicfilter.beautify;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;

/**
 * jni
 *
 * @author Created by jz on 2017/5/2 16:57
 */
public class MagicJni {
    static {
        System.loadLibrary("MagicBeautify");
    }

    public static native void jniInitMagicBeautify(ByteBuffer handler);

    public static native void jniUnInitMagicBeautify();

    public static native void jniStartSkinSmooth(float denoiseLevel);

    public static native void jniStartWhiteSkin(float whitenLevel);

    public static native ByteBuffer jniStoreBitmapData(Bitmap bitmap);

    public static native void jniFreeBitmapData(ByteBuffer handler);

    public static native Bitmap jniGetBitmapFromStoredBitmapData(ByteBuffer handler);

    public static native void glReadPixels(int x, int y, int width, int height, int format, int type);
}
