package com.seu.magicfilter.camera;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.seu.magicfilter.camera.bean.PixelBuffer;
import com.seu.magicfilter.camera.interfaces.OnRecordListener;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 录制数据
 *
 * @author Created by jz on 2017/5/2 11:21
 */
public class RecordHelper extends Handler {

    private static final int MAX_CACHE_BUFFER_NUMBER = 24;

    private static final int PREVIEW_BITMAP = 0;

    private int[] mPixelData;
    private List<byte[]> mReusableBuffers;

    private List<PixelBuffer> mBuffers;
    private Thread mThread;

    private OnRecordListener mOnRecordListener;

    public RecordHelper() {
        super(Looper.getMainLooper());
        mReusableBuffers = Collections.synchronizedList(new ArrayList<byte[]>());

        mBuffers = Collections.synchronizedList(new ArrayList<PixelBuffer>());
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case PREVIEW_BITMAP:
                if (mOnRecordListener != null)
                    mOnRecordListener.onRecord((Bitmap) msg.obj);
                break;
        }
    }

    public void setOnRecordListener(OnRecordListener l) {
        this.mOnRecordListener = l;
    }

    public void onRecord(ByteBuffer buffer, int width, int height, int pixelStride, int rowStride, long timestamp) {
        if (mBuffers.size() >= MAX_CACHE_BUFFER_NUMBER) {
            return;
        }

        byte[] data = getBuffer(rowStride * height);
        buffer.get(data);
        buffer.clear();

        mBuffers.add(new PixelBuffer(data, width, height, pixelStride, rowStride, timestamp));
    }

    public void start() {
        if (mThread != null) {
            return;
        }
        mThread = new MyThread();
        mThread.start();
    }

    public void stop() {
        if (mThread == null) {
            return;
        }
        mThread.interrupt();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mThread = null;
    }

    private class MyThread extends Thread {//转换成Bitmap演示用效率低下，可以用libyuv代替

        @Override
        public void run() {
            while (!isInterrupted()) {
                if (mBuffers.isEmpty()) {
                    SystemClock.sleep(1);
                    continue;
                }

                PixelBuffer buffer = mBuffers.remove(0);

                byte[] data = buffer.getData();
                int width = buffer.getWidth();
                int height = buffer.getHeight();
                int pixelStride = buffer.getPixelStride();
                int rowStride = buffer.getRowStride();

                int size = width * height;
                if (mPixelData == null || size != mPixelData.length) {
                    mPixelData = new int[width * height];
                }

                int offset = 0;
                int index = 0;
                for (int i = 0; i < height; ++i) {
                    for (int j = 0; j < width; ++j) {
                        int pixel = 0;
                        pixel |= (data[offset] & 0xff) << 16;     // R
                        pixel |= (data[offset + 1] & 0xff) << 8;  // G
                        pixel |= (data[offset + 2] & 0xff);       // B
                        pixel |= (data[offset + 3] & 0xff) << 24; // A
                        mPixelData[index++] = pixel;
                        offset += 4;
                    }
                    offset += rowStride - width * pixelStride;
                }

                Bitmap bitmap = Bitmap.createBitmap(mPixelData,
                        width, height,
                        Bitmap.Config.ARGB_8888);
                sendMessage(obtainMessage(PREVIEW_BITMAP, bitmap));

                mReusableBuffers.add(data);
            }
            mBuffers.clear();
        }
    }

    private byte[] getBuffer(int length) {
        if (mReusableBuffers.isEmpty()) {
            return new byte[length];
        } else {
            return mReusableBuffers.remove(0);
        }
    }
}