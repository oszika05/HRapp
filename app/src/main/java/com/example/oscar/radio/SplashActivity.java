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

        Globals.getInstance().themes[0] = R.style.AppTheme_Brown;
        Globals.getInstance().themes[1] = R.style.AppTheme_Indigo;
        Globals.getInstance().themes[2] = R.style.AppTheme_Teal;
        Globals.getInstance().themes[3] = R.style.AppTheme_Pink;
        Globals.getInstance().themes[4] = R.style.AppTheme_Green;
        Globals.getInstance().themes[5] = R.style.AppTheme_Grey;

        Globals.getInstance().themeN = mPrefs.getInt("theme", 0);    // for now
        if (Globals.getInstance().themeN < 0 && Globals.getInstance().themeN > 6)
            Globals.getInstance().themeN = 0;
        Globals.getInstance().theme = Globals.getInstance().themes[Globals.getInstance().themeN];

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
