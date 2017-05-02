package com.seu.magicfilter.camera.interfaces;

/**
 * 切换摄像头回调
 *
 * @author Created by jz on 2017/5/2 16:56
 */
public interface OnSwitchCameraListener {
    /**
     * 如果失败msg将不为空
     *
     * @param isSuccess 是否切换成功
     * @param msg       失败信息
     */
    void onSwitchCamera(boolean isSuccess, String msg);
}
