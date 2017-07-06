package com.example.oscar.radio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.content.ContextCompat.startActivity;

public class ProgramAdapter extends BaseAdapter {
    private ArrayList<Program> searchArrayList;
    private int day;
    private ListView list;
    private static boolean first = true;
    private static HTMLDownloader downloader = null;
    private static int current = 0;
    private boolean isCurrentDay = false;

    private LayoutInflater mInflater;

    public ProgramAdapter(Context context, ArrayList<Program> results, ListView list, int day) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
        this.day = day;
        this.list = list;

        try {
            highlightCurrentItem();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        setTimer();


    }

    private void setTimer() { // highlights
        Timer timer = new Timer();
        Log.d("DAY", "setTimer: monday: " + Calendar.MONDAY);
        Log.d("DAY", "setTimer: sunday: " + Calendar.SUNDAY);
        Log.d("DAY", "setTimer: monday: " + Calendar.TUESDAY);
        Log.d("DAY", "setTimer: sunday: " + Calendar.FRIDAY);

        boolean isHalf = Calendar.getInstance().get(Calendar.MINUTE) < 31;
        isCurrentDay = (day == currentDay());

        Calendar cal = new GregorianCalendar(Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + (isHalf ? 0 : 1),
                isHalf ? 30 : 0, 10);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss");
        Log.d("DATE", "setTimer: " + sdf.format(date));

        timer.schedule(new TimerTask() {   // TODO: test this in action

            @Override
            public void run() {
                Globals.getInstance().mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isCurrentDay = (day == currentDay());
                        refresh();
                        Log.d("run", "run: RUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUUN"); // it's okay
                    }
                });
            }
        }, date, 1800000); // 30 min = 1 800 000 ms
    }

    private int currentDay() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if (day == 0) day = 7;

        return day;
    }

    public int getCount() {
        return searchArrayList.size();
    }

    public Object getItem(int position) {
        return searchArrayList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.program_list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.programListItemTitle);
            holder.txtArtist = (TextView) convertView.findViewById(R.id.programListItemDesc);
            holder.txtTime = (TextView) convertView.findViewById(R.id.programListItemTime);
            holder.txtDate = (TextView) convertView.findViewById(R.id.programListItemDay);
            holder.item = (LinearLayout) convertView.findViewById(R.id.programListItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(searchArrayList.get(position).getName());
        holder.txtArtist.setText(searchArrayList.get(position).getType());
        holder.txtTime.setText(searchArrayList.get(position).getTimeStr().substring(0, searchArrayList.get(position).getTimeStr().indexOf("-") - 1));
        holder.txtDate.setText(searchArrayList.get(position).getDayStr());

        if (position == current && isCurrentDay) {
            holder.txtTitle.setTypeface(null, Typeface.BOLD);
        } else {
            holder.txtTitle.setTypeface(null, Typeface.NORMAL);
        }

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Textactivity.class);    // this is a test

                Bundle b = new Bundle();
                b.putString("title", searchArrayList.get(position).getName()); // Title
                b.putString("type", searchArrayList.get(position).getType()); // Type
                b.putString("desc", searchArrayList.get(position).getDesc()); // Description
                b.putString("time", searchArrayList.get(position).getDayWithTime()); // Time
                b.putInt("cover", CoverPhoto.get(searchArrayList.get(position).getName(), false)); // Cover Photo
                intent.putExtras(b); // Put your id to your next Intent

                startActivity(v.getContext(), intent, null);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        LinearLayout item;
        TextView txtTitle;
        TextView txtArtist;
        TextView txtTime;
        TextView txtDate;
    }

    public void refresh() {
        searchArrayList.clear();

        for(int i=0; i<Globals.getInstance().programs.size(); i++) {
            if(Globals.getInstance().programs.get(i).getDay() == day) {
                searchArrayList.add(Globals.getInstance().programs.get(i));
            }
        }

        highlightCurrentItem();

        notifyDataSetChanged();



        if(first) {
            goToCurrItem();
        }
    }

    public void highlightCurrentItem() {
        for (int i = 0; i < searchArrayList.size(); i++) {
            if (searchArrayList.get(i).isPlayingNow()) {
                current = i;
                break;
            }
        }

    }

    public void getPrograms() {
        if (downloader == null || downloader.getStatus() == AsyncTask.Status.FINISHED) {
            downloader = new HTMLDownloader();
            downloader.execute();
        }
    }

    public void goToCurrItem() {    // TODO this
        if(!first) return;

        first = false;
        Log.d("E S E M É N Y", "goToCurrItem: ESEMÉNY!");
        for(int i=0; i<searchArrayList.size(); ++i) {
            if(searchArrayList.get(i).isPlayingNow()) {
                list.smoothScrollToPosition(list.getMaxScrollAmount());
                Log.d("DAAY", "goToCurrItem: " + day);
                return;
            }
        }
    }
}