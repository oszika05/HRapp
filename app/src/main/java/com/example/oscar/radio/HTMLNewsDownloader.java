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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by oszi on 6/6/17.
 */

public class HTMLNewsDownloader extends AsyncTask<Void, Void, List<News>>{

    private class NewsComparator implements Comparator<News> {
        @Override
        public int compare(News a, News b) {
            return a.getDate().compareToIgnoreCase(b.getDate());
        }
    }

    @Override
    protected List<News> doInBackground(Void... params) {
        List<News> news = new ArrayList<News>();    // creating a new list
        HashMap<String, String> links = new HashMap<String, String>();
        links.put("Hitéleti hír", "http://www.hitradio.hu/hiteleti_hirek");
        links.put("Rádiós hír", "http://www.hitradio.hu/radios_hirek");

        for (Map.Entry<String, String> entry : links.entrySet()) {

            try {
                // Connect to the web site
                Document document = Jsoup.connect(entry.getValue()).get();


                Elements rawNews = document.select("div.szoveg_o");
                Log.d("HTML", "doInBackground: " + rawNews.size());

                for (Element n : rawNews) {
                    news.add(new News(
                            n.select("h3 > a").html(), // title
                            n.select("a.datum > span").html(), // date
                            n.select("a.szoveg_body").html(), // content
                            n.select("div.kepkocka > a > img").attr("src"), // picture
                            n.select("a.szoveg_body").attr("href"),  // link
                            entry.getKey()
                    ));
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
        }

        Collections.sort(news, new NewsComparator());
        Collections.reverse(news);

        return news;
    }

    @Override
    protected void onPostExecute(final List<News> news) {
        Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Globals.getInstance().news = news;
                    Log.d("Dfinished", "run: " + "aaaaaaa:\t" + news.size());
                    Globals.getInstance().newsAdapter.refresh((ArrayList<News>) news);
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
