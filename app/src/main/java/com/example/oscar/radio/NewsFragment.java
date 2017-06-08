package com.example.oscar.radio;

import android.content.res.Resources;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class NewsFragment extends Fragment {
    private RecyclerView newsRecyclerView;
    private LinearLayoutManager newsLinearLayoutManager;


    private int getLastVisibleItemPosition() {
        return newsLinearLayoutManager.findLastVisibleItemPosition();
    }

    private void setRecyclerViewScrollListener() {
        newsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                Log.d("SCROLL", "onScrollStateChanged: SCROLL");
                super.onScrollStateChanged(recyclerView, newState);
                int totalItemCount = newsRecyclerView.getLayoutManager().getItemCount();
                Log.d("SCROLL", "onScrollStateChanged: SCROLL totalItemCout: " + totalItemCount);
                Log.d("SCROLL", "onScrollStateChanged: SCROLL lastVisiBleItemPosition: " + getLastVisibleItemPosition());
                if (totalItemCount == getLastVisibleItemPosition() + 1) {
                    Globals.getInstance().newsAdapter[0].getNextNews();
                    Log.d("SCROLL", "onScrollStateChanged: LAST");
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int index = 0;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            index = bundle.getInt("type", 0);
        }

        final int finalIndex = index;

        View rootView = inflater.inflate(R.layout.news_tab, container, false);

        newsRecyclerView = (RecyclerView) rootView.findViewById(R.id.newsRecyclerView);
        newsLinearLayoutManager = new LinearLayoutManager(getContext()); // TODO ??
        newsRecyclerView.setLayoutManager(newsLinearLayoutManager);
        Globals.getInstance().newsAdapter[index] = new RecyclerAdapter((ArrayList<News>) Globals.getInstance().newsRaw.get(index));
        newsRecyclerView.setAdapter(Globals.getInstance().newsAdapter[index]);
        setRecyclerViewScrollListener();
        Log.d("INIT", "init: recView");


        Globals.getInstance().newsSwipeContainer[index] = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer_news);

        Globals.getInstance().newsSwipeContainer[index].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                try {
                    if(Globals.getInstance().isNetworkOnline())
                        Globals.getInstance().newsAdapter[0].getNews();
                } catch (NullPointerException e) {
                    Globals.getInstance().newsSwipeContainer[finalIndex].setRefreshing(false);
                }
            }
        });

        Globals.getInstance().refreshSwypeContainerColor(); // set the swyperefreshcontainer's color

        return rootView;
    }
}
