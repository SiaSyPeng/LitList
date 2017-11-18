package com.wabalub.cs65.litlist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.wabalub.cs65.litlist.gson.Playlist;
import com.wabalub.cs65.litlist.MapFragment.OnFragmentInteractionListener;
import com.wabalub.cs65.litlist.my_libs.InternetMgmtLib.InternetListener;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;

public final class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        InternetListener, OnFragmentInteractionListener, OnListFragmentInteractionListener {
    private static final String TAG = "MAIN" ;
    public static ActionbarPagerAdapter pagerAdapter;
    private GoogleMap map;

    public static RequestQueue queue;
    public static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static String userID = null;
    public static String token = null;
    public static SpotifyApi spotifyApi;
    public static SpotifyService spotifyService;

    public static Playlist playlist = new Playlist(new ArrayList<String>(), "", "");
    public static List<Track> tracks = new ArrayList<Track>();

    public static String USER_PREF = "profile_data";



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        //setup the tab layout
        setupTabLayout();

        // setup the Spotify API and Player
        setupSpotifyAPI();
    }

    /**
     * Method to setup the tab layout
     */
    private void setupTabLayout(){
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ActionbarPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);

        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Start on the only tab we have implemented
        TabLayout.Tab tab = tabLayout.getTabAt(0);
        assert tab != null;
        tab.select();
    }

    /**
     * Method to setup the Spotify API
     */
    private void setupSpotifyAPI(){
        Intent intent = getIntent();
        token = intent.getStringExtra(EXTRA_TOKEN);
        logMessage("Api Client created");
        spotifyApi = new SpotifyApi();

        if (token != null) {
            spotifyApi.setAccessToken(token);
            spotifyService = MainActivity.spotifyApi.getService();
        } else {
            logError("No valid access token");
        }

        // allows the get me request to work
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //UserPrivate me = spotifyService.getMe();
        //logMessage("User ID: " + me.id);
        //userID = me.id;

        Intent serviceIntent = PlayerService.getIntent(this);
        startService(serviceIntent);
    }

    /**
     * Method for handling selecting a song in the playlist
     * @param item song selected
     */
    public void onListFragmentInteraction(@Nullable Track item) {
        if(item == null) return;
        updateTracks();

        String url = item.uri;
        if (PlayerService.player == null) {
            logMessage("Player is null");
            return;
        }
        if(PlayerService.previewPlayer.isPlaying()) PlayerService.previewPlayer.pause();
        if(PlayerService.player.getPlaybackState().isPlaying) PlayerService.player.pause(null);

        PlayerService.currentTrack = item;
        String currentTrackId = item.id;
        logMessage("currentTrackId: " + currentTrackId);
        PlayerService.player.playUri(null, "spotify:track:" + currentTrackId, 0, 0);
    }

    /**
     * Method for handling selection of a fragment in the tab layout
     * @param uri uri of frag
     */
    public void onFragmentInteraction(@Nullable Uri uri) {
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        // Add a marker in Sydney and move the camera
    }

    /**
     * Method to handle error responses form the server
     * @param requestCode request code
     * @param res response
     */
    public void onErrorResponse(int requestCode, @Nullable String res) {
    }

    /**
     * Method to handle responses form the server
     * @param requestCode request code
     * @param res response
     */
    public void onResponse(int requestCode, @Nullable String res) {
    }

    public void onAddSongClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /**
     * Method to setup or update the notification
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateNotification(){
        Log.d(TAG, "Updating notification");
        //TODO get the track title form the url
        String title = PlayerService.currentTrack.name;

        // setup a pending intent so if the user clicks on the notification it can open the main activity
        PendingIntent resultPendingIntent = getPendingIntent();

        // build the notification
        Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Playing " + title)
                        .setContentText("Artist")
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(true)
                        .setContentIntent(resultPendingIntent);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(PlayerService.NOTIFICATION_ID, notification);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private PendingIntent getPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Gets a PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    private void updateTracks(){
        tracks = new ArrayList<Track>();

        for(String id : playlist.getIds()){
            tracks.add(spotifyService.getTrack(id));
        }
    }

    /**
     * On signout Clicked,
     * Clear all the data and go back to sign in
     */
    public void signOut(View view){

        //Remove everything from user sharedPrefs
        SharedPreferences sp = getSharedPreferences(MainActivity.USER_PREF, 0);
        sp.edit().clear().apply();

        //Go back to login
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
