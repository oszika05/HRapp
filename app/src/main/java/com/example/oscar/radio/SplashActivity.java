package com.example.oscar.radio;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by oszi on 2/14/17.
 */

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
