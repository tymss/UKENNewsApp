package com.java.zhangyuxuan.rss;

import java.util.HashMap;

public class RssAddress {
    private static HashMap<String, String> addr;
    public RssAddress()
    {
        addr = new HashMap<>();
        addr.put("GJ", "http://news.qq.com/newsgj/rss_newswj.xml");
        addr.put("SH", "http://news.qq.com/newssh/rss_newssh.xml");
        addr.put("DY", "http://ent.qq.com/movie/rss_movie.xml");
        addr.put("DS", "http://ent.qq.com/tv/rss_tv.xml");
        addr.put("MX", "http://ent.qq.com/newxw/rss_start.xml");
        addr.put("YY", "http://ent.qq.com/m_news/rss_yinyue.xml");
        addr.put("ZQ", "http://finance.qq.com/stock/zhqxw/rss_zqxw.xml");
        addr.put("TY", "http://sports.qq.com/others/rss_others.xml");
        addr.put("YX", "http://games.qq.com/ntgame/rss_ntgame.xml");
        addr.put("DM", "http://comic.qq.com/news/rss_news.xml");
        addr.put("XZ", "http://astro.fashion.qq.com/fash/rss_fash.xml");
    }
    public static HashMap<String, String> getRssMap()
    {
        return addr;
    }
}
