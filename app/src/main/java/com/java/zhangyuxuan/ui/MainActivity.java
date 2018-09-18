package com.java.zhangyuxuan.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.entity.NewsEntity;
import com.java.zhangyuxuan.newsList.NewsListFragment;
import com.java.zhangyuxuan.newsList.ViewPageAdapter;
import com.java.zhangyuxuan.rss.RssParser;
import com.java.zhangyuxuan.utils.DatabaseUtil;
import com.java.zhangyuxuan.utils.TagUtil;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements TabLayout.OnTabSelectedListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    private ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();

        get_view();

    }

    private void get_view() {
        String[] tags = TagUtil.tag;
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        ArrayList<Fragment> fragments = new ArrayList<>();
        int total = tags.length;
        SharedPreferences shared = getSharedPreferences("tag", MODE_PRIVATE);
        for (int i = 0; i < total; i++) {
            if (shared.getBoolean(tags[i], true)) {
                NewsListFragment fragment = NewsListFragment.newInstance(tags[i]);
                fragment.setOnRefreshListener(new NewsListFragment.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        final Handler handler = new Handler()
                        {
                            @Override
                            public void handleMessage(Message msg)
                            {
                                if(msg.what == 1)
                                    refresh();
                            }
                        };

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<NewsEntity> entityArrayList = RssParser.getNewsList();
                                DatabaseUtil.insertNews(MainActivity.this, entityArrayList);
                                Message msg = handler.obtainMessage(1);
                                handler.sendMessage(msg);
                            }
                        });
                        thread.start();
                    }
                });
                fragments.add(fragment);
            }
        }
        tabLayout.addOnTabSelectedListener(this);
        adapter.setFragments(fragments);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        int time = 0;
        String[] titles = TagUtil.tagCH;
        for (int i = 0; i < total; i++) {
            if (shared.getBoolean(tags[i], true)) {
                tabLayout.getTabAt(time++).setText(titles[i]);
            }
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("首页");
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        toolbar.inflateMenu(R.menu.main_menu);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.set_tab:
                        Intent tabIntent = new Intent(MainActivity.this,
                                SetTagActivity.class);
                        startActivityForResult(tabIntent, 2);
                        return true;
                    case R.id.search:
                        Intent searchIntent = new Intent(MainActivity.this,
                                SearchActivity.class);
                        startActivityForResult(searchIntent, 4);
                        return true;
                    case R.id.favorite:
                        Intent favIntent = new Intent(MainActivity.this,
                                FavoriteActivity.class);
                        startActivityForResult(favIntent, 3);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("setTag", false))
        {
            refresh();
        }
    }

    private void refresh()
    {
        String[] tags = TagUtil.tag;
        ArrayList<Fragment> fragments = new ArrayList<>();
        int total = tags.length;
        SharedPreferences shared = getSharedPreferences("tag", MODE_PRIVATE);
        for (int i = 0; i < total; i++) {
            if (shared.getBoolean(tags[i], true)) {
                NewsListFragment fragment = NewsListFragment.newInstance(tags[i]);
                fragment.setOnRefreshListener(new NewsListFragment.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        final Handler handler = new Handler()
                        {
                            @Override
                            public void handleMessage(Message msg)
                            {
                                if(msg.what == 1)
                                    refresh();
                            }
                        };

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<NewsEntity> entityArrayList = RssParser.getNewsList();
                                DatabaseUtil.insertNews(MainActivity.this, entityArrayList);
                                Message msg = handler.obtainMessage(1);
                                handler.sendMessage(msg);
                            }
                        });
                        thread.start();
                    }
                });
                fragments.add(fragment);
            }
        }
        adapter.setFragments(fragments);
        adapter.notifyDataSetChanged();
        int time = 0;
        String[] titles = TagUtil.tagCH;
        for (int i = 0; i < total; i++) {
            if (shared.getBoolean(tags[i], true)) {
                tabLayout.getTabAt(time++).setText(titles[i]);
            }
        }
    }

}

