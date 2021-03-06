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
        Globals.getInstance().rPlayerService = this;
        Globals.getInstance().mediaPlayer = mediaPlayer;

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
            Globals.getInstance().finishedLoading = false;
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(Globals.getInstance().url);
            mediaPlayer.prepareAsync(); // might take long! (for buffering, etc) <- that's why it's async
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Globals.getInstance().finishedLoading = true;
                Globals.getInstance().playing = true;
                mediaPlayer.start();    // starting the player, when it finished preparing
                mediaPlayer.setVolume(1.0f, 1.0f);
                Globals.getInstance().loadBar.dismiss();
            }
        });

        startForegroundNotification();

        Globals.getInstance().fab.setImageResource(R.drawable.ic_pause_light);
    }

    private void stop() {
        Globals.getInstance().playing = false;
        Globals.getInstance().loadBar.dismiss();

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;


        Globals.getInstance().fab.setImageResource(R.drawable.ic_play_light);

        Globals.getInstance().setNotifButton(false);
        stopForeground(false);
        Globals.getInstance().notifBuilder.setOngoing(false);
        Globals.getInstance().notifBuilder.setSmallIcon(R.drawable.ic_pause);
        Globals.getInstance().mNotifyMgr.notify(Globals.getInstance().NOTIFICATION_ID, Globals.getInstance().notifBuilder.build());
    }

    public void startForegroundNotification() {
        Globals.getInstance().buildNotification(this);
        Globals.getInstance().setNotifButton(true);
    }







// this is old


    private Notification assembleNotif() {
        String ns = Context.NOTIFICATION_SERVICE;
        Context ctx = Globals.getInstance().mainActivity;
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
        Intent radio = new Intent(Globals.getInstance().mainActivity, NotifReceiver.class);
        PendingIntent pRadio = PendingIntent.getActivity(Globals.getInstance().mainActivity, 0, radio, 0);
        contentView.setOnClickPendingIntent(R.id.radio, pRadio);

        notification.contentView = contentView;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        CharSequence contentTitle = "From Shortcuts";
        //mNotificationManager.notify(Globals.getInstance().NOTIFICATION_ID, notification);
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
