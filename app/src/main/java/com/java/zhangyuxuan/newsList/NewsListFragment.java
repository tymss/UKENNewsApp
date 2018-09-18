package com.java.zhangyuxuan.newsList;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.entity.NewsEntity;
import com.java.zhangyuxuan.ui.NewsDetailActivity;
import com.java.zhangyuxuan.utils.DatabaseUtil;
import com.java.zhangyuxuan.utils.HttpUtil;

import java.util.ArrayList;

public class NewsListFragment extends Fragment{
    private ListView list_view;
    private View view;
    private String mType;
    private boolean isFavorite;
    private boolean isSearch;
    private int lastItem = 0;
    private NewsListAdapter madapter;
    private View footView;
    private String keyword;

    private ImageView img;
    private TextView tip;
    private int headHeight;
    private View headView;
    private int nowState = 3;
    private final int MOVE = 1;
    private final int FRESHABLE = 2;
    private final int UNTOUCHABEL = 3;
    private final int FRESHING = 4;
    private int firstItem;
    private double startY;

    public OnRefreshListener refreshListener;

    public void setOnRefreshListener(OnRefreshListener listener)
    {
        this.refreshListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_list_fragment, container, false);
        list_view = (ListView) view.findViewById(R.id.newslist);
        footView = inflater.inflate(R.layout.listfoot, null);
        headView = inflater.inflate(R.layout.listhead, null);
        img = (ImageView)headView.findViewById(R.id.refreshing_img);
        list_view.addHeaderView(headView, null, false);
        headHeight = (int)(50 * getActivity().getResources().getDisplayMetrics().density + 0.6);
        headView.setPadding(0, -headHeight, 0, 0);
        tip = (TextView)headView.findViewById(R.id.tips);
        final ArrayList<NewsEntity> listData = getDataByType(mType);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsEntity entity = listData.get(position - 1);
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("favorite", DatabaseUtil.isFavorite(getActivity(),
                        entity.getId()));
                intent.putExtra("link", entity.getLink());
                intent.putExtra("type", entity.getType());
                intent.putExtra("title", entity.getTitle());
                intent.putExtra("date", entity.getDate());
                intent.putExtra("author", entity.getAuthor());
                intent.putExtra("description", entity.getDescription());
                intent.putExtra("read", 1);
                intent.putExtra("id", entity.getId());
                DatabaseUtil.setRead(getActivity(), entity.getId());
                startActivityForResult(intent, 1);
                SharedPreferences shared = getActivity().getSharedPreferences("read",
                        Context.MODE_PRIVATE);
                int time = shared.getInt(entity.getType(), 0);
                SharedPreferences.Editor editor = shared.edit();
                editor.putInt(entity.getType(), time + 1);
                editor.apply();
                madapter.setRead(position - 1);
                madapter.notifyDataSetChanged();
                }
            });

        madapter = new NewsListAdapter(getActivity(), listData, isFavorite, isSearch);
        if(isSearch)
            madapter.setKey(keyword);
        list_view.setAdapter(madapter);
        madapter.notifyDataSetChanged();
        if(!isFavorite)
            setScroll();

        if(!isFavorite && !isSearch)
            setTouch();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getString("type");
        keyword = getArguments().getString("key", "");
    }

    public static NewsListFragment newInstance(String text) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("type", text);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewsListFragment newInstance(String text, String keyword) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putString("type", text);
        args.putString("key", keyword);
        fragment.setArguments(args);
        return fragment;
    }

    ArrayList<NewsEntity> getDataByType(String type) {
        ArrayList<NewsEntity> list;
        if (type.equals("FAV")) {
            isFavorite = true;
            isSearch = false;
            list = DatabaseUtil.getFavorite(getActivity());
        }else if (type.equals("SEARCH"))
        {
            isFavorite = false;
            isSearch = true;
            list = DatabaseUtil.getSearch(getActivity(), keyword);
        }
        else {
            isFavorite = false;
            isSearch = false;
            list = DatabaseUtil.getNewsByType(getActivity(), type);
        }
        return list;
    }

    public String getmType() {
        return mType;
    }

    private void setScroll()
    {
        final Handler handler = new Handler()
        {
            public void handleMessage(Message msg) {
                if(msg.what == 1)
                {
                    madapter.addCount();
                    madapter.notifyDataSetChanged();
                    list_view.removeFooterView(footView);
                }
            }
        };

        list_view.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(lastItem  == madapter.getCount() && !madapter.isAllLoaded()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE)
                {
                    list_view.addFooterView(footView, null, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendMessage(handler.obtainMessage(1));
                        }
                    }, 1500);
                }
                //else if(lastItem)
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount - 1;
                firstItem = firstVisibleItem;
            }
        });
    }

    private void setTouch()
    {
        list_view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(firstItem == 0)
                {
                    switch (event.getAction())
                    {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float nowY = event.getY();
                            double distance = nowY - startY;
                            if(distance <= 0 && nowState == UNTOUCHABEL)
                                return false;
                            nowState = MOVE;
                            if(nowState != FRESHING)
                            {
                                if(headView.getPaddingTop() >= 0) {
                                    nowState = FRESHABLE;
                                    img.setVisibility(View.GONE);
                                    tip.setText("释放刷新ヾ(^▽^*)))");
                                }
                                else
                                {
                                    nowState = MOVE;
                                    img.setVisibility(View.GONE);
                                    tip.setText("下拉刷新o(≧口≦)o");
                                }
                                headView.setPadding(0 , -headHeight + (int)(distance / 2),
                                        0, 0);
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                        default:
                            if(nowState == FRESHABLE) {
                                nowState = FRESHING;
                                headView.setPadding(0, 0, 0, 0);
                                if(HttpUtil.isNetAvailabel(getContext())) {

                                    img.setVisibility(View.VISIBLE);
                                    Animation animation = AnimationUtils.loadAnimation(getActivity(),  R.anim.rotate_anim);
                                    animation.setStartTime(Animation.START_ON_FIRST_FRAME);
                                    animation.setDuration(1000);
                                    img.startAnimation(animation);
                                    tip.setText("刷新中(/≧▽≦)/");
                                    refreshListener.onRefresh();
                                }
                                else
                                {
                                    headView.setPadding(0, -headHeight, 0, 0);
                                    Toast toast = Toast.makeText(getContext(), null, Toast.LENGTH_SHORT);
                                    toast.setText("没有网络连接哦（；´д｀）ゞ");
                                    toast.show();
                                    nowState = UNTOUCHABEL;
                                }
                            }else if(nowState == MOVE) {
                                nowState = UNTOUCHABEL;
                                img.setVisibility(View.GONE);
                                tip.setText("下拉刷新o(≧口≦)o");
                                headView.setPadding(0, -headHeight, 0, 0);
                            }
                    }
                }
                return false;
            }
        });
    }

    public interface OnRefreshListener
    {
        void onRefresh();
    }
}
