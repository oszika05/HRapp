package com.example.oscar.radio;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HTMLDownloader extends AsyncTask<Void, Void, List<Program>> {

    private Program parseProgram(Element li, int day) {
        Program res = new Program();

        try {
            String dateStr = li.select("div.time-default").text();
            res.setDate(dateStr, day);

            res.setName(li.select("span.views-field-field-musorok-ref-musorrendhez").text());

            res.setType(li.select("span.views-field-php").text());


            res.setDesc(li.select("div.ajanlo-szoveg").text());

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return res;
    }

    @Override
    protected List<Program> doInBackground(Void... params) {

        List<Program> programs = new ArrayList<Program>();    // creating(or overwriting) a new list

        try {
            // Connect to the web site
            Document document = Jsoup.connect("http://www.hitradio.hu").get();

            // Using Elements to get the class data
            Elements ul = document.select("div.item-list > ul");
            Elements li = ul.select("li");

            for (int i = 0; i < li.size(); i++) {   // Monday
                for(int j=1; j<=7; j++) {
                    if (li.get(i).select("span.nap"+j).text().length() > 15) {
                        programs.add(parseProgram(li.get(i), j));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return programs;
    }
    @Override
    protected void onPostExecute(final List<Program> programs){

        HTMLDownloader.setProgramCard(programs);
        Globals.noInternetCard.setVisibility(View.GONE); // hide noInternet card
        Globals.programCard.setVisibility(View.VISIBLE);    // show programcard

        Globals.mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Globals.programs = programs;
                    //Globals.programAdapter.notifyDataSetChanged();
                    Globals.programAdapter.refresh();

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            Globals.programSwipeContainer.setRefreshing(false);
            Globals.noInternetCard.setVisibility(View.GONE);
            Globals.mainSwipeContainer.setRefreshing(false);    // the loading is finished
            Globals.mainSwipeContainer.setEnabled(false);
        } catch (NullPointerException e) {
            // it's ok
        }
    }



    public static void setProgramCard(final List<Program> programs) {
        try {
            Globals.programCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Program curr = null;

                    for(Program p : programs) {
                        if(p.isPlayingNow()) {
                            curr = p;
                            break;
                        }
                    }

                    if(curr != null) {
                        Intent i = new Intent();
                        i.setClass(v.getContext(), Textactivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        Bundle b = new Bundle();
                        b.putString("title", curr.getName()); // Title
                        b.putString("type", curr.getType()); // Type
                        b.putString("desc", curr.getDesc()); // Description
                        b.putString("time", curr.getDayWithTime()); // Time
                        b.putInt("cover", CoverPhoto.get(curr.getName(), false)); // Cover Photo
                        i.putExtras(b); // Put your id to your next Intent


                        try {
                            v.getContext().startActivity(i);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


            Program curr = null;
            for(Program p : programs) {
                if(p.isPlayingNow()) {
                    curr = p;
                    break;
                }
            }

            // set the program
            Globals.programText.setText(curr.getName());
            Globals.programDescText.setText(curr.getType());
            //Globals.programCardImage.setImageResource(CoverPhoto.get(curr.getName(), true));
            Picasso.with(Globals.mainActivity.getApplicationContext()).load(
                    CoverPhoto.get(curr.getName(), true)).into(
                    (ImageView)Globals.mainActivity.findViewById(R.id.thumbnail_program)
            );
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
