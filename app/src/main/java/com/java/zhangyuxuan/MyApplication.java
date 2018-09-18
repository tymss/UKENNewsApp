package com.java.zhangyuxuan;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.java.zhangyuxuan.utils.TagUtil;

public class MyApplication extends Application {
    @Override
    public void onCreate()
    {
        SharedPreferences shared = getSharedPreferences("first", MODE_PRIVATE);
        boolean isfirst = shared.getBoolean("isfirst", true);
        if(isfirst)
        {
            Editor editor = shared.edit();
            editor.putBoolean("isfirst", false);
            editor.apply();
            createTag();
        }
        super.onCreate();
    }

    private void createTag() {
        SharedPreferences shared = getSharedPreferences("tag", MODE_PRIVATE);
        Editor editor = shared.edit();
        SharedPreferences readShare = getSharedPreferences("read", MODE_PRIVATE);
        Editor editor1 = readShare.edit();
        SharedPreferences hisShare = getSharedPreferences("history", MODE_PRIVATE);
        Editor editor2 = hisShare.edit();
        editor2.putInt("num", 0);
        int total = TagUtil.tag.length;
        for (int i = 0; i < total; i++)
        {
            editor.putBoolean(TagUtil.tag[i], true);
            editor1.putInt(TagUtil.tag[i], 0);
        }
        editor.commit();
        editor1.commit();
        editor2.commit();
    }
}
