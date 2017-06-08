package com.example.oscar.radio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by oszi on 2/4/17.
 */

class Globals {
    private static Globals instance = null;

    protected Globals() {
    }

    public static Globals getInstance() {
        if(instance == null) {
            instance = new Globals();
        }

        return instance;
    }

    public MainActivity mainActivity;
    public SettingsFragment settingsFragment;
    public RadioService radioService;
    public radioPlayerService rPlayerService;
    public MediaPlayer mediaPlayer; // the mediaPlayer
    public NotificationCompat.Builder mBuilder;
    public boolean playing;
    public String url;
    public String[] urls = {
            "http://stream3.hit.hu:8080/speech",  // low
            "http://stream3.hit.hu:8080/low",     // mid
            "http://stream3.hit.hu:8080/high",    // high
            "http://79.172.239.251:8080/speech", // alternate low
            "http://79.172.239.251:8080/low",   // alternate mid
            "http://79.172.239.251:8080/high"  // alternate high
    };
    public int activeUrl;
    public boolean alternateUrl;
    public PendingIntent pIntent;
    public NotificationManager mNotifyMgr;
    public Handler handler = new Handler();
    public TextView titleText;
    public TextView artistText;
    public TextView programText;
    public TextView programDescText;
    public FloatingActionButton fab;
    public boolean finishedLoading;
    public Snackbar loadBar;
    public Snackbar errBar;
    public CardView musicCard;
    public CardView programCard;
    public List<Program> programs = new ArrayList<Program>();
    public List<MusicTitle> songs = new ArrayList<MusicTitle>();
    public List<News> news = new ArrayList<News>();
    public List<List<News>> newsRaw = new ArrayList<List<News>>();
    public MusicAdapter musicAdapter;
    public ProgramAdapter programAdapter[] = new ProgramAdapter[7];
    public RecyclerAdapter newsAdapter[] = new RecyclerAdapter[2];
    public RecyclerAdapter mainNewsAdapter;
    public SwipeRefreshLayout musicSwipeContainer;
    public SwipeRefreshLayout programSwipeContainer[] = new SwipeRefreshLayout[7];
    public SwipeRefreshLayout mainSwipeContainer;
    public SwipeRefreshLayout newsSwipeContainer[] = new SwipeRefreshLayout[2];
    public ImageView musicCardImage;
    public ImageView programCardImage;
    public NotificationCompat.Builder notifBuilder;
    public String musicTitle = "Hit Rádió Budapest";
    public String musicDesc = "Több, mint zene!";
    public boolean restartNeeded = false;
    public AppBarLayout appBarLayout;
    public TabLayout tabLayout;
    public int theme;

    // TEMP

    public TextView news_test_1_1;
    public TextView news_test_1_2;
    public ImageView news_test_1_image;

    public TextView news_test_2_1;
    public TextView news_test_2_2;
    public ImageView news_test_2_image;


    public final int NOTIFICATION_ID = 1;

    public final int ONE_SECOND = 1000;
    public final int TWO_SECONDS = 2000;
    public final int SEVEN_SECONDS = 7000;
    public final int TEN_SECONDS = 10000;
    public final int ONE_HOUR = 60*60*ONE_SECOND;
    public Intent radioServicePlayerIntent;

    public AudioManager am = null;
    //public static AudioManager.OnAudioFocusChangeListener afChangeListener;
    public AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        if(playing) {
                            stopRadio();
                            restartNeeded = true;
                        }

                        // Wait 30 seconds before stopping playback - not now
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        // Pause playback
                        if(playing) {
                            stopRadio();
                            restartNeeded = true;
                        }

                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Lower the volume, keep playing
                        if(playing) {
                            mediaPlayer.setVolume(0.5f, 0.5f);
                        }

                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        if (playing) {
                            mediaPlayer.setVolume(1.0f, 1.0f);
                        } else if (restartNeeded) { // only if the playback stopped because of gain loss
                            playRadio();    // starting the radio
                            restartNeeded = false;
                        }

                    }
                }
            };

    public BecomingNoisyReceiver myNoisyAudioStreamReceiver;


    public int getDay() {
        Date now = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int day = calendar.get(Calendar.DAY_OF_WEEK); // the day of the week in numerical format

        if (--day == 0)
            day = 7;

        return day;
    }


    /**
     * This function checks the music's title
     * @param title If the song's title
     * @return  If it's valid it returns with true
     */
    public boolean validSongTitle(String title) {
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

    public void setUrl(int index) {
        try {
            url = urls[index + ((alternateUrl) ? 3 : 0)];
            if(playing && activeUrl != index)
                radioService.restartStream();

            activeUrl = index;

            Log.d("URL", "setUrl: " + url);
        } catch (NullPointerException e) {
            // this is ok
        }
    }

    public void setUrl(boolean alternate) {
        try {
            url = urls[activeUrl + ((alternate) ? 3 : 0)];
            if(playing && alternate != alternateUrl)
                radioService.restartStream();

            alternateUrl = alternate;

            Log.d("URL", "setUrl: " + url);
        } catch (NullPointerException e) {
            // this is ok
        }
    }

    public boolean isNetworkOnline() {
        ConnectivityManager cm =
                (ConnectivityManager)mainActivity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

    }

    public void refreshSwypeContainerColor() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = Globals.getInstance().mainActivity.getApplicationContext().getTheme();
        theme.resolveAttribute(R.attr.colorAccent, typedValue, true);

        TypedValue a = new TypedValue();
        Globals.getInstance().mainActivity.getTheme().resolveAttribute(R.attr.colorAccent, a, true);
        int color = a.data;


        if (Globals.getInstance().mainSwipeContainer != null)
            Globals.getInstance().mainSwipeContainer.setColorSchemeColors(color);
        if(Globals.getInstance().musicSwipeContainer != null)
            Globals.getInstance().musicSwipeContainer.setColorSchemeColors(color);
        for(int i=0; i<7; ++i) {
            if(Globals.getInstance().programSwipeContainer[i] != null)
                Globals.getInstance().programSwipeContainer[i].setColorSchemeColors(color);
        }
    }

    public void buildNotification(Context ctx) {
        Intent notificationIntent = new Intent(ctx, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);


        notifBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(ctx)
                .setContentTitle(musicTitle)
                .setContentText(musicDesc)
                .setSmallIcon(R.drawable.ic_play)
                .setContentIntent(pendingIntent);


        rPlayerService.startForeground(NOTIFICATION_ID, notifBuilder.build());
    }

    public void setNotifButton(boolean play) {
        notifBuilder.mActions.clear();

        if(play) {
            notifBuilder.addAction(R.drawable.ic_pause, "Pause", pIntent);
            notifBuilder.setSmallIcon(R.drawable.ic_play);
        } else {
            notifBuilder.addAction(R.drawable.ic_play, "Play", pIntent);
            notifBuilder.setSmallIcon(R.drawable.ic_pause);
        }

        rPlayerService.startForeground(NOTIFICATION_ID, notifBuilder.build());
    }

    public void setNotifText(String Title, String Text) {
        notifBuilder.setContentTitle(Title);
        notifBuilder.setContentText(Text);

        rPlayerService.startForeground(NOTIFICATION_ID, notifBuilder.build());
    }

    public void playRadio() {
        if(am == null)
            am = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);

        // Request audio focus for playback
        int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // Start playback

            radioServicePlayerIntent = new Intent(mainActivity, radioPlayerService.class);
            mainActivity.startService(radioServicePlayerIntent);


            final IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

            mainActivity.registerReceiver(myNoisyAudioStreamReceiver, intentFilter);

        }
    }

    public void stopRadio() {    // TODO this
        mainActivity.stopService(radioServicePlayerIntent);
        restartNeeded = false;
        try {
            mainActivity.unregisterReceiver(myNoisyAudioStreamReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public int themeN = 0;
    public int themes[] = new int[6];
    public void setTheme() {
        theme = themes[themeN++];
        if (themeN == 6) themeN = 0;

        SharedPreferences mPrefs = Globals.getInstance().mainActivity.getSharedPreferences("asd", 0);
        SharedPreferences.Editor mEditor = mPrefs.edit();
        mEditor.putInt("theme", themeN).apply();

        mainActivity.recreate();
    }
}
