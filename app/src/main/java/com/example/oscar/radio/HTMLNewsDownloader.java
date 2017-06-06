package com.example.oscar.radio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oszi on 6/6/17.
 */

public class HTMLNewsDownloader extends AsyncTask<Void, Void, List<News>>{
    @Override
    protected List<News> doInBackground(Void... params) {
        List<News> news = new ArrayList<News>();    // creating a new list

        try {
            // Connect to the web site
            Document document = Jsoup.connect("http://www.hitradio.hu/hiteleti_hirek").get();


            Elements rawNews = document.select("div.szoveg_o");
            Log.d("HTML", "doInBackground: " + rawNews.size());

            for (Element n : rawNews) {

                String urldisplay = n.select("div.kepkocka > a > img").attr("src");
                Bitmap mIcon11 = null;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }


                news.add(new News(
                        n.select("h3 > a").html(), // title
                        n.select("a.datum > span").html(), // date
                        n.select("a.szoveg_body").html(), // content
                        n.select("a.szoveg_body").attr("href")  // link
                ));
                news.get(news.size() - 1).setPicture(mIcon11);
/*
                Log.d("NEWS", "title: " + news.get(news.size() - 1).getTitle());
                Log.d("NEWS", "date: " + news.get(news.size() - 1).getDate());
                Log.d("NEWS", "content: " + news.get(news.size() - 1).getContent());
                Log.d("NEWS", "link: " + news.get(news.size() - 1).getLink());
                Log.d("NEWS", "");*/
            }


        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return news;
    }

    @Override
    protected void onPostExecute(final List<News> news){
        Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Globals.getInstance().news = news;

                    Globals.getInstance().news_test_1_1.setText(news.get(0).getTitle());
                    Globals.getInstance().news_test_1_2.setText(news.get(0).getDate());
                    Globals.getInstance().news_test_1_image.setImageBitmap(news.get(0).getPicture());

                    Globals.getInstance().news_test_2_1.setText(news.get(1).getTitle());
                    Globals.getInstance().news_test_2_2.setText(news.get(1).getDate());
                    Globals.getInstance().news_test_2_image.setImageBitmap(news.get(1).getPicture());

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Globals.getInstance().musicSwipeContainer.setRefreshing(false);
        } catch (NullPointerException e) {
            // it's ok
        }
    }
}
