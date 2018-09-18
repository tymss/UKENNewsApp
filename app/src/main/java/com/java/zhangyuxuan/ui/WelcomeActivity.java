package com.java.zhangyuxuan.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.rss.RssParser;
import com.java.zhangyuxuan.utils.DatabaseUtil;
import com.java.zhangyuxuan.utils.HttpUtil;

public class WelcomeActivity extends Activity {
    private static int REQUEST_EXTERNAL_STRONGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        getFilePermission();

        initAnim();

        welcomeHandle();

    }

    private void getFilePermission()
    {
        if (ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS},
                    REQUEST_EXTERNAL_STRONGE);
        }
    }

    private void welcomeHandle()
    {
        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 2)
                {
                    Intent intent = new Intent(WelcomeActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(msg.what == 1)
                {
                    Toast toast = Toast.makeText(WelcomeActivity.this,
                            null, Toast.LENGTH_SHORT);
                    toast.setText("没有网络连接哦（；´д｀）ゞ");
                    toast.show();
                    Intent intent = new Intent(WelcomeActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(!HttpUtil.isNetAvailabel(getApplicationContext()))
                {
                    Message msg = handler.obtainMessage(1);
                    handler.sendMessage(msg);
                }
                else
                {
                    DatabaseUtil.insertNews(WelcomeActivity.this, RssParser.getNewsList());
                    Message msg = handler.obtainMessage(2);
                    handler.sendMessage(msg);
                }
            }
        });

        thread.start();
    }

    private void initAnim()
    {
        Animation animation = AnimationUtils.loadAnimation(this,  R.anim.rotate_anim);
        animation.setStartTime(Animation.START_ON_FIRST_FRAME);
        animation.setDuration(1000);
        ImageView img = (ImageView)findViewById(R.id.load_img);
        img.startAnimation(animation);
    }
}
