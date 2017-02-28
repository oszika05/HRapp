package com.example.oscar.radio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by oszi on 2/28/17.
 */

class BecomingNoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause the playback
            Globals.getInstance().mainActivity.stopService(Globals.getInstance().radioServicePlayerIntent);  // stopping the service
        }
    }
}