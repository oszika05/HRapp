package com.example.oscar.radio;

import android.graphics.Bitmap;

/**
 * Created by oszi on 6/6/17.
 */

// TODO: open the full article

public class News {
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    String date;
    String picture;
    String content;
    String link;

    public String getFullArticle() { // TODO
        return "";
    }

    public News(String title, String date, String content, String picture, String link) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.picture = picture;
        this.link = link;
    }

}
