package com.seu.magicfilter.camera;

import android.os.Handler;
import android.os.Message;

import com.seu.magicfilter.camera.interfaces.OnErrorListener;
import com.seu.magicfilter.camera.interfaces.OnSwitchCameraListener;

/**
 * 将消息发回主线程
 *
 * @author Created by jz on 2017/5/2 16:56
 */
public class ThreadHelper extends Handler {

    private static final int ERROR = 0, SWITCH_CAMERA = 1;

    private OnSwitchCameraListener mOnSwitchCameraListener;
    private OnErrorListener mOnErrorListener;//整体失败信息

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SWITCH_CAMERA:
                if (mOnSwitchCameraListener != null)
                    mOnSwitchCameraListener.onSwitchCamera(msg.arg1 == 1, (String) msg.obj);
                setOnSwitchCameraListener(null);
                break;
            case ERROR:
                if (mOnErrorListener != null)
                    mOnErrorListener.onError((String) msg.obj);
                break;
        }
    }

    /**
     * 设置切换摄像头回调
     */
    public void setOnSwitchCameraListener(OnSwitchCameraListener l) {
        this.mOnSwitchCameraListener = l;
    }

    public void sendSwitchCamera(boolean isSuccess, String msg) {
        Message message = Message.obtain();
        message.obj = isSuccess ? null : msg;
        message.arg1 = isSuccess ? 1 : 0;
        message.what = SWITCH_CAMERA;
        sendMessage(message);
    }

    /**
     * 设置错误回调
     */
    public void setOnErrorListener(OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    /**
     * 发送错误
     *
     * @param msg 错误信息
     */
    public void sendError(String msg) {
        sendMessage(obtainMessage(ERROR, msg));
    }
}
