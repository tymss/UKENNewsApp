package com.java.zhangyuxuan.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.history.HistoryAdapter;
import com.java.zhangyuxuan.newsList.NewsListFragment;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SearchView searchView;
    private ListView listView;
    private HistoryAdapter adapter;
    private View footView;
    private View headView;

    @Override
    protected void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_search);

        initToolbar();
        initListView();
    }

    private void initToolbar()
    {
        toolbar = (Toolbar)findViewById(R.id.search_toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.inflateMenu(R.menu.search_menu);
        initSearchView();
    }

    private void initSearchView()
    {
        MenuItem item = toolbar.getMenu().findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("请输入关键词");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                search(s);
                listView.setVisibility(View.INVISIBLE);
                addToHistory(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s!=null)
                    showHistory(s);
                return false;
            }
        });
    }

    private void search(String key)
    {
        NewsListFragment fragment = NewsListFragment.newInstance("SEARCH", key);
        getSupportFragmentManager().beginTransaction().replace(R.id.search_result,
                fragment).commit();
    }

    private void initListView()
    {
        listView = (ListView)findViewById(R.id.history_list);
        adapter = new HistoryAdapter(SearchActivity.this);
        listView.setAdapter(adapter);
        footView = getLayoutInflater().inflate(R.layout.history_footer, null);
        headView = getLayoutInflater().inflate(R.layout.history_header, null);
        listView.addHeaderView(headView, null, false);
        listView.addFooterView(footView, null, true);
        showHistory("");
    }

    private void showHistory(String key)
    {
        listView.setVisibility(View.VISIBLE);
        final ArrayList<String> history = getHistory(key);
        final int num = history.size();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == num + 1)
                {
                    clearHistory();
                    showHistory("");
                }
                else
                {
                    searchView.setQuery(history.get(position-1), false);
                }
            }
        });
        adapter.setHistory(history);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<String> getHistory(String key)
    {
        SharedPreferences shared = getSharedPreferences("history", MODE_PRIVATE);
        int num = shared.getInt("num", 0);
        ArrayList<String> list = new ArrayList<>();
        for(int i = num - 1; i >= 0; i--)
        {
            String his = shared.getString(String.valueOf(i),"");
            if(his.contains(key))
                list.add(his);
        }
        return list;
    }

    private void addToHistory(String key)
    {
        SharedPreferences shared = getSharedPreferences("history", MODE_PRIVATE);
        int num = shared.getInt("num", 0);
        SharedPreferences.Editor editor = shared.edit();
        editor.putString(String.valueOf(num), key);
        editor.putInt("num", num + 1);
        editor.apply();
    }

    private void clearHistory()
    {
        SharedPreferences shared = getSharedPreferences("history", MODE_PRIVATE);
        int num = shared.getInt("num", 0);
        SharedPreferences.Editor editor = shared.edit();
        for(int i = 0; i < num; i++)
            editor.remove(String.valueOf(i));
        editor.putInt("num", 0);
        editor.commit();
    }
}
