package com.example.oscar.radio;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;


public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mPrefs = getSharedPreferences("asd", 0);
        /*
        Globals.getInstance().colorPrimary = mPrefs.getInt("colorPrimary", R.color.colorPrimary);
        Globals.getInstance().colorPrimaryDark = mPrefs.getInt("colorPrimaryDark", R.color.colorPrimaryDark);
        Globals.getInstance().colorAccent = mPrefs.getInt("colorAccent", R.color.colorAccent);
        */

        Globals.getInstance().theme = mPrefs.getInt("theme", R.style.AppTheme_Teal);    // for now

        /*
        // get the color
        Globals.getInstance().colorPrimary = R.color.colorPrimary;
        Globals.getInstance().colorPrimaryDark = R.color.colorPrimaryDark;
        Globals.getInstance().colorAccent = R.color.colorAccent;
        */

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
