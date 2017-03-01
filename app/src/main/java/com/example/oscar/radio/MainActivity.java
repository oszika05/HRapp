package com.example.oscar.radio;

import android.app.ActionBar;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;



public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static NotificationCompat.Builder mBuilder;
    public static Intent toggleIntent;
    public static PendingIntent pToggleIntent;
    public static NotificationManager mNotifyMgr;
    private static ListView songsListView;  // TODO fix this
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private void init() {

        // getting the saved values (also defaulting, if no config exist yet)
        SharedPreferences mPrefs = getSharedPreferences("asd", 0);
        Globals.getInstance().activeUrl = mPrefs.getInt("quality", 0);    // default: speech
        Globals.getInstance().alternateUrl = mPrefs.getBoolean("alternate", false);   // default: no alternative
        Globals.getInstance().url = Globals.getInstance().urls[Globals.getInstance().activeUrl + (Globals.getInstance().alternateUrl ? 3 : 0)];


        Globals.getInstance().appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);

        Globals.getInstance().mainSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_main);
        if(Globals.getInstance().isNetworkOnline())
            Globals.getInstance().mainSwipeContainer.setRefreshing(true); // the first init
        Globals.getInstance().mainSwipeContainer.setEnabled(true);

        Globals.getInstance().mainSwipeContainer.setColorSchemeResources(R.color.colorAccent);

        Globals.getInstance().titleText = (TextView) findViewById(R.id.title);
        Globals.getInstance().artistText = (TextView) findViewById(R.id.artist);
        Globals.getInstance().programText = (TextView) findViewById(R.id.programTitle);
        Globals.getInstance().programDescText = (TextView) findViewById(R.id.programDescription);
        Globals.getInstance().musicCardImage = (ImageView) findViewById(R.id.thumbnail_music);
        Globals.getInstance().programCardImage = (ImageView) findViewById(R.id.thumbnail_program);
        Globals.getInstance().musicCard = (CardView) findViewById(R.id.card_view_music);
        Globals.getInstance().programCard = (CardView) findViewById(R.id.card_view_program);
        Globals.getInstance().noInternetCard = (CardView) findViewById(R.id.card_view_nointernet);

        Globals.getInstance().noInternetCard.setVisibility(Globals.getInstance().isNetworkOnline() ? View.GONE : View.VISIBLE);

        Picasso.with(getApplicationContext()).load(R.drawable.internet2).into((ImageView)findViewById(R.id.thumbnail_nointernet));

        Globals.getInstance().finishedLoading = true;

        //((NavigationView) findViewById(R.id.nav_view)).setItemBackgroundResource(R.drawable.cover_hitradio);

        toggleIntent = new Intent(this, NotifReceiver.class);
        Globals.getInstance().pIntent =  PendingIntent.getBroadcast(this, 0, toggleIntent, 0);;

        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Globals.getInstance().mNotifyMgr = mNotifyMgr;
        Globals.getInstance().mBuilder = mBuilder;

        Globals.getInstance().fab = (FloatingActionButton) findViewById(R.id.fab);
        if(Globals.getInstance().playing)
            Globals.getInstance().fab.setImageResource(R.drawable.ic_pause_light);

        Globals.getInstance().fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Globals.getInstance().loadBar==null) { // init the snackbar
                    Globals.getInstance().loadBar = Snackbar.make(view, "Pufferelés...", Snackbar.LENGTH_INDEFINITE);
                }

                Globals.getInstance().errBar = Snackbar.make(view, "Hiba! Ellenőrizze az internetkapcsolatát!", Snackbar.LENGTH_LONG);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


                if (!Globals.getInstance().playing) {
                    if (!Globals.getInstance().isNetworkOnline()) {
                        Globals.getInstance().errBar.show();
                        return;
                    }

                    if (Globals.getInstance().programs.size()==0)
                        Globals.getInstance().radioService.downloadHtml();    // this refreshes the programs


                    if(!Globals.getInstance().finishedLoading)
                        return;

                    //Globals.getInstance().radioService.startStream(Globals.getInstance().errBar); // starting the stream
                    // TODO this + notification buttons
                    Globals.getInstance().playRadio();

                    //startNotification();    // showing the notification

                    Globals.getInstance().loadBar.show();     // showing snackbar

                    fab.setImageResource(R.drawable.ic_pause_light);    // changing the icon of the fab button
                } else {
                    Globals.getInstance().loadBar.dismiss();
                    Globals.getInstance().stopRadio();
                    //stopNotification();   // removing the notification
                    fab.setImageResource(R.drawable.ic_play_light);     // changing the icon of the fab button
                }

                //Globals.getInstance().radioService.setNotificationButton();
            }
        });

        CardView face = (CardView) findViewById(R.id.card_view_facebook);
        face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://www.facebook.com/hitradio.hu");
                try {
                    ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                    if (applicationInfo.enabled) {
                        // http://stackoverflow.com/a/24547437/1048340
                        uri = Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/hitradio.hu");
                    }

                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException ignored) {    // if there is no facebook app, we open the browser
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/hitradio.hu"));
                    startActivity(i);
                }

            }
        });

        CardView mail = (CardView) findViewById(R.id.card_view_mail);
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"info@hitradio.hu"});
                try {
                    startActivity(Intent.createChooser(i, "Email küldése..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Nincs email kliens telepítve!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CardView phone = (CardView) findViewById(R.id.card_view_phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+3614317010"));
                try {
                    startActivity(callIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "Hiba történt!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Globals.getInstance().musicSwipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer_music);
        // Setup refresh listener which triggers new data loading
        Globals.getInstance().musicSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

                try {
                    if(Globals.getInstance().isNetworkOnline())
                        Globals.getInstance().radioService.refreshSongsHTML();
                    //Globals.getInstance().musicAdapter.refresh();
                    //((MusicAdapter) songsListView.getAdapter()).refresh();
                } catch (NullPointerException e) {  // TODO error
                    // this is ok
                    Globals.getInstance().musicSwipeContainer.setRefreshing(false);
                }
            }
        });
/* UNUSED
        final IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        final BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();

        MediaSessionCompat.Callback callback = new  // TODO call it sometime
                MediaSessionCompat.Callback() {
                    @Override
                    public void onPlay() {
                        registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                    }

                    @Override
                    public void onStop() {
                        unregisterReceiver(myNoisyAudioStreamReceiver);
                    }
                };
*/


        // Configure the refreshing colors TODO this
        Globals.getInstance().musicSwipeContainer.setColorSchemeResources(R.color.colorAccent);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);  // the volume control is controlling the media playback, not he ringtone

    }

    private void initOnce() {
        Globals.getInstance().mainActivity = this;
        Globals.getInstance().playing = false;

        mBuilder = new NotificationCompat.Builder(this);
        Globals.getInstance().radioService = new RadioService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        try {
            Globals.getInstance().playing = Globals.getInstance().mediaPlayer.isPlaying();
        } catch (NullPointerException e) {
            Globals.getInstance().playing = false;
        }

        Globals.getInstance().settingsFragment = new SettingsFragment();

        if(!Globals.getInstance().playing)
            initOnce();
        else
            Globals.getInstance().radioService = new RadioService();

        init();

        if(!Globals.getInstance().playing) {
            Globals.getInstance().radioService.refreshMetaData(); // this refreshes the metadata in every 2 sec
            if(Globals.getInstance().isNetworkOnline())
                Globals.getInstance().radioService.downloadHtml();    // this refreshes the programs
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
/*
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Intent intentSetPref = new Intent(getApplicationContext(), SettingsActivity.class);
            Intent intentSetPref = new Intent(getApplicationContext(), SettingsS.class);
            startActivityForResult(intentSetPref, 0);

            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_radio) {
            Picasso.with(getApplicationContext()).load(R.drawable.internet2).into((ImageView)findViewById(R.id.thumbnail_nointernet));

            changeViewVisibility(R.id.t1);
        } else if (id == R.id.nav_program) {
            if(!Globals.getInstance().isNetworkOnline()) {
                Globals.getInstance().errBar = Snackbar.make(findViewById(R.id.drawer_layout), "Hiba! Ellenőrizze az internetkapcsolatát!", Snackbar.LENGTH_LONG);
                Globals.getInstance().errBar.show();
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                return false;
            }

            String[] dayArr = {"Hétfő", "Kedd", "Szerda", "Csütörtök", "Péntek", "Szombat", "Vasárnap"};
            // TODO

            Globals.getInstance().tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            Globals.getInstance().tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

            // Removing tabs
            Globals.getInstance().tabLayout.removeAllTabs();
            // Adding Tabs
            for (String tab_name : dayArr) {
                Globals.getInstance().tabLayout.addTab(Globals.getInstance().tabLayout.newTab().setText(tab_name));
            }


            final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
            final TabsPagerAdapter adapter = new TabsPagerAdapter
                    (getSupportFragmentManager());
            viewPager.setAdapter(adapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(Globals.getInstance().tabLayout));
            Globals.getInstance().tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            try {
                Globals.getInstance().tabLayout.getTabAt(Globals.getInstance().getDay() - 1).select();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }




            changeViewVisibility(R.id.t2);

/*
            if(Globals.getInstance().programs == null)
                return false;

            for(int i=0; i<Globals.getInstance().programs.size(); i++) {  // todo clickable days
                Globals.getInstance().programs.get(i).getName();
            }

            Globals.getInstance().programAdapter = new ProgramAdapter(this, (ArrayList<Program>) Globals.getInstance().programs);

            ListView listView = (ListView) findViewById(R.id.program_list);
            listView.setAdapter(Globals.getInstance().programAdapter);

            if(Globals.getInstance().programs.size() == 0) {  // the list is not yet loaded
                Globals.getInstance().programSwipeContainer.setRefreshing(true);  // the refreshing icon

                Globals.getInstance().radioService.downloadHtml();    // getting the programs
            }


            changeViewVisibility(R.id.t2);
*/
        } else if (id == R.id.nav_contact) {

            changeViewVisibility(R.id.t3);
        } else if (id == R.id.nav_music) {
            if(!Globals.getInstance().isNetworkOnline()) {
                Globals.getInstance().errBar = Snackbar.make(findViewById(R.id.drawer_layout), "Hiba! Ellenőrizze az internetkapcsolatát!", Snackbar.LENGTH_LONG);
                Globals.getInstance().errBar.show();
                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawers();
                return false;
            }

            Globals.getInstance().musicAdapter = new MusicAdapter(getApplicationContext(), (ArrayList<MusicTitle>) Globals.getInstance().songs);

            songsListView = (ListView) findViewById(R.id.music_list);
            songsListView.setAdapter(Globals.getInstance().musicAdapter);

            if(Globals.getInstance().songs.size() == 0) {  // the list is not yet loaded TODO no internet
                Globals.getInstance().musicSwipeContainer.setRefreshing(true);  // the refreshing icon

                Globals.getInstance().radioService.refreshSongsHTML();
            }

            changeViewVisibility(R.id.t4);
        } else if (id == R.id.nav_settings) {
            Intent intentSetPref = new Intent(getApplicationContext(), SettingsS.class);
            startActivityForResult(intentSetPref, 0);

            return false;   // no highlight
            // TODO
        } else if (id == R.id.nav_info) {
            Intent i = new Intent();
            i.setClass(this, Textactivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle b = new Bundle();
            b.putString("title", "Hit Rádió Budapest"); // Title
            b.putString("type", "Az appról"); // Type
            b.putString("desc", "Ez a hitrádió alkalmazása...."); // Description
            b.putString("time", "v0.8"); // Time
            b.putInt("cover", R.drawable.cover_hitradio_cropped2); // Cover Photo
            i.putExtras(b); // Put your id to your next Intent


            try {
                startActivity(i);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            // TODO
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void startNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent toggleIntent = new Intent(this, NotifReceiver.class);
        PendingIntent pToggleIntent = PendingIntent.getBroadcast(this, 0, toggleIntent, 0);

        mBuilder.setSmallIcon(R.drawable.ic_play);
        mBuilder.setContentTitle("Hit Rádió Budapest");
        mBuilder.setContentText("Hit Rádió! Több, mint zene!");
        mBuilder.setOngoing(true);
        mBuilder.setContentIntent(pIntent);
        mBuilder.addAction(R.drawable.ic_pause, "Pause", pToggleIntent);


        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(Globals.getInstance().NOTIFICATION_ID, mBuilder.build());
    }

    public void stopNotification() {
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(Globals.getInstance().NOTIFICATION_ID);
    }
    
    private void changeViewVisibility(int id) {
        // set all content to GONE
        findViewById(R.id.t1).setVisibility(View.GONE);
        findViewById(R.id.t2).setVisibility(View.GONE);
        findViewById(R.id.t3).setVisibility(View.GONE);
        findViewById(R.id.t4).setVisibility(View.GONE);

        if(Build.VERSION.SDK_INT >= 21 && Globals.getInstance().appBarLayout != null) {
            if (id == R.id.t2) {
                Globals.getInstance().appBarLayout.setElevation(0.0f);
            } else {
                Globals.getInstance().appBarLayout.setElevation(10.5f); // the original value
            }
        }

        findViewById(id).setVisibility(View.VISIBLE);
    }

}
