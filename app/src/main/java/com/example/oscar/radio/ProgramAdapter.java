package com.example.oscar.radio;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;

public class ProgramAdapter extends BaseAdapter {
    private static ArrayList<Program> searchArrayList;

    private LayoutInflater mInflater;

    public ProgramAdapter(Context context, ArrayList<Program> results) {
        searchArrayList = results;
        mInflater = LayoutInflater.from(context);
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
            holder.item = (RelativeLayout) convertView.findViewById(R.id.programListItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(searchArrayList.get(position).getName());
        holder.txtArtist.setText(searchArrayList.get(position).getType());
        holder.txtTime.setText(searchArrayList.get(position).getTimeStr());
        holder.txtDate.setText(searchArrayList.get(position).getDayStr());

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
        RelativeLayout item;
        TextView txtTitle;
        TextView txtArtist;
        TextView txtTime;
        TextView txtDate;
    }

    public void refresh() {
        searchArrayList = (ArrayList<Program>) Globals.programs;

        notifyDataSetChanged();
    }
}