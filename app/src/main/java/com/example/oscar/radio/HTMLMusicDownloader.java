package com.example.oscar.radio;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oszi on 2/4/17.
 */

public class HTMLMusicDownloader extends AsyncTask<Integer, Void, List<MusicTitle>> {

    private void parseMusic(Element line, List<MusicTitle> songs) {
        MusicTitle res = new MusicTitle();

        try {
            res.setTitle(line.select("div.mline_song").text());
            res.setDate(line.select("div.mline_time").text());
            res.setLink(line.select("a[href]").get(0).attr("href"));    // youtube link
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        songs.add(res);
    }

    @Override
    protected List<MusicTitle> doInBackground(Integer[] params) {
        int page = params[0];
        List<MusicTitle> songs = new ArrayList<MusicTitle>();    // creating a new list

        try {
            // Connect to the web site
            // Document document = Jsoup.connect("http://streamstat.hu/tracklist.cgi?5398&ch=2&r").get();
            // Document document = Jsoup.connect("https://onlinestream.hu/tracklist.cgi?id=5398&ch=3&songsearch=%20&tfp=100&tp=1").get();
            // int page = 1;
            int resultsPerPage = 25; // 25, 50, 100, 200
            // Document documentRaw = Jsoup.connect("https://onlinestream.hu/tracklist.cgi?id=5398&ch=1&songsearch=%20&tfp=" + resultsPerPage + "&tp=" + page +"&mode=ajax").get(); // ajax

            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "https://onlinestream.hu/tracklist.cgi?id=5398&ch=1&songsearch=%20&tfp=" + resultsPerPage + "&tp=" + page +"&mode=ajax";
            String jsonStr = sh.makeServiceCall(url);


            JSONObject reader = new JSONObject(jsonStr);
            Document document = Jsoup.parse(reader.get("content").toString());
            Log.d("RAW", "doInBackground: " + reader.get("content").toString());

            // Log.d("DocHTML", "doInBackground: " + document.select("table").html());



            // Using Elements to get the class data
            Elements line = document.select("table > tbody > tr");
            //Elements line = lines.select("tbody");
            Log.d("HTML", "doInBackground: " + line.get(0).html());

            for (Element el : line) {
                Elements td = el.select("td");
                Log.d("SONG", "date: " + td.get(0).select("span").html());
                Log.d("SONG", "name: " + td.get(1).html());
                Log.d("SONG", "link: " + td.get(2).select("a").attr("href"));

                MusicTitle res = new MusicTitle(); // TODO filter songs with ""(empty) title

                try {
                    res.setDate("20" + td.get(0).select("span").html());
                    res.setTitle(td.get(1).html());
                    res.setLink(td.get(2).select("a").attr("href"));    // youtube link
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                songs.add(res);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return songs;
    }

    @Override
    protected void onPostExecute(final List<MusicTitle> songs) {
        Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(songs.size() > 1) {
                        if (Globals.getInstance().songs.indexOf(songs.get(0)) + 1 == Globals.getInstance().songs.indexOf(songs.get(1))) {
                            Globals.getInstance().songs = songs;
                        } else {
                            Globals.getInstance().songs.addAll(songs);
                        }
                    } else {
                        Globals.getInstance().songs.addAll(songs);
                    }

                    Globals.getInstance().musicAdapter.refresh();

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
