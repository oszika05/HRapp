package com.example.oscar.radio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by oszi on 6/6/17.
 */


public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainProgramsHolder> {
    private ArrayList<Program> programs;
    static private HTMLDownloader downloader = null;
    static private int currentPage = 0; // starts from 0
    static private boolean isLastPage = false;
    public static boolean getIsLastPage() {
        return isLastPage;
    }
    public static void setIsLastPage(boolean value) {
        isLastPage = value;
    }


    public static class MainProgramsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView image;
        private Program programs;


        public MainProgramsHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.news_title);
            image = (ImageView) v.findViewById(R.id.thumbnail_news);
            v.setOnClickListener(this);
        }

        public void bindPrograms(Program programs) {
            this.programs = programs;
            Picasso.with(image.getContext()).load(CoverPhoto.get(programs.getName(), false)).into(image);
            title.setText(programs.getName());
            title.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("LINE", "bindPrograms: 00000000000\t" + title.getLineCount());
                    title.setMinLines(title.getLineCount() + 1);
                    title.post(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("LINE", "bindPrograms: 11111111111\t" + title.getLineCount());
                        }
                    });
                }
            });

        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent programsIntent = new Intent(context, Textactivity.class);
            programsIntent.putExtra("title", programs.getType());
            programsIntent.putExtra("type", programs.getName());
            programsIntent.putExtra("desc", "<br /><b>" + programs.getDesc() + "</b>");
            programsIntent.putExtra("time", programs.getTimeStr());
            programsIntent.putExtra("picture", CoverPhoto.get(programs.getName(), false));

            context.startActivity(programsIntent);
        }
    }

    public MainRecyclerAdapter(ArrayList<Program> programs) {
        getPrograms();
        this.programs = programs;
    }

    public void refresh(ArrayList<Program> programs) {
        int currentI = 0;
        for (int i = 0; i < programs.size(); ++i) {
            if (programs.get(i).isPlayingNow()) {
                currentI = i;
                break;
            }
        }

        this.programs = new ArrayList<Program>(programs.subList(currentI,
                (currentI + 10 < programs.size()) ?
                        currentI + 10 :
                        programs.size()
        ));
        notifyDataSetChanged();
        Log.d("TAG", "currentPage AFTER: " + currentPage);
    }

    public void getPrograms() {
        getNextPrograms();
    }

    public void getNextPrograms() {
        Log.d("LASTPAGE", "getNextPrograms: " + isLastPage);
        Log.d("PAGE", "getNextPrograms: lastPage: " + currentPage);
        if ((downloader == null || downloader.getStatus() == AsyncTask.Status.FINISHED)) {
            downloader = new HTMLDownloader();
            Log.d("TAG", "currentPage BEFORE: " + currentPage);
            downloader.execute();
            currentPage++;
        }
    }

    @Override
    public MainRecyclerAdapter.MainProgramsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_news_list_layout, parent, false);
        return new MainRecyclerAdapter.MainProgramsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.MainProgramsHolder holder, int position) {
        Program programsItem = programs.get(position);
        holder.bindPrograms(programsItem);
    }

    @Override
    public int getItemCount() {
        return programs.size();
    }
}
