package com.example.oscar.radio;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
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

public class HTMLNewsDownloader extends AsyncTask<Integer, Void, List<List<News>>>{

    private class NewsComparator implements Comparator<News> {
        @Override
        public int compare(News a, News b) {
            return b.getDate().compareToIgnoreCase(a.getDate());
        }
    }

    private List<News> mergeNews(List<List<News>> newsRaw) {
        List<News> news = new ArrayList<News>();
        for(List<News> it : newsRaw) {
            news.addAll(it);
        }

        Collections.sort(news, new NewsComparator());

        return news;
    }

    private List<News> mergeNews(List<News> news1, List<News> news2) {
        if(news2 == null) {
            return news1;
        }

        int to = news1.indexOf(news2.get(0));
        Log.d("TO", "mergeNews: TO:\t" + to);
        if (to < 0) {
            news1.addAll(news2);
            return news1;
        }

        news2.addAll(0, news1.subList(0, to));

        return news2;
    }

    private int newNews(List<News> news, int index) {
        try {
            return Globals.getInstance().newsRaw.get(index).get(0).getDate().compareTo(news.get(0).getDate());
        } catch (IndexOutOfBoundsException e) {
            return -1;
        }
    }

    private List<News> getNews(String what, String url) {
        List<News> news = new ArrayList<News>();
        try {
            // Connect to the web site
            Document document = Jsoup.connect(url).get();


            Elements rawNews = document.select("div.szoveg_o");
            Log.d("HTML", "doInBackground: " + rawNews.size());

            for (Element n : rawNews) {
                news.add(new News(
                        n.select("h3 > a").html(), // title
                        n.select("a.datum > span").html(), // date
                        n.select("a.szoveg_body").html(), // content
                        n.select("div.kepkocka > a > img").attr("src"), // picture
                        n.select("a.szoveg_body").attr("href"),  // link
                        what
                ));
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return news;
    }

    @Override
    protected List<List<News>> doInBackground(Integer[] params) {
        int page = params[0];
        List<News> news = new ArrayList<News>();    // creating a new list
        List<List<News>> newsArray = new ArrayList<List<News>>();

        Log.d("PAGE", "doInBackground: page: " + page);

        newsArray.add(getNews("Hitéleti hír", "http://www.hitradio.hu/hiteleti_hirek?page=" + page));
        newsArray.add(getNews("Rádiós hír", "http://www.hitradio.hu/radios_hirek?page=" + page));

        return newsArray;
    }

    @Override
    protected void onPostExecute(final List<List<News>> finalNewsRaw) {
        final int resultsPerPage = 12;
        Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                List<List<News>> newsRaw = finalNewsRaw.subList(0, finalNewsRaw.size());
                boolean hasSomethingChanged = false;

                try {
                    /*
                    if (Globals.getInstance().news.size() < 2) {

                    }*/

                    for (int i = 0; i < 2; i++) {
                        if (newNews(newsRaw.get(i), i) < 0) { // got fresh news, add them to the top
                            Log.d("ASYNC", "run: FRESH");
                            if (Globals.getInstance().newsRaw.size() > i)
                                Globals.getInstance().newsRaw.set(i, mergeNews(newsRaw.get(i), Globals.getInstance().newsRaw.get(i)));
                            else
                                Globals.getInstance().newsRaw.add(mergeNews(newsRaw.get(i), null));
                            hasSomethingChanged = true;
                        } else if (newNews(newsRaw.get(i), i) > 0) { // got fresh news, add them to the top
                            Log.d("ASYNC", "run: NOT FRESH");
                            if (Globals.getInstance().newsRaw.size() > i)
                                Globals.getInstance().newsRaw.set(i, mergeNews(Globals.getInstance().newsRaw.get(i), newsRaw.get(i)));
                            else
                                Globals.getInstance().newsRaw.add(mergeNews(newsRaw.get(i), null));

                            hasSomethingChanged = true;
                        }
                    }

                    if(hasSomethingChanged) {
                        Globals.getInstance().news = mergeNews(Globals.getInstance().newsRaw);
                        Globals.getInstance().newsAdapter[0].refresh((ArrayList<News>) Globals.getInstance().newsRaw.get(0));
                        Globals.getInstance().newsAdapter[1].refresh((ArrayList<News>) Globals.getInstance().newsRaw.get(1));
                        int s = Globals.getInstance().news.size() < 5 ? Globals.getInstance().news.size() : 5;
                        Log.d("S", "run: S: " + s);
                        Globals.getInstance().mainNewsAdapter.refresh((ArrayList<News>) Globals.getInstance().news.subList(0, s));
                    }

                    Globals.getInstance().newsSwipeContainer[0].setRefreshing(false);
                    Globals.getInstance().newsSwipeContainer[1].setRefreshing(false);
/*

                    if (Globals.getInstance().news.size() < 2 || Globals.getInstance().news.indexOf(news.get(0)) >= 0) {
                        Globals.getInstance().news = news;
                        if (Globals.getInstance().news.size() > 2*resultsPerPage)
                        RecyclerAdapter.setIsLastPage(true);
                    } else {
                        Globals.getInstance().news.addAll(news);
                    }

                    Log.d("Dfinished", "run: " + "aaaaaaa:\t" + Globals.getInstance().news.size());
                    Globals.getInstance().newsAdapter.refresh((ArrayList<News>) Globals.getInstance().news);
                    Globals.getInstance().mainNewsAdapter.refresh((ArrayList<News>) Globals.getInstance().news);*/
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
