package com.wabalub.cs65.litlist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.wabalub.cs65.litlist.MapFragment.OnFragmentInteractionListener;
import com.wabalub.cs65.litlist.my_libs.InternetMgmtLib.InternetListener;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;

public final class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        InternetListener, OnFragmentInteractionListener, OnListFragmentInteractionListener,
        SensorEventListener {
    private static final String TAG = "MAIN" ;
    public static ActionbarPagerAdapter pagerAdapter;
    private GoogleMap map;

    // for network requests and Spotify api
    public static RequestQueue queue;
    public static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static String userID = null;
    public static String token = null;
    public static SpotifyApi spotifyApi;
    public static SpotifyService spotifyService;

    // for playlist management

    //Todo: Come back here to look at the playlist will cause error
    public static Playlist playlist = new Playlist(new ArrayList<String>(), "", "");
    public static List<Track> tracks = new ArrayList<Track>();

    public static String USER_PREF = "profile_data";


    // for sensors
    private SensorManager sensorMgr;
    private Sensor accelerometer;
    private static final int SHAKE_THRESHOLD = 800;
    private long lastTime = 0, lastShakeTime = 0, TIME_THRESHOLD = 2000;
    private int samplingPeriod = 100000; // 10 ^ 5 us = 0.1 s
    private float last_x = 0f, last_y = 0f, last_z = 0f;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

        // setup the tab layout
        setupTabLayout();

        // setup the Spotify API and Player
        setupSpotifyAPI();

        // setup the sensors
        setupSensors();
    }

    /**
     * Method to setup the sensors for accelerometer shaking
     */
    private void setupSensors(){
        sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorMgr == null) {
            logError("Sensor manager is null.");
            return;
        }
        accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(this, accelerometer, samplingPeriod);
    }

    /**
     * Method to setup the tab layout
     */
    private void setupTabLayout(){
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int position = 0;
        if(extras != null) {
            position = extras.getInt("tab_position");
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerAdapter = new ActionbarPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);

        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Start on the only tab we have implemented
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.select();
    }

    /**
     * Method to setup the Spotify API
     */
    @TargetApi(Build.VERSION_CODES.O)
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

        try {
            UserPrivate me = spotifyService.getMe();
            logMessage("User ID: " + me.id);
            userID = me.id;
        } catch (Exception e){
            logError("Access token expired.");
        }

        startPlayerService();

    }

    /**
     * Method for handling selecting a song in the playlist
     * @param item song selected
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void onListFragmentInteraction(@Nullable Track item) {
        if(item == null) return;
        updateTracks();

        if (PlayerService.player == null) {
            logMessage("Player is null");
            return;
        }
        if(PlayerService.previewPlayer.isPlaying()) PlayerService.previewPlayer.pause();
        com.spotify.sdk.android.player.PlaybackState playbackState = PlayerService.player.getPlaybackState();
        if(playbackState != null && playbackState.isPlaying) PlayerService.player.pause(null);

        PlayerService.currentTrack = item;
        String currentTrackId = item.id;
        logMessage("currentTrackId: " + currentTrackId);

        startPlayerService();

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

    public static void updateTracks(){
        Log.d(TAG, "Updating tracks!");
        tracks = new ArrayList<>();

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

        CredentialsHandler.setToken(this, null, 0, TimeUnit.HOURS);

        //Go back to login
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }


    /**
     * On About Clicked,
     * Go to our gitlab page
     */
    public void about(View view){

        String url = "https://gitlab.cs.dartmouth.edu/wubalub/LitList";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//      finish();
    }

    /*
     * QR code clicked
     */
    public void qr(View view){

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long now = System.currentTimeMillis();
            long dTime = now - lastTime;
            lastTime = now;
            if(dTime < 50) return;

            float x = sensorEvent.values[SensorManager.DATA_X];
            float y = sensorEvent.values[SensorManager.DATA_Y];
            float z = sensorEvent.values[SensorManager.DATA_Z];

            float speed = Math.abs(x + y + z - last_x - last_y - last_z) / dTime * 10000;
            // Log.d(TAG, "speed = " + speed);

            // if the speed is past the threshold
            if (speed > SHAKE_THRESHOLD && now - lastShakeTime > TIME_THRESHOLD) {
                lastShakeTime = now;
                onShake();
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    private void onShake(){
        Log.d("sensor", "shake detected");
        Toast.makeText(this, "shake detected ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void onShareClicked(View view) {
    }

    private void startPlayerService(){
        Intent serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.putExtra(EXTRA_TOKEN, token);

        // we need a foreground service for oreo and up so the service doesn't get killed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }
}
