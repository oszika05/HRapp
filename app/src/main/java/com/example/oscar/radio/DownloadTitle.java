package com.example.oscar.radio;

import android.os.AsyncTask;
import android.view.View;

import java.io.IOException;
import java.net.URL;


class DownloadTitle extends AsyncTask<String, Void, String> {   // This is a long task, so we need to do it async

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
        for(int i=0; i<Globals.getInstance().programs.size(); i++)
            if (Globals.getInstance().programs.get(i).isPlayingNow())
                return Globals.getInstance().programs.get(i);

        return null;
    }

    @Override
    protected void onPostExecute(String s) {    // set the notification to the title and artist
        Program currentProgram = now();
        if(currentProgram==null) {
            currentProgram = new Program();
        }

        if(s == null || s.length() == 0 || (!Globals.getInstance().validSongTitle(s) && Globals.getInstance().songs.size()==0)) {

            Globals.getInstance().musicTitle = currentProgram.getName();
            Globals.getInstance().musicDesc = currentProgram.getType();     // If there is no music title in the metadata
            if(Globals.getInstance().musicCard!=null)
                Globals.getInstance().musicCard.setVisibility(View.GONE);     // hide the music card

        } else {
            try {
                if (!Globals.getInstance().validSongTitle(s)) {     // setting the notification title from the sound history
                    Globals.getInstance().musicTitle = Globals.getInstance().songs.get(0).getTitle() +
                            " - " + Globals.getInstance().songs.get(0).getArtist();
                    Globals.getInstance().musicDesc = currentProgram.getName();

                    Globals.getInstance().artistText.setText(Globals.getInstance().songs.get(0).getArtist());
                    Globals.getInstance().titleText.setText(Globals.getInstance().songs.get(0).getTitle());

                } else {
                    Globals.getInstance().musicTitle =  s;
                    Globals.getInstance().musicDesc = currentProgram.getName();

                    if (s.contains("-")) {  // just to be sure :)
                        Globals.getInstance().artistText.setText(s.substring(0, s.indexOf("-"))); // this is the main View
                        Globals.getInstance().titleText.setText(s.substring(s.indexOf("-") + 2)); // +1 for the '-' and an other one for the ' '
                    }
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            }


            try {
                Globals.getInstance().musicCard.setVisibility(View.VISIBLE);  // display the music card
                Globals.getInstance().noInternetCard.setVisibility(View.GONE);    // hide the noInternet card
                Globals.getInstance().mainSwipeContainer.setRefreshing(false);    // the loading is finished
                Globals.getInstance().mainSwipeContainer.setEnabled(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        Globals.getInstance().setNotifText(Globals.getInstance().musicTitle, Globals.getInstance().musicDesc);


        HTMLDownloader.setProgramCard(Globals.getInstance().programs);

        if(Globals.getInstance().artistText == null ||Globals.getInstance(). titleText == null || Globals.getInstance().programText == null) {
            return;
        }


    }
}