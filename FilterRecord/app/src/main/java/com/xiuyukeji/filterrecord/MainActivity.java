package com.xiuyukeji.filterrecord;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.seu.magicfilter.camera.CameraGlSurfaceView;
import com.seu.magicfilter.camera.interfaces.OnErrorListener;
import com.seu.magicfilter.camera.interfaces.OnRecordListener;
import com.seu.magicfilter.camera.interfaces.OnSwitchCameraListener;
import com.seu.magicfilter.filter.helper.MagicFilterType;

/**
 * 主界面
 *
 * @author Created by jz on 2017/5/2 17:01
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CameraGlSurfaceView mGLSurfaceView;
    private ImageView mImgView;
    private FloatingActionButton mRecordView;
    private FloatingActionButton mSwitchCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initView();
        setListener();
    }

    private void findView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGLSurfaceView = (CameraGlSurfaceView) findViewById(R.id.surface);
        mImgView = (ImageView) findViewById(R.id.img);
        mRecordView = (FloatingActionButton) findViewById(R.id.record);
        mSwitchCameraView = (FloatingActionButton) findViewById(R.id.switchCamera);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
    }

    private void setListener() {
        mRecordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGLSurfaceView.isRecording()) {
                    stopRecord();
                } else {
                    startRecord();
                }
                mRecordView.setSelected(mGLSurfaceView.isRecording());
            }
        });
        mSwitchCameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGLSurfaceView.switchCamera(new OnSwitchCameraListener() {
                    @Override
                    public void onSwitchCamera(boolean isSuccess, String msg) {
                        if (!isSuccess) {
                            Snackbar.make(mSwitchCameraView, msg, Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        mGLSurfaceView.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(String msg) {
                Snackbar.make(mRecordView, msg, Snackbar.LENGTH_LONG).show();
            }
        });
        mGLSurfaceView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onRecord(Bitmap bitmap) {
                mImgView.setImageBitmap(bitmap);
            }
        });
    }

    private void startRecord() {
        if (mGLSurfaceView.isRecording())
            return;

        mGLSurfaceView.startRecord();
    }

    private void stopRecord() {
        if (!mGLSurfaceView.isRecording())
            return;

        mGLSurfaceView.stopRecord();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_none:
                mGLSurfaceView.setFilter(MagicFilterType.NONE);
                return true;
//            case R.id.action_fairytale:
//                mGLSurfaceView.setFilter(MagicFilterType.FAIRYTALE);
//                return true;
            case R.id.action_sunrise:
                mGLSurfaceView.setFilter(MagicFilterType.SUNRISE);
                return true;
            case R.id.action_sunset:
                mGLSurfaceView.setFilter(MagicFilterType.SUNSET);
                return true;
            case R.id.action_whitecat:
                mGLSurfaceView.setFilter(MagicFilterType.WHITECAT);
                return true;
            case R.id.action_blackcat:
                mGLSurfaceView.setFilter(MagicFilterType.BLACKCAT);
                return true;
            case R.id.action_beauty:
                mGLSurfaceView.setFilter(MagicFilterType.BEAUTY);
                return true;
            case R.id.action_skinwhiten:
                mGLSurfaceView.setFilter(MagicFilterType.SKINWHITEN);
                return true;
            case R.id.action_healthy:
                mGLSurfaceView.setFilter(MagicFilterType.HEALTHY);
                return true;
//            case R.id.action_sweets:
//                mGLSurfaceView.setFilter(MagicFilterType.SWEETS);
//                return true;
            case R.id.action_romance:
                mGLSurfaceView.setFilter(MagicFilterType.ROMANCE);
                return true;
            case R.id.action_sakura:
                mGLSurfaceView.setFilter(MagicFilterType.SAKURA);
                return true;
            case R.id.action_warm:
                mGLSurfaceView.setFilter(MagicFilterType.WARM);
                return true;
            case R.id.action_antique:
                mGLSurfaceView.setFilter(MagicFilterType.ANTIQUE);
                return true;
            case R.id.action_nostalgia:
                mGLSurfaceView.setFilter(MagicFilterType.NOSTALGIA);
                return true;
            case R.id.action_calm:
                mGLSurfaceView.setFilter(MagicFilterType.CALM);
                return true;
            case R.id.action_latte:
                mGLSurfaceView.setFilter(MagicFilterType.LATTE);
                return true;
            case R.id.action_tender:
                mGLSurfaceView.setFilter(MagicFilterType.TENDER);
                return true;
            case R.id.action_cool:
                mGLSurfaceView.setFilter(MagicFilterType.COOL);
                return true;
            case R.id.action_emerald:
                mGLSurfaceView.setFilter(MagicFilterType.EMERALD);
                return true;
            case R.id.action_evergreen:
                mGLSurfaceView.setFilter(MagicFilterType.EVERGREEN);
                return true;
            case R.id.action_crayon:
                mGLSurfaceView.setFilter(MagicFilterType.CRAYON);
                return true;
            case R.id.action_sketch:
                mGLSurfaceView.setFilter(MagicFilterType.SKETCH);
                return true;
            case R.id.action_amaro:
                mGLSurfaceView.setFilter(MagicFilterType.AMARO);
                return true;
            case R.id.action_brannan:
                mGLSurfaceView.setFilter(MagicFilterType.BRANNAN);
                return true;
            case R.id.action_brooklyn:
                mGLSurfaceView.setFilter(MagicFilterType.BROOKLYN);
                return true;
            case R.id.action_earlybird:
                mGLSurfaceView.setFilter(MagicFilterType.EARLYBIRD);
                return true;
            case R.id.action_freud:
                mGLSurfaceView.setFilter(MagicFilterType.FREUD);
                return true;
//            case R.id.action_hefe:
//                mGLSurfaceView.setFilter(MagicFilterType.HEFE);
//                return true;
            case R.id.action_hudson:
                mGLSurfaceView.setFilter(MagicFilterType.HUDSON);
                return true;
            case R.id.action_inkwell:
                mGLSurfaceView.setFilter(MagicFilterType.INKWELL);
                return true;
            case R.id.action_kevin:
                mGLSurfaceView.setFilter(MagicFilterType.KEVIN);
                return true;
//            case R.id.action_lomo:
//                mGLSurfaceView.setFilter(MagicFilterType.LOMO);
//                return true;
            case R.id.action_n1977:
                mGLSurfaceView.setFilter(MagicFilterType.N1977);
                return true;
            case R.id.action_nashville:
                mGLSurfaceView.setFilter(MagicFilterType.NASHVILLE);
                return true;
            case R.id.action_pixar:
                mGLSurfaceView.setFilter(MagicFilterType.PIXAR);
                return true;
            case R.id.action_rise:
                mGLSurfaceView.setFilter(MagicFilterType.RISE);
                return true;
            case R.id.action_sierra:
                mGLSurfaceView.setFilter(MagicFilterType.SIERRA);
                return true;
            case R.id.action_sutro:
                mGLSurfaceView.setFilter(MagicFilterType.SUTRO);
                return true;
            case R.id.action_toaster2:
                mGLSurfaceView.setFilter(MagicFilterType.TOASTER2);
                return true;
            case R.id.action_valencia:
                mGLSurfaceView.setFilter(MagicFilterType.VALENCIA);
                return true;
            case R.id.action_walden:
                mGLSurfaceView.setFilter(MagicFilterType.WALDEN);
                return true;
            case R.id.action_xproii:
                mGLSurfaceView.setFilter(MagicFilterType.XPROII);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLSurfaceView.stop();
    }
}
