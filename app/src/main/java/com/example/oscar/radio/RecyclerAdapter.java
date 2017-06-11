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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.NewsHolder> {
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


    public static class NewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;
        private TextView date;
        private TextView desc;
        private ImageView image;
        private News news;


        public NewsHolder(View v) {
            super(v);

            title = (TextView) v.findViewById(R.id.news_title);
            date = (TextView) v.findViewById(R.id.news_date);
            desc = (TextView) v.findViewById(R.id.news_desc);
            image = (ImageView) v.findViewById(R.id.thumbnail_news);
            v.setOnClickListener(this);
        }

        public void bindNews(News news) {
            this.news = news;
            Picasso.with(image.getContext()).load(news.getPicture()).into(image);
            title.setText(news.getTitle());
            date.setText(news.getDate());
            desc.setText(news.getContent());
        }

        @Override
        public void onClick(View v) {
            int t = news.getType().equals(Globals.getInstance().newsRaw.get(0).get(0).getType()) ?  0 : 1;
            Context context = itemView.getContext();
            Intent newsIntent = new Intent(context, Textactivity.class);
            newsIntent.putExtra("title", news.getType());
            newsIntent.putExtra("type", news.getTitle());
            newsIntent.putExtra("desc", "<br /><b>" + news.getContent() + "</b>");
            newsIntent.putExtra("time", news.getDate());
            newsIntent.putExtra("picture", news.getPicture());
            newsIntent.putExtra("contentLoading", true);
            int n = Globals.getInstance().newsRaw.get(t).indexOf(news);
            newsIntent.putExtra("newsN", n);
            newsIntent.putExtra("newsType", t); // LOL

            context.startActivity(newsIntent);
            Log.d("RecyclerView", "CLICK!");
        }
    }

    public RecyclerAdapter(ArrayList<News> news) {
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
    public RecyclerAdapter.NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.news_list_layout, parent, false);
        return new NewsHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.NewsHolder holder, int position) {
        News newsItem = news.get(position);
        holder.bindNews(newsItem);
    }

    @Override
    public int getItemCount() {
        return news.size();
    }
}
