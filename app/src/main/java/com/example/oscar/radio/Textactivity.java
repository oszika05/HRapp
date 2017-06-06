package com.example.oscar.radio;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Textactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Globals.getInstance().theme);
        setContentView(R.layout.activity_textactivity);

        Bundle b = getIntent().getExtras();
        String title = "";
        String type = "";
        String desc = "";
        String time = "";
        Bitmap image = null;
        int cover = R.drawable.cover_no22;

        ImageView coverPhoto = (ImageView) findViewById(R.id.textactivity_cover_photo);

        if(b != null) {
            try {
                title = b.getString("title");
                type = b.getString("type");
                desc = b.getString("desc");
                time = b.getString("time");

                if(getIntent().hasExtra("picture")) {
                    Picasso.with(coverPhoto.getContext()).load(b.getString("picture")).into(coverPhoto);
                } else {
                    cover = b.getInt("cover");
                }

            } catch (Resources.NotFoundException e) {
                cover = R.drawable.cover_no22;
                e.printStackTrace();
            }
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle(title);

        TextView typeText = (TextView) findViewById(R.id.type_text);
        TextView descText = (TextView) findViewById(R.id.description_textview);
        TextView timeText = (TextView) findViewById(R.id.number_textview);

        typeText.setText(type);
        descText.setText(desc);
        timeText.setText(time);
        if (!getIntent().hasExtra("picture")) {
            coverPhoto.setImageResource(cover);
        }

        android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point p = new Point();
        display.getSize(p);
        if (descText.getHeight() < 0.7*p.y) {
            descText.setHeight((int)(0.7*p.y));
        }

        setSupportActionBar(toolbar);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


/*

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item .getItemId() == android.R.id.home) {    // back button pressed on top left corner
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
