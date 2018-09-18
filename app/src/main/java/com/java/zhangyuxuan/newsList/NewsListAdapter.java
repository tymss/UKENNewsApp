package com.java.zhangyuxuan.newsList;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.zhangyuxuan.R;
import com.java.zhangyuxuan.entity.NewsEntity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewsListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<NewsEntity> newsList;
    private boolean isFavorite;
    private boolean isSearch;
    private int count;
    private String key;

    public NewsListAdapter(Context _context, ArrayList<NewsEntity> list,
                           boolean _isFavorite, boolean _isSearch)
    {
        context = _context;
        newsList = list;
        layoutInflater = LayoutInflater.from(_context);
        isFavorite = _isFavorite;
        isSearch = _isSearch;
        count = list.size() < 6 ? list.size() : 6;
    }

    public final class ItemView
    {
        public TextView title;
        public TextView author;
        public TextView pubdate;
    }

    public int getCount()
    {
        return count;
    }

    public Object getItem(int position)
    {
        return newsList.get(position);
    }

    public long getItemId(int position)
    {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        ItemView itemView = null;
        if(convertView == null)
        {
            itemView = new ItemView();
            convertView = layoutInflater.inflate(R.layout.news_list, null);
            itemView.title = (TextView)convertView.findViewById(R.id.title);
            itemView.author = (TextView)convertView.findViewById(R.id.author);
            itemView.pubdate = (TextView)convertView.findViewById(R.id.pubdate);
            convertView.setTag(itemView);
        }
        else
        {
            itemView = (ItemView)convertView.getTag();
        }
        NewsEntity news = newsList.get(position);
        itemView.author.setTextColor(context.getResources().getColor(R.color.readColor));
        itemView.pubdate.setTextColor(context.getResources().getColor(R.color.readColor));
        itemView.author.setText(news.getAuthor());
        itemView.pubdate.setText(news.getDate());

        if (news.getRead() == 1 && !isFavorite)
            itemView.title.setTextColor(context.getResources().getColor(R.color.readColor));
        else
            itemView.title.setTextColor(context.getResources().getColor(R.color.textColor));
        if(!isSearch)
            itemView.title.setText(news.getTitle());
        else
        {
            String ori = news.getTitle();
            SpannableString s = new SpannableString(ori);
            Pattern p = Pattern.compile(key);
            Matcher m = p.matcher(s);
            while(m.find())
            {
                s.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.keyColor)),
                        m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            itemView.title.setText(s);
        }


        return convertView;
    }

    public void addCount()
    {
        count = (count + 6 < newsList.size()) ? count + 6 : newsList.size();
    }

    public void setRead(int index)
    {
        newsList.get(index).setRead(1);
    }

    public boolean isAllLoaded()
    {
        return count == newsList.size();
    }

    public void setKey(String k)
    {
        key = k;
    }
}
