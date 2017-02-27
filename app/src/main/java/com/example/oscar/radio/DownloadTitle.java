package com.example.oscar.radio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;
import java.net.URL;


public class DownloadTitle extends AsyncTask<String, Void, String> {   // This is a long task, so we need to do it async

    @Override
    protected String doInBackground(String... urls) {   // get the artist and the title
        try {
            String url = urls[0];
            IcyStreamMeta meta = new IcyStreamMeta(new URL(url));
            String metaData = meta.getMeta();
            if(metaData == null)
                return "";

            metaData = metaData.replace("_", " ");

            // TODO: NO CAPS LOCK

            return metaData;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NullPointerException e) {
            return "";
        }

    }

    private Program now() {
        for(int i=0; i<Globals.programs.size(); i++)
            if (Globals.programs.get(i).isPlayingNow())
                return Globals.programs.get(i);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {    // set the notification to the title and artist
        Program currentProgram = now();
        if(currentProgram==null) {
            currentProgram = new Program();
        }

        if(s == null || s.length() == 0 || (!Globals.validSongTitle(s) && Globals.songs.size()==0)) {

            Globals.musicTitle = currentProgram.getName();
            Globals.musicDesc = currentProgram.getType();     // If there is no music title in the metadata
            if(Globals.musicCard!=null)
                Globals.musicCard.setVisibility(View.GONE);     // hide the music card

        } else {
            try {
                if (!Globals.validSongTitle(s)) {     // setting the notification title from the sound history
                    Globals.musicTitle = Globals.songs.get(0).getTitle() +
                            " - " + Globals.songs.get(0).getArtist();
                    Globals.musicDesc = currentProgram.getName();

                    Globals.artistText.setText(Globals.songs.get(0).getArtist());
                    Globals.titleText.setText(Globals.songs.get(0).getTitle());

                } else {
                    Globals.musicTitle =  s;
                    Globals.musicDesc = currentProgram.getName();

                    if (s.contains("-")) {  // just to be sure :)
                        Globals.artistText.setText(s.substring(0, s.indexOf("-"))); // this is the main View
                        Globals.titleText.setText(s.substring(s.indexOf("-") + 2)); // +1 for the '-' and an other one for the ' '
                    }
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            try {
                Globals.musicCard.setVisibility(View.VISIBLE);  // display the music card
                Globals.noInternetCard.setVisibility(View.GONE);    // hide the noInternet card
                Globals.mainSwipeContainer.setRefreshing(false);    // the loading is finished
                Globals.mainSwipeContainer.setEnabled(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        Globals.setNotifText(Globals.musicTitle, Globals.musicDesc);


        HTMLDownloader.setProgramCard(Globals.programs);

        if(Globals.artistText == null ||Globals. titleText == null || Globals.programText == null) {
            return;
        }


    }
}