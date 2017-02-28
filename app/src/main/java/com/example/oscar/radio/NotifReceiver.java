package com.example.oscar.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotifReceiver extends BroadcastReceiver {
    public static RadioService radioService;

    public NotifReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");

        // toggle the music playback
        // radioService.toggleStream();

        if(!Globals.getInstance().playing) {
            Globals.getInstance().playRadio();
        } else {
            Globals.getInstance().stopRadio();
        }
    }
}
