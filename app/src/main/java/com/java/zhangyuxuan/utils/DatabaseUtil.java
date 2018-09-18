package com.java.zhangyuxuan.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.java.zhangyuxuan.database.FavoriteDatabaseHelper;
import com.java.zhangyuxuan.database.NewsDatabaseHelper;
import com.java.zhangyuxuan.entity.NewsEntity;

import java.util.ArrayList;
import java.util.Collections;

public class DatabaseUtil {
    private static final int MAX_LIST_NUM = 20;

    public static void insertNews(Context context, ArrayList<NewsEntity> newsList)
    {
        Log.d("db", "start inserting news into database");
        NewsDatabaseHelper helper = new NewsDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        int num = newsList.size();
        for (int i = num - 1; i >= 0; i--) {
            NewsEntity news = newsList.get(i);
            String sql = "select * from news where title=?";
            Cursor cursor = db.rawQuery(sql, new String[]{news.getTitle()});
            if (cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put("type", news.getType());
                values.put("title", news.getTitle());
                values.put("link", news.getLink());
                values.put("author", news.getAuthor());
                values.put("date", news.getDate());
                values.put("description", news.getDescription());
                values.put("read", news.getRead());
                db.insert("news", null, values);
            }
        }
        Log.d("db", "finish inserting news into database");
        db.close();
    }

    public static void setRead(Context context, int id)
    {
        NewsDatabaseHelper helper = new NewsDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "update news set read = 1 where id = " + id;
        db.execSQL(sql);
        db.close();
    }

    public static ArrayList<NewsEntity> getNewsByType(Context context, String type)
    {
        Log.d("db", "start get news list of TYPE: " + type);
        NewsDatabaseHelper helper = new NewsDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NewsEntity> list = new ArrayList<>();
        if(!type.equals("REC"))
        {
            String sql = "select * from news where type = ? order by id";
            Cursor cursor = db.rawQuery(sql, new String[]{type});
            int time = 0;
            if(cursor.moveToLast())
            {
                while (true) {
                    time++;
                    NewsEntity entity = new NewsEntity(cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(3),
                            cursor.getString(4),
                            cursor.getString(5),
                            cursor.getString(6));
                    entity.setId(cursor.getInt(0));
                    entity.setRead(cursor.getInt(7));
                    list.add(entity);
                    if (time == MAX_LIST_NUM || !cursor.moveToPrevious())
                        break;
                }
            }
            cursor.close();
        }
        else    //get recommend list
        {
            SharedPreferences shared = context.getSharedPreferences("read",
                    Context.MODE_PRIVATE);
            String[] tags = TagUtil.tag;
            int total = tags.length;
            int[] read = new int[total];
            for(int i = 0; i < total; i++)
                read[i] = shared.getInt(tags[i], 0);
            double[] prob = MathUtil.getSoftmax(read);
            int[] time = new int[total];
            for(int i = 0; i < MAX_LIST_NUM; i++)
            {
                double random = Math.random();
                time[MathUtil.getRussianNum(prob, random)] += 1;
            }
            for(int i = 0; i < total; i++)
            {

                String tag = tags[i];
                String sql = "select * from news where read = 0 and type = '" + tag + "' order by" +
                        " random() limit " + time[i];
                Cursor cursor = db.rawQuery(sql, null);
                if(cursor.getCount() != 0) {
                    while (cursor.moveToNext()) {
                        NewsEntity entity = new NewsEntity(cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(6));
                        entity.setId(cursor.getInt(0));
                        entity.setRead(0);
                        list.add(entity);
                    }
                }
                cursor.close();
            }
            Collections.shuffle(list);
        }
        db.close();
        Log.d("db", "finish get news list of TYPE: " + type);
        return list;
    }

    public static void addToFavorite(Context context, NewsEntity news)
    {
        Log.d("db", "Add news: id = " + news.getId() + "  to favorite");
        FavoriteDatabaseHelper helper = new FavoriteDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", news.getId());
        values.put("type", news.getType());
        values.put("title", news.getTitle());
        values.put("link", news.getLink());
        values.put("author", news.getAuthor());
        values.put("date", news.getDate());
        values.put("description", news.getDescription());
        values.put("read", news.getRead());
        db.insert("news", null, values);
        db.close();
    }

    public static void removeFromFavorite(Context context, int id)
    {
        Log.d("db", "remove news: id = " + id + "  from favorite");
        FavoriteDatabaseHelper helper = new FavoriteDatabaseHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "delete from news where id = " + id;
        db.execSQL(sql);
        db.close();
    }

    public static boolean isFavorite(Context context, int id)
    {
        FavoriteDatabaseHelper helper = new FavoriteDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select * from news where id = " + id;
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.getCount() != 0)
        {
            cursor.close();
            db.close();
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public static ArrayList<NewsEntity> getFavorite(Context context)
    {
        FavoriteDatabaseHelper helper = new FavoriteDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NewsEntity> newsList = new ArrayList<>();
        String sql = "select * from news";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToLast()) {
            while (true) {
                NewsEntity entity = new NewsEntity(cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),
                        cursor.getString(6));
                entity.setId(cursor.getInt(0));
                entity.setRead(cursor.getInt(7));
                newsList.add(entity);
                if(!cursor.moveToPrevious())
                    break;
            }
        }
        cursor.close();
        db.close();
        return newsList;
    }

    public static ArrayList<NewsEntity> getSearch(Context context, String keyword)
    {
        NewsDatabaseHelper helper = new NewsDatabaseHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<NewsEntity> list = new ArrayList<>();
        String sql = "select * from news where title like ? or description like ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"%" + keyword + "%", "%" + keyword + "%"});
        while(cursor.moveToNext())
        {
            NewsEntity entity = new NewsEntity(cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6));
            entity.setId(cursor.getInt(0));
            entity.setRead(cursor.getInt(7));
            list.add(entity);
        }
        cursor.close();
        db.close();
        return list;
    }
}
