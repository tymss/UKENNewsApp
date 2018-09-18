package com.java.zhangyuxuan.rss;

import android.util.Log;

import com.java.zhangyuxuan.entity.NewsEntity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class RssParser {
    public static ArrayList<NewsEntity> getNewsList() throws RuntimeException {
        RssAddress addr = new RssAddress();
        ArrayList<NewsEntity> newsList = new ArrayList<>();
        ArrayList<ParseThread> threadPool = new ArrayList<>();
        HashMap<String, String> addrMap = RssAddress.getRssMap();
        Set<String> set = addrMap.keySet();
        for (String key : set)
        {
            ParseThread thread = new ParseThread(addrMap.get(key), key);
            threadPool.add(thread);
            thread.start();
        }
        int num = threadPool.size();
        while(true)
        {
            boolean alive = false;
            for(int i = 0; i < num; i++)
            {
                if(threadPool.get(i).isAlive()) {
                    alive = true;
                    break;
                }
            }
            if(!alive)
                break;
        }
        for(int i = 0; i < num; i++)
        {
            newsList.addAll(threadPool.get(i).getList());
        }
        return newsList;
    }
}

class SAXhandler extends DefaultHandler
{
    private String tag;
    ArrayList<NewsEntity> locallist;
    private String title;
    private String link;
    private String author;
    private String date;
    private String description;
    private boolean inItem;
    private String type;

    SAXhandler(String _type, ArrayList<NewsEntity> list)
    {
        tag = null;
        locallist = list;
        title = "";
        link = "";
        author = "";
        date = "";
        description = "";
        inItem = false;
        type = _type;
    }

    public void startDocument() throws SAXException{
        super.startDocument();
    }
    public void endDocument() throws SAXException{
        super.endDocument();
    }
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException
    {
        tag = qName;
        if("item".equals(qName))
            inItem = true;
        super.startElement(uri, localName, qName, attributes);
    }
    public void endElement(String uri, String localName, String qName) throws
            SAXException{
        tag = null;
        if("item".equals(qName)) {
            inItem = false;
            locallist.add(new NewsEntity(type, title, link, author, date, description));
            title = "";
            link = "";
            author = "";
            date = "";
            description = "";
        }
        super.endElement(uri, localName, qName);
    }
    public void characters(char[] ch, int st, int length)
    {
        if(inItem)
        {
           if("title".equals(tag))
               title += new String(ch, st, length);
           if("link".equals(tag))
               link += new String(ch, st, length);
           if("author".equals(tag))
               author += new String(ch, st, length);
           if("pubDate".equals(tag))
               date += new String(ch, st, length);
           if("description".equals(tag))
               description += new String(ch, st, length);
        }
    }
}

class ParseThread extends Thread
{
    private ArrayList<NewsEntity> list;
    private String _url;
    private String key;
    public ParseThread(String url, String key)
    {
        this._url = url;
        this.key = key;
        list = new ArrayList<>();
    }

    public ArrayList<NewsEntity> getList() {
        return list;
    }

    @Override
    public void run()
    {
        try
        {
            Log.d("db", "start parsing TYPE = " + key);
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            if(connection.getResponseCode() == 200)
            {
                InputStream is = connection.getInputStream();
                SAXParserFactory saxParserFactory=SAXParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                SAXhandler handler = new SAXhandler(key, list);
                saxParser.parse(is, handler);
                list = handler.locallist;
                is.close();
            }
            connection.disconnect();
            Log.d("db", "finish parsing TYPE = " + key);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}