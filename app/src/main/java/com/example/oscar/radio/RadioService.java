package com.example.oscar.radio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import java.net.URL;

import static com.example.oscar.radio.Globals.mainActivity;


public class RadioService extends Service {

    public RadioService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void setNotificationButton() {
        Globals.mBuilder.mActions.clear();
        if(Globals.playing) {
            Globals.mBuilder.addAction(R.drawable.ic_pause, "Pause", Globals.pIntent);
            Globals.mBuilder.setOngoing(true);
            Globals.mBuilder.setSmallIcon(R.drawable.ic_play);

            Globals.fab.setImageResource(R.drawable.ic_pause_light);
        } else {
            Globals.mBuilder.addAction(R.drawable.ic_play, "Play", Globals.pIntent);
            Globals.mBuilder.setOngoing(false);
            Globals.mBuilder.setSmallIcon(R.drawable.ic_pause);

            Globals.fab.setImageResource(R.drawable.ic_play_light);
        }

        Globals.mNotifyMgr.notify(Globals.NOTIFICATION_ID, Globals.mBuilder.build());
    }

    public boolean startStream(final Snackbar errBar) {

        Globals.playing = true;
        Globals.finishedLoading = false;

        try {
            Globals.mediaPlayer = new MediaPlayer();
            Globals.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Globals.mediaPlayer.setDataSource(Globals.url);
            Globals.mediaPlayer.prepareAsync(); // might take long! (for buffering, etc) <- that's why it's async

            Globals.handler.postDelayed(new Runnable() {
                public void run() {
                    if(!Globals.finishedLoading) {
                        //loadBar.dismiss();
                        errBar.show();
                    }
                }
            }, Globals.SEVEN_SECONDS);


            new DownloadTitle().execute(Globals.url);   // get the meta from the stream
        } catch (IOException e) {
            errBar.show();
            Globals.playing = false;
            e.printStackTrace();
        }

        Globals.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Globals.finishedLoading = true;
                Globals.playing = true;
                mediaPlayer.start();    // starting the player, when it finished preparing
                Globals.loadBar.dismiss();
            }
        });

        return Globals.playing;
    }

    public boolean startStream() {
        Globals.playing = true;

        try {
            Globals.mediaPlayer = new MediaPlayer();
            Globals.mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Globals.mediaPlayer.setDataSource(Globals.url);
            Globals.mediaPlayer.prepareAsync(); // might take long! (for buffering, etc) <- that's why it's async

            new DownloadTitle().execute(Globals.url);   // get the meta from the stream
        } catch (IOException e) {

            Globals.playing = false;
            e.printStackTrace();

            Log.d("DBG", "startStream: Playing: " + Globals.playing);
        }

        Globals.mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();    // starting the player, when it finished preparing
            }
        });

        return Globals.playing;
    }

    public boolean stopStream() {
        if(Globals.mediaPlayer==null)
            return Globals.playing;

        Globals.playing = false;
        Globals.loadBar.dismiss();
        Globals.mediaPlayer.release();
        Globals.mediaPlayer = null;
        return Globals.playing;
    }

    public void restartStream() {
        stopStream();
        setNotificationButton();
        startStream();
        setNotificationButton();
    }

    public boolean toggleStream() {

        if(Globals.playing)
            Globals.playing = stopStream();
        else
            Globals.playing = startStream();

        setNotificationButton();

        return Globals.playing;
    }

    public void refreshMetaData() {

        if(Globals.playing)
            try {
                if(Globals.isNetworkOnline())
                    new DownloadTitle().execute(Globals.url);
            } catch (NullPointerException e) {
                // it's ok
            }

        Globals.handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Globals.playing) {
                    try {
                        if(Globals.isNetworkOnline())
                            new DownloadTitle().execute(Globals.url);
                    } catch (NullPointerException e) {
                        // It's ok'
                    }
                }

                Globals.handler.postDelayed(this, Globals.TEN_SECONDS);
            }
        }, Globals.TEN_SECONDS);
    }

    public void downloadHtml() {
        try {
            new HTMLDownloader().execute();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void refreshSongsHTML() {
        try {
            new HTMLMusicDownloader().execute();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // stop the notification
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(Globals.NOTIFICATION_ID);

        super.onDestroy();
    }


}
