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

/**
 * Created by oszi on 6/6/17.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.MainNewsHolder> {
    private ArrayList<News> news;
    static private HTMLNewsDownloader downloader = null;
    static private int currentPage = 0; // starts from 0
    static private boolean isLastPage = false;
    public static boolean getIsLastPage() {
        return isLastPage;
    }
    public static void setIsLastPage(boolean value) {
        isLastPage = value;
    }


    public static class MainNewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private ImageView image;
        private News news;


        public MainNewsHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.news_title);
            image = (ImageView) v.findViewById(R.id.thumbnail_news);
            v.setOnClickListener(this);
        }

        public void bindNews(News news) {
            this.news = news;
            Picasso.with(image.getContext()).load(news.getPicture()).into(image);
            title.setText(news.getTitle());
            title.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("LINE", "bindNews: 00000000000\t" + title.getLineCount());
                    title.setMinLines(title.getLineCount() + 1);
                    title.post(new Runnable() {
                        @Override
                        public void run() {

                            Log.d("LINE", "bindNews: 11111111111\t" + title.getLineCount());
                        }
                    });
                }
            });

        }

        @Override
        public void onClick(View v) {
            Context context = itemView.getContext();
            Intent newsIntent = new Intent(context, Textactivity.class);
            newsIntent.putExtra("title", news.getType());
            newsIntent.putExtra("type", news.getTitle());
            newsIntent.putExtra("desc", "<br /><b>" + news.getContent() + "</b>");
            newsIntent.putExtra("time", news.getDate());
            newsIntent.putExtra("picture", news.getPicture());
            newsIntent.putExtra("contentLoading", true);
            int n = Globals.getInstance().news.indexOf(news);
            newsIntent.putExtra("newsN", n);
            newsIntent.putExtra("newsType", -1);

            context.startActivity(newsIntent);
            Log.d("RecyclerView", "CLICK!");
        }
    }

    public MainRecyclerAdapter(ArrayList<News> news) {
        getNews();
        this.news = news;
    }

    public void refresh(ArrayList<News> news) {
        this.news = news;
        notifyDataSetChanged();
        Log.d("TAG", "currentPage AFTER: " + currentPage);
    }

    public void getNews() {
        currentPage = 0; // first page
        isLastPage = false;
        getNextNews();
    }

    public void getNextNews() {
        Log.d("LASTPAGE", "getNextNews: " + isLastPage);
        Log.d("PAGE", "getNextNews: lastPage: " + currentPage);
        if (!isLastPage && (downloader == null || downloader.getStatus() == AsyncTask.Status.FINISHED)) {
            downloader = new HTMLNewsDownloader();
            Log.d("TAG", "currentPage BEFORE: " + currentPage);
            downloader.execute(currentPage);
            currentPage++;
        }
    }

    @Override
    public MainRecyclerAdapter.MainNewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_news_list_layout, parent, false);
        return new MainRecyclerAdapter.MainNewsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(MainRecyclerAdapter.MainNewsHolder holder, int position) {
        News newsItem = news.get(position);
        holder.bindNews(newsItem);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}
