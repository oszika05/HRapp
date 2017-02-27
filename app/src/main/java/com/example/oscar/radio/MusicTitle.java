package com.example.oscar.radio;

import android.util.Log;


public class MusicTitle {

    private String time;
    private String date;
    private String title;
    private String link;


    public String getTime() {
        return time;
    }
    public String getDate() {
        return date;
    }

    public String getTitle() {
        try {
            return title.substring(title.indexOf("-") + 2);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return title;
        }
    }
    public String getArtist() {
        try {
            return title.substring(0, title.indexOf("-") - 1);
        } catch (StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            Log.d("TITLE", "getArtist: " + title);
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }
    public String getLink() {
        return link;
    }


    public void setDate(String raw) {
        try {

            date = raw.substring(0, 10);
            time = raw.substring(11, 16);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            time = "";
            date = "";
        }
    }
}
