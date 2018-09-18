package com.java.zhangyuxuan.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.support.v7.widget.Toolbar;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.entity.NewsEntity;
import com.java.zhangyuxuan.utils.DatabaseUtil;
import com.java.zhangyuxuan.utils.HttpUtil;
import com.java.zhangyuxuan.utils.ShareUtil;

import java.io.File;

public class NewsDetailActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private NewsEntity newsEntity;
    private boolean favorite;
    private String url;
    private Toolbar toolbar;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);
        webView = (WebView)findViewById(R.id.webview);

        Intent intent = getIntent();
        favorite = intent.getExtras().getBoolean("favorite");
        url = intent.getExtras().getString("link");

        newsEntity = new NewsEntity(intent.getExtras().getString("type"),
                intent.getExtras().getString("title"),
                intent.getExtras().getString("link"),
                intent.getExtras().getString("author"),
                intent.getExtras().getString("date"),
                intent.getExtras().getString("description"));
        newsEntity.setId(intent.getExtras().getInt("id"));
        newsEntity.setRead(intent.getExtras().getInt("read"));

        initToolbar();

        webView.setWebViewClient(webViewClient);
        webView.setWebChromeClient(webChromeClient);
        webView.loadUrl(url);

        WebSettings webSettings = webView.getSettings();
        webSettings.setDomStorageEnabled(true);
        if(HttpUtil.isNetAvailabel(getApplicationContext()))
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        else
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
    }

    private WebViewClient webViewClient=new WebViewClient()
    {
        public void onPageFinished(WebView view, String _url)
        {
            if(_url.endsWith("htm"))
            {
                if(favorite) {
                    String path = Environment.getExternalStorageDirectory() + "/downloadedPages/";
                    File file = new File(path, "downloadPage_id" + newsEntity.getId() + ".mht");
                    if(file.exists()) {
                        url = "file:///" + path + "downloadPage_id" + newsEntity.getId() + ".mht";
                        Log.d("db", url);
                        webView.loadUrl(url);
                    }
                }
            }
            progressBar.setVisibility(View.GONE);
        }

        public void onPageStarted(WebView view, String _url, Bitmap favicon)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    private WebChromeClient webChromeClient = new WebChromeClient()
    {
        public void onReceivedTitle(WebView view, String title)
        {
            super.onReceivedTitle(view, title);
        }
        public void onProgressChanged(WebView view, int newProgress)
        {
            progressBar.setProgress(newProgress);
        }
    };

    @JavascriptInterface
    public void getClient(String str) {
        Log.i("js", "html getclient"); }

    public void onDestroy()
    {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
        webView = null;
    }

    private void initToolbar()
    {
        toolbar = (Toolbar)findViewById(R.id.webview_toolbar);
        toolbar.inflateMenu(R.menu.webview_menu);
        toolbar.setTitle("新闻详情");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        if(favorite)
            toolbar.getMenu().findItem(R.id.set_favorite).setIcon(getResources().
                    getDrawable(R.drawable.ic_fav_on));
        else
            toolbar.getMenu().findItem(R.id.set_favorite).setIcon(getResources().
                    getDrawable(R.drawable.ic_fav_not));
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.set_favorite:
                        if(favorite)
                        {
                            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_fav_not));
                            favorite = false;
                            DatabaseUtil.removeFromFavorite(NewsDetailActivity.this,
                                    newsEntity.getId());
                            deletePage();
                        }
                        else
                        {
                            menuItem.setIcon(getResources().getDrawable(R.drawable.ic_fav_on));
                            favorite = true;
                            DatabaseUtil.addToFavorite(NewsDetailActivity.this,
                                    newsEntity);
                            if(HttpUtil.isNetAvailabel(getApplicationContext()));
                            {
                                downloadPage();
                            }
                        }
                        return true;
                    case R.id.share:
                        ShareUtil.shareNews(NewsDetailActivity.this, newsEntity);
                        return true;
                    default:
                        return false;
                }

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void deletePage()
    {
        try {
            String path = Environment.getExternalStorageDirectory() + "/downloadedPages";
            Log.d("db", "delete page id = " + newsEntity.getId() + "  into: "
                    + path);
            File file = new File(path, "downloadPage_id" + newsEntity.getId() + ".mht");
            if (file.exists())
                file.delete();
        }catch (Exception e) {}
    }

    private void downloadPage()
    {
        try {
            String path = Environment.getExternalStorageDirectory() + "/downloadedPages";
            Log.d("db", "download page id = " + newsEntity.getId() + "  into: "
                    + path);
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdir();
            File file = new File(path, "downloadPage_id" + newsEntity.getId() + ".mht");
            webView.saveWebArchive(file.getAbsolutePath());
        }catch (Exception e) {}
    }

}
