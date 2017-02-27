package com.example.oscar.radio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;

import static com.example.oscar.radio.Globals.mainActivity;


/**
 * Created by oszi on 2/17/17.
 */

public class radioPlayerService extends Service {
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private NotificationManager mNotificationManager;

    @Override
    public void onCreate() {
    }

    @Override
    public void  onDestroy() {
        stop();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Globals.rPlayerService = this;

        play();

        return(START_NOT_STICKY);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void play() {
        try {
            Globals.finishedLoading = false;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(Globals.url);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc) <- that's why it's async
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Globals.finishedLoading = true;
                Globals.playing = true;
                mediaPlayer.start();    // starting the player, when it finished preparing
                Globals.loadBar.dismiss();
            }
        });

        startForegroundNotification();

        Globals.fab.setImageResource(R.drawable.ic_pause_light);
    }

    private void stop() {
        Globals.playing = false;
        Globals.loadBar.dismiss();

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;


        Globals.fab.setImageResource(R.drawable.ic_play_light);

        Globals.setNotifButton(false);
        stopForeground(false);
        Globals.notifBuilder.setOngoing(false);
        Globals.notifBuilder.setSmallIcon(R.drawable.ic_pause);
        Globals.mNotifyMgr.notify(Globals.NOTIFICATION_ID, Globals.notifBuilder.build());
    }

    public void startForegroundNotification() {
        Globals.buildNotification(this);
        Globals.setNotifButton(true);
    }







// this is old


    private Notification assembleNotif() {
        String ns = Context.NOTIFICATION_SERVICE;
        Context ctx = Globals.mainActivity;
        mNotificationManager = (NotificationManager) ctx.getSystemService(ns);
        CharSequence tickerText = "Shortcuts";
        long when = System.currentTimeMillis();
        Notification.Builder builder = new Notification.Builder(ctx);
        @SuppressWarnings("deprecation")
        Notification notification=builder.getNotification();
        notification.when=when;
        notification.tickerText=tickerText;
        notification.icon = R.drawable.ic_play;

        RemoteViews contentView=new RemoteViews(ctx.getPackageName(), R.layout.notification);

        //set the button listeners
        // setListeners(contentView);
        //radio listener
        Intent radio = new Intent(Globals.mainActivity, NotifReceiver.class);
        PendingIntent pRadio = PendingIntent.getActivity(mainActivity, 0, radio, 0);
        contentView.setOnClickPendingIntent(R.id.radio, pRadio);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        //mNotificationManager.notify(Globals.NOTIFICATION_ID, notification);
        return notification;




/*
        //volume listener
        Intent volume=new Intent(ctx, HelperActivity.class);
        volume.putExtra("DO", "volume");
        PendingIntent pVolume = PendingIntent.getActivity(ctx, 1, volume, 0);
        view.setOnClickPendingIntent(R.id.volume, pVolume);

        //reboot listener
        Intent reboot=new Intent(ctx, HelperActivity.class);
        reboot.putExtra("DO", "reboot");
        PendingIntent pReboot = PendingIntent.getActivity(ctx, 5, reboot, 0);
        view.setOnClickPendingIntent(R.id.reboot, pReboot);

        //top listener
        Intent top=new Intent(ctx, HelperActivity.class);
        top.putExtra("DO", "top");
        PendingIntent pTop = PendingIntent.getActivity(ctx, 3, top, 0);
        view.setOnClickPendingIntent(R.id.top, pTop);*/

        //app listener
        /*Intent app=new Intent(ctx, com.example.demo.HelperActivity.class);
        app.putExtra("DO", "app");
        PendingIntent pApp = PendingIntent.getActivity(ctx, 4, app, 0);
        view.setOnClickPendingIntent(R.id.btn1, pApp);
        */
    }

}
