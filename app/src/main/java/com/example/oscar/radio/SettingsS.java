package com.example.oscar.radio;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsS extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, Globals.getInstance().settingsFragment).commit();
    }
}
