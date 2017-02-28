package com.example.oscar.radio;

import android.os.AsyncTask;
import android.util.Log;

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

public class HTMLMusicDownloader extends AsyncTask<Void, Void, List<MusicTitle>> {

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
    protected List<MusicTitle> doInBackground(Void... params) {
        List<MusicTitle> songs = new ArrayList<MusicTitle>();    // creating a new list

        try {
            // Connect to the web site
            Document document = Jsoup.connect("http://streamstat.hu/tracklist.cgi?5398&ch=2&r").get();

            // Using Elements to get the class data
            Elements line = document.select("div.mline_line");

            for (int i = 0; i < line.size(); i++) {   // Monday
                if (Globals.getInstance().validSongTitle(line.get(i).select("div.mline_song").text())) {
                    parseMusic(line.get(i), songs);
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }

        return songs;
    }

    @Override
    protected void onPostExecute(final List<MusicTitle> songs){
        Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Globals.getInstance().songs = songs;
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
