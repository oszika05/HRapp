package com.example.oscar.radio;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;


/**
 * Created by oszi on 2/1/17.
 */

public class MusicAdapter extends BaseAdapter {
    private static ArrayList<MusicTitle> searchArrayList;

    private LayoutInflater mInflater;

    public MusicAdapter(Context context, ArrayList<MusicTitle> results) {
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
            convertView = mInflater.inflate(R.layout.music_list_item, null);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.musicListItemTitle);
            holder.txtArtist = (TextView) convertView.findViewById(R.id.musicListItemDesc);
            holder.txtTime = (TextView) convertView.findViewById(R.id.musicListItemTime);
            holder.txtDate = (TextView) convertView.findViewById(R.id.musicListItemDate);
            holder.item = (RelativeLayout) convertView.findViewById(R.id.musicListItem);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtTitle.setText(searchArrayList.get(position).getTitle());
        holder.txtArtist.setText(searchArrayList.get(position).getArtist());
        holder.txtTime.setText(searchArrayList.get(position).getTime());
        holder.txtDate.setText(searchArrayList.get(position).getDate());

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(searchArrayList.get(position).getLink()));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(v.getContext(), i, null);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
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
        searchArrayList = (ArrayList<MusicTitle>) Globals.songs;

        notifyDataSetChanged();
    }
}
