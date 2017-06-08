package com.example.oscar.radio;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    String type;

    public String getFullArticle(TextView tv) { // TODO

        Pair a = new Pair();
        a.link = link;
        a.textView  = tv;
        new ArticleGetter().execute(a);

        return "";
    }

    public News(String title, String date, String content, String picture, String link, String type) {
        this.title = title;
        this.date = date;
        this.content = content;
        this.picture = picture;
        this.link = link;
        this.type = type;
    }

    private class Pair {
        public String link;
        public TextView textView;
        // pulic
    }

    private class ArticleGetter extends AsyncTask<Pair, Void, String> {

        @Override
        protected String doInBackground(final Pair... params) {
            String link = params[0].link;
            Document document;
            String articleContent = "";

            try {
                // Connect to the web site
                try {
                    document = Jsoup.connect("http://www.hitradio.hu" + link).get();
                } catch (java.lang.RuntimeException e) {
                    e.printStackTrace();
                    return "";
                }

                Elements content = document.select("div.field-name-body > div.field-items > div > p");
                for(Element a : content) {
                    articleContent += a.html() + "<br /><br />";
                }

                Log.d("HTML", "doInBackground: " + articleContent);


            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }

            final String fStr = articleContent;

            Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    params[0].textView.setText(Html.fromHtml("<br /><b>" + params[0].textView.getText() + "</b>" + "<br /><br />" + fStr));
                }
            });

            return articleContent;
        }

        @Override
        protected void onPostExecute(String str) {

        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()) {
            return this.link.equals(((News) obj).link);
        } else {
            return false;
        }
    }
}
