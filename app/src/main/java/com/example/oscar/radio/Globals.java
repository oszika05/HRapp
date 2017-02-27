package com.example.oscar.radio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oszi on 2/4/17.
 */

public final class Globals {
    public static MainActivity mainActivity;    // TODO this is bad
    public static SettingsFragment settingsFragment;
    public static RadioService radioService;
    public static radioPlayerService rPlayerService;
    public static MediaPlayer mediaPlayer; // the mediaPlayer
    public static NotificationCompat.Builder mBuilder;
    public static boolean playing;
    public static String url;
    public static String[] urls = {
            "http://live7.hit.hu:8080/speech",  // low
            "http://live7.hit.hu:8080/low",     // mid
            "http://live7.hit.hu:8080/high",    // high
            "http://194.38.107.136:8080/speech", // alternate low
            "http://194.38.107.136:8080/low",   // alternate mid
            "http://194.38.107.136:8080/high"  // alternate high
    };
    public static int activeUrl;
    public static boolean alternateUrl;
    public static PendingIntent pIntent;
    public static NotificationManager mNotifyMgr;
    public static Handler handler = new Handler();
    public static TextView titleText;
    public static TextView artistText;
    public static TextView programText;
    public static TextView programDescText;
    public static FloatingActionButton fab;
    public static boolean finishedLoading;
    public static Snackbar loadBar;
    public static Snackbar errBar;
    public static CardView musicCard;
    public static CardView programCard;
    public static CardView noInternetCard;
    public static List<Program> programs = new ArrayList<Program>();
    public static List<MusicTitle> songs = new ArrayList<MusicTitle>();
    public static MusicAdapter musicAdapter;
    public static ProgramAdapter programAdapter;
    public static SwipeRefreshLayout musicSwipeContainer;
    public static SwipeRefreshLayout programSwipeContainer;
    public static SwipeRefreshLayout mainSwipeContainer;
    public static ImageView musicCardImage;
    public static ImageView programCardImage;
    public static NotificationCompat.Builder notifBuilder;
    public static String musicTitle = "Hit Rádió Budapest";
    public static String musicDesc = "Több, mint zene!";

    public final static int NOTIFICATION_ID = 1;

    public final static int ONE_SECOND = 1000;
    public final static int TWO_SECONDS = 2000;
    public final static int SEVEN_SECONDS = 7000;
    public final static int TEN_SECONDS = 10000;
    public final static int ONE_HOUR = 60*60*ONE_SECOND;
    public static Intent radioServicePlayerIntent;


    /**
     * This function checks the music's title
     * @param title If the song's title
     * @return  If it's valid it returns with true
     */
    public static boolean validSongTitle(String title) {
        switch(title) {
            case "Hírek":
            case "REGGELI MUSORVEZETO":
            case "HÍREK BLOKK ELEMEI":
            case "MUSORVEZETO":
            case "HÍREK HEADLINE":
            case "Egyéb":
            case "":
                return false;
            default:
                return true;
        }
    }

    public static void setUrl(int index) {
        try {
            url = urls[index + ((alternateUrl) ? 3 : 0)];
            if(Globals.playing && activeUrl != index)
                radioService.restartStream();

            activeUrl = index;

            Log.d("URL", "setUrl: " + url);
        } catch (NullPointerException e) {
            // this is ok
        }
    }

    public static void setUrl(boolean alternate) {
        try {
            url = urls[activeUrl + ((alternate) ? 3 : 0)];
            if(Globals.playing && alternate != alternateUrl)
                radioService.restartStream();

            alternateUrl = alternate;

            Log.d("URL", "setUrl: " + url);
        } catch (NullPointerException e) {
            // this is ok
        }
    }

    public static boolean isNetworkOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)mainActivity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public static void buildNotification(Context ctx) {
        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);


        notifBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ctx)
                .setContentTitle(musicTitle)
                .setContentText(musicDesc)
                .setSmallIcon(R.drawable.ic_play)
                .setContentIntent(pendingIntent);


        rPlayerService.startForeground(Globals.NOTIFICATION_ID, notifBuilder.build());
    }

    public static void setNotifButton(boolean play) {
        notifBuilder.mActions.clear();

        if(play) {
            notifBuilder.addAction(R.drawable.ic_pause, "Pause", Globals.pIntent);
            notifBuilder.setSmallIcon(R.drawable.ic_play);
        } else {
            notifBuilder.addAction(R.drawable.ic_play, "Play", Globals.pIntent);
            notifBuilder.setSmallIcon(R.drawable.ic_pause);
        }

        rPlayerService.startForeground(Globals.NOTIFICATION_ID, notifBuilder.build());
    }

    public static void setNotifText(String Title, String Text) {
        notifBuilder.setContentTitle(Title);
        notifBuilder.setContentText(Text);

        rPlayerService.startForeground(Globals.NOTIFICATION_ID, notifBuilder.build());
    }
}