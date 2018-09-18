package com.java.zhangyuxuan.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.zhangyuxuan.R;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<String> history;

    public HistoryAdapter(Context context)
    {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        history = new ArrayList<>();
    }

    public void setHistory(ArrayList<String> history)
    {
        this.history = history;
    }

    public class ItemView
    {
        public TextView historyText;
    }

    @Override
    public int getCount()
    {
        return history.size();
    }

    @Override
    public Object getItem(int position)
    {
        return history.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ItemView itemView = null;
        if(convertView == null)
        {
            itemView = new ItemView();
            convertView = layoutInflater.inflate(R.layout.history_list, null);
            itemView.historyText = (TextView)convertView.findViewById(R.id.history);
            convertView.setTag(itemView);
        }
        else
        {
            itemView = (ItemView)convertView.getTag();
        }
        itemView.historyText.setText(history.get(position));
        return convertView;
    }

}
