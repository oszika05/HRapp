package com.example.oscar.radio;

import android.media.tv.TvContract;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class ProgramFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        int d = 0;
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            d = bundle.getInt("day", 0);
        }

        final int day = d;

        View rootView = inflater.inflate(R.layout.program_day, container, false);
        ListView list = (ListView) rootView.findViewById(R.id.program_list);
        ArrayList<Program> perDay = new ArrayList<Program>();

        Globals.getInstance().programSwipeContainer[day - 1] = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeContainer_program);
        // Setup refresh listener which triggers new data loading
        Globals.getInstance().programSwipeContainer[day - 1].setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                try {
                    if(Globals.getInstance().isNetworkOnline())
                        Globals.getInstance().radioService.downloadHtml();
                } catch (NullPointerException e) {
                    Globals.getInstance().programSwipeContainer[day - 1].setRefreshing(false);
                }
            }
        });
        // Configure the refreshing colors
        Globals.getInstance().programSwipeContainer[day - 1].setColorSchemeResources(R.color.colorAccent);



        if(Globals.getInstance().programs == null)
            return rootView;

        for(int i=0; i<Globals.getInstance().programs.size(); i++) {  // todo clickable days
            if (Globals.getInstance().programs.get(i).getDay() == day) {
                perDay.add(Globals.getInstance().programs.get(i));
            }
        }

        Globals.getInstance().programAdapter[day - 1] = new ProgramAdapter(Globals.getInstance().mainActivity, perDay, list, day);

        list.setAdapter(Globals.getInstance().programAdapter[day - 1]);
        
        Globals.getInstance().programAdapter[Globals.getInstance().getDay() - 1].goToCurrItem();


        if(Globals.getInstance().programs.size() == 0) {  // the list is not yet loaded
            Globals.getInstance().programSwipeContainer[day - 1].setRefreshing(true);  // the refreshing icon

            Globals.getInstance().radioService.downloadHtml();    // getting the programs
        }


        return rootView;
    }
}
