package com.xiuyukeji.filterrecord;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * 主界面
 *
 * @author Created by jz on 2017/5/2 17:01
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findView();
        initView();
        setListener();
    }

    private void findView() {
        mToolbar = findViewById(R.id.toolbar);
        mButton = findViewById(R.id.button);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
    }

    private void setListener() {
        mButton.setOnClickListener(v -> {
            new RxPermissions(this)
                    .request(Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) {
                            Intent intent = new Intent(this, RecordActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "录制权限被拒绝！", Toast.LENGTH_LONG).show();
                        }
                    }, Throwable::printStackTrace);
        });
    }
}
