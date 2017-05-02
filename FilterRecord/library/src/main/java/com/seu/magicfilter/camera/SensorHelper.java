package com.seu.magicfilter.camera;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * 重力感应
 *
 * @author Created by jz on 2017/5/2 16:58
 */
public class SensorHelper implements SensorEventListener {

    private SensorManager mSensorManager;// 重力感应

    private OnSensorListener mOnSensorListener;

    public SensorHelper(Context context, OnSensorListener l) {  // 设置重力感应
        try {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);// SENSOR_DELAY_GAME
        } catch (Exception e) {
            e.printStackTrace();// 说明不支持重力感应
        }
        mOnSensorListener = l;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mOnSensorListener == null)
            return;
        if (event.sensor == null)
            return;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) event.values[0];
            int y = (int) event.values[1];
            if (Math.abs(x) > 6) {// 倾斜度超过60度 10*1.732/2
                if (x <= -3)
                    mOnSensorListener.onSensor(1, true);
                else
                    mOnSensorListener.onSensor(1, false);
            } else {
                if (y <= -3)
                    mOnSensorListener.onSensor(0, true);
                else
                    mOnSensorListener.onSensor(0, false);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void release() {
        mSensorManager.unregisterListener(this);
    }

    public interface OnSensorListener {
        /**
         * 参数返回
         *
         * @param orientation 横竖屏(1横0竖)
         * @param isInversion 是否倒置
         */
        void onSensor(int orientation, boolean isInversion);
    }
}