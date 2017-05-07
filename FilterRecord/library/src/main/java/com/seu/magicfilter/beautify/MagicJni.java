package com.seu.magicfilter.beautify;

/**
 * jni
 *
 * @author Created by jz on 2017/5/2 16:57
 */
public class MagicJni {
    static {
        System.loadLibrary("MagicBeautify");
    }

    public static native void glReadPixels(int x, int y, int width, int height, int format, int type);
}
