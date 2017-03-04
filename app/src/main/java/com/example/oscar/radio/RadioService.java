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
        Globals.getInstance().mBuilder.mActions.clear();
        if(Globals.getInstance().playing) {
            Globals.getInstance().mBuilder.addAction(R.drawable.ic_pause, "Pause", Globals.getInstance().pIntent);
            Globals.getInstance().mBuilder.setOngoing(true);
            Globals.getInstance().mBuilder.setSmallIcon(R.drawable.ic_play);

            Globals.getInstance().fab.setImageResource(R.drawable.ic_pause_light);
        } else {
            Globals.getInstance().mBuilder.addAction(R.drawable.ic_play, "Play", Globals.getInstance().pIntent);
            Globals.getInstance().mBuilder.setOngoing(false);
            Globals.getInstance().mBuilder.setSmallIcon(R.drawable.ic_pause);

            Globals.getInstance().fab.setImageResource(R.drawable.ic_play_light);
        }

        Globals.getInstance().mNotifyMgr.notify(Globals.getInstance().NOTIFICATION_ID, Globals.getInstance().mBuilder.build());
    }

    public boolean startStream(final Snackbar errBar) {

        Globals.getInstance().playing = true;
        Globals.getInstance().finishedLoading = false;

        try {
            Globals.getInstance().mediaPlayer = new MediaPlayer();
            Globals.getInstance().mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Globals.getInstance().mediaPlayer.setDataSource(Globals.getInstance().url);
            Globals.getInstance().mediaPlayer.prepareAsync(); // might take long! (for buffering, etc) <- that's why it's async

            Globals.getInstance().handler.postDelayed(new Runnable() {
                public void run() {
                    if(!Globals.getInstance().finishedLoading) {
                        //loadBar.dismiss();
                        errBar.show();
                    }
                }
            }, Globals.getInstance().SEVEN_SECONDS);


            new DownloadTitle().execute(Globals.getInstance().url);   // get the meta from the stream
        } catch (IOException e) {
            errBar.show();
            Globals.getInstance().playing = false;
            e.printStackTrace();
        }

        Globals.getInstance().mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Globals.getInstance().finishedLoading = true;
                Globals.getInstance().playing = true;
                mediaPlayer.start();    // starting the player, when it finished preparing
                Globals.getInstance().loadBar.dismiss();
            }
        });

        return Globals.getInstance().playing;
    }

    public boolean startStream() {
        Globals.getInstance().playing = true;

        try {
            Globals.getInstance().mediaPlayer = new MediaPlayer();
            Globals.getInstance().mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            Globals.getInstance().mediaPlayer.setDataSource(Globals.getInstance().url);
            Globals.getInstance().mediaPlayer.prepareAsync(); // might take long! (for buffering, etc) <- that's why it's async

            new DownloadTitle().execute(Globals.getInstance().url);   // get the meta from the stream
        } catch (IOException e) {

            Globals.getInstance().playing = false;
            e.printStackTrace();

            Log.d("DBG", "startStream: Playing: " + Globals.getInstance().playing);
        }

        Globals.getInstance().mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.start();    // starting the player, when it finished preparing
            }
        });

        return Globals.getInstance().playing;
    }

    public boolean stopStream() {
        if(Globals.getInstance().mediaPlayer==null)
            return Globals.getInstance().playing;

        Globals.getInstance().playing = false;
        Globals.getInstance().loadBar.dismiss();
        Globals.getInstance().mediaPlayer.release();
        Globals.getInstance().mediaPlayer = null;
        return Globals.getInstance().playing;
    }

    public void restartStream() {
        stopStream();
        setNotificationButton();
        startStream();
        setNotificationButton();
    }

    public boolean toggleStream() {

        if(Globals.getInstance().playing)
            Globals.getInstance().playing = stopStream();
        else
            Globals.getInstance().playing = startStream();

        setNotificationButton();

        return Globals.getInstance().playing;
    }

    public void refreshMetaData() {

        if(Globals.getInstance().playing)
            try {
                if(Globals.getInstance().isNetworkOnline())
                    new DownloadTitle().execute(Globals.getInstance().url);
            } catch (NullPointerException e) {
                // it's ok
            }

        Globals.getInstance().handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Globals.getInstance().playing) {
                    try {
                        if(Globals.getInstance().isNetworkOnline())
                            new DownloadTitle().execute(Globals.getInstance().url);
                    } catch (NullPointerException e) {
                        // It's ok'
                    }
                }

                Globals.getInstance().handler.postDelayed(this, Globals.getInstance().TEN_SECONDS);
            }
        }, Globals.getInstance().TEN_SECONDS);
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
        mNotifyMgr.cancel(Globals.getInstance().NOTIFICATION_ID);

        super.onDestroy();
    }


}
