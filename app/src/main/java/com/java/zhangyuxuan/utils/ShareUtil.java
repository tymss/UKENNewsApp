package com.java.zhangyuxuan.utils;

import android.content.Context;
import android.content.Intent;

import com.java.zhangyuxuan.entity.NewsEntity;

public class ShareUtil {
    public static void shareNews(Context context, NewsEntity entity)
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, entity.getTitle());
        String text = entity.getDescription() + "\n" + entity.getLink();
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, "分享"));
    }
}
