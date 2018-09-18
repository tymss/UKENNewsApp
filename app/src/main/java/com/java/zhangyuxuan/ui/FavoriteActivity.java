package com.java.zhangyuxuan.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.newsList.NewsListFragment;

public class FavoriteActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private NewsListFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_favorite);

        initToolbar();

        fragment = NewsListFragment.newInstance("FAV");
        getSupportFragmentManager().beginTransaction().
                replace(R.id.favorite_list, fragment).commit();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.favorite_toolbar);
        toolbar.setTitle("收藏");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        fragment = NewsListFragment.newInstance("FAV");
        getSupportFragmentManager().beginTransaction().
                replace(R.id.favorite_list, fragment).commit();
    }
}
