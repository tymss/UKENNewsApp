package com.java.zhangyuxuan.entity;

public class NewsEntity {
    private int id;
    private String type;
    private String title;
    private String link;
    private String author;
    private String date;
    private String description;
    private int read;

    public NewsEntity(String _type, String _title, String _link, String _author,
                      String _date, String _description)
    {
        type = _type;
        title = _title;
        link = _link;
        author = _author;
        date = _date;
        description = _description;
        read = 0;
    }

    public int getId() { return id; }
    public void setId(int _id) { id = _id; }
    public String getType() { return type; }
    public String getTitle() { return title; }
    public String getLink() { return link; }
    public String getAuthor() { return author; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public int getRead() { return read; }
    public void setRead(int _read) { read = _read; }

    public void print() { System.out.println(type + title + link + author + date + description); }
}
