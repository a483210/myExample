package com.seu.magicfilter.camera.bean;

import java.util.Arrays;

/**
 * 摄像头帧缓存
 *
 * @author Created by jz on 2017/5/2 16:56
 */
public class PixelBuffer {
    private final byte[] data;
    private final int width;
    private final int height;
    private final int pixelStride;
    private final int rowStride;
    private final long timestamp;

    public PixelBuffer(byte[] data, int width, int height, int pixelStride, int rowStride, long timestamp) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.pixelStride = pixelStride;
        this.rowStride = rowStride;
        this.timestamp = timestamp;
    }

    public byte[] getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPixelStride() {
        return pixelStride;
    }

    public int getRowStride() {
        return rowStride;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "PixelBuffer{" +
                "data=" + Arrays.toString(data) +
                ", width=" + width +
                ", height=" + height +
                ", pixelStride=" + pixelStride +
                ", rowStride=" + rowStride +
                ", timestamp=" + timestamp +
                '}';
    }
}
