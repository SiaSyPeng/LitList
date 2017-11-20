package com.wabalub.cs65.litlist;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wabalub.cs65.litlist.MapFragment.OnFragmentInteractionListener;
import com.wabalub.cs65.litlist.gson.FPlaylist;
import com.wabalub.cs65.litlist.gson.FPlaylists;
import com.wabalub.cs65.litlist.gson.Song;
import com.wabalub.cs65.litlist.my_libs.InternetMgmtLib.InternetListener;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.search.SearchActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jetbrains.annotations.Nullable;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.UserPrivate;

public final class MainActivity extends AppCompatActivity implements
        InternetListener,
        OnFragmentInteractionListener,
        OnListFragmentInteractionListener,
        SensorEventListener,
        AdapterView.OnItemSelectedListener,
        OnMapReadyCallback,
        LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener{

    private static final String TAG = "MAIN" ;
    public static String USER_PREF = "profile_data";
    public static String PLAYLIST_ID_PREF = "playlist_id";
    public static DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    // for network requests and Spotify api
    public static RequestQueue queue;

    private ViewPager viewPager;
    private ActionbarPagerAdapter pagerAdapter;

    // TODO is this not imported correctly?
    // public static DatabaseReference database;

    public static String SHARED_PREF = "litlist_" + "shared_pref";

    // for playlist management

    //Todo: Come back here to look at the playlist will cause error
    public static FPlaylist playlist = null;
    public static FPlaylists playlists = null;
    public static List<Track> tracks = new ArrayList<>();
    public static int playlistIndex;

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

        // setup the playlist
        setupPlaylist();
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
        viewPager = findViewById(R.id.viewpager);
        pagerAdapter = new ActionbarPagerAdapter(getSupportFragmentManager(),
                MainActivity.this);

        viewPager.setAdapter(pagerAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Start on the only tab we have implemented
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.select();
    }

    private void setupPlaylist(){
        // TODO get playlist from database
        playlist = new FPlaylist("name", "cerator", null, 0.0, 43.1939, 71.5724, new ArrayList<Song>(), new ArrayList<String>(), 0);
    }
    /*
    ================================================================================================
    Interaction listeners
    ================================================================================================
     */

    public void onShareClicked(View view) {
    }

    public void onMuteClicked(View view) {
        ImageButton playPauseButton = view.findViewById(R.id.mute_button);

        if(PlayerService.previewPlayer == null) return;
        if(!PlayerService.muted) {
            PlayerService.mute();
            playPauseButton.setImageResource(R.drawable.mute);
        }
        else {
            PlayerService.unmute();
            playPauseButton.setImageResource(R.drawable.unmute);
        }
    }

    /**
     * Listener for the alert type spinner
     * @param adapterView the view for the adapter
     * @param view the view
     * @param i position
     * @param l code
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //TODO switch alert type, save to shared pref
    }

    /**
     * For alert type spinner
     * @param adapterView adapter view
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * On signout Clicked,
     * Clear all the data and go back to sign in
     */
    public void onSignOutClicked(View view){

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
    public void onAboutClicked(View view){

        String url = "https://gitlab.cs.dartmouth.edu/wubalub/LitList";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//      finish();
    }

    /**
     * QR code clicked
     * @param view view clicked in
     */
    public void onQRClicked(View view){

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

    public void onAddSongClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    /*
    ================================================================================================
    For Sensors
    ================================================================================================
     */
    private static final int SHAKE_THRESHOLD = 800;
    private long lastTime = 0, lastShakeTime = 0, TIME_THRESHOLD = 2000;
    private int samplingPeriod = 100000; // 10 ^ 5 us = 0.1 s
    private float last_x = 0f, last_y = 0f, last_z = 0f;

    /**
     * Method to setup the sensors for accelerometer shaking
     */
    private void setupSensors(){
        SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorMgr == null) {
            logError("Sensor manager is null.");
            return;
        }
        Sensor accelerometer = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMgr.registerListener(this, accelerometer, samplingPeriod);
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


    /*
    ================================================================================================
    for Google maps
    ================================================================================================
     */
    private GoogleMap map;
    private Marker[] playlistMarkers;
    private Marker myPosMarker;
    private LatLng currentPos;
    public static float zoom = 10;
    private boolean zoomedOut = true;
    public static boolean permissionsGranted = true;
    public static boolean setup;
    private MapFragment mapFragment = null;

    FPlaylist closestPlaylist = null;

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
        logMessage("Map is ready!");
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnMarkerClickListener(this);

        getLocation();
        getPlaylists();
        setupPlaylistMarkers();
        moveToCurrentLocation(currentPos);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if(fragment instanceof MapFragment){
            mapFragment = ((MapFragment) fragment);
        }
    }

    /**
     * Method to get a list of playlists from the server
     */
    private void getPlaylists() {
        // TODO get the playlists from the database
    }


    /**
     * Method to make all of the playlist markers
     */
    private void setupPlaylistMarkers() {
        if(playlists == null) {
            logError("No playlists");
            return;
        }

        if(playlistMarkers != null)
            for(Marker marker : playlistMarkers)
                marker.remove();

        playlistMarkers = new Marker[playlists.playlists.size()];

        Double closestDist = Double.POSITIVE_INFINITY;


        for(int i = 0; i < playlists.playlists.size(); i++){

            FPlaylist playlist = playlists.playlists.get(i);
            LatLng l = new LatLng(playlist.lat, playlist.lon);

            // calculate the distance (for figuring out which cat is the closest)
            Double dist = dist(l, currentPos);
            if(dist < closestDist){
                closestPlaylist = playlist;
                closestDist = dist;
            }


            // add the marker for the playlist
            Bitmap catMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.fire);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                    catMarkerIcon, 100, 125, false);
            playlistMarkers[i] = map.addMarker(new MarkerOptions().position(l).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

        }
    }

    /**
     * Method to calculate the distance in miles between two locations
     * @param l1 latlng 1
     * @param l2 latlng 2
     * @return distance in miles
     */
    public static double dist(LatLng l1, LatLng l2){
        if(l1 == null || l2 == null) return 0.0;
        return 69.0 * Math.sqrt((l1.latitude - l2.latitude) * (l1.latitude - l2.latitude)
                + (l1.longitude - l2.longitude) * (l1.longitude - l2.longitude));
    }


    @Override
    public void onLocationChanged(Location location) {
        updateWithNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        getLocation();
        moveToCurrentLocation(currentPos);
    }

    @Override
    public void onProviderDisabled(String provider) { }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // TODO get the playlist whose marker is at this position

        MapFragment mapFragment = (MapFragment) pagerAdapter.getItem(0);
        mapFragment.updatePanel();
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        logMessage("Map clicked!");
        MapFragment mapFragment = (MapFragment) pagerAdapter.getItem(0);
        mapFragment.updatePanel();
        playlist = null;
    }

     // You NEED to first check for location permissions before using the location.
    // Make sure you declare the corresponding permission in your manifest.
    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Criteria criteria = getCriteria();
            String provider;
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (locationManager != null) {
                provider = locationManager.getBestProvider(criteria, true);
                Location l = locationManager.getLastKnownLocation(provider);

                if(l!=null){
                    Log.d(TAG, "location: " + l.toString());
                    updateWithNewLocation(l);
                }
                else{
                    Log.e(TAG, "location is null");
                }
                locationManager.requestLocationUpdates(provider, 0, 0, this);
            }
            else{
                Log.e(TAG, "location manager is null");
            }
        }
    }

    // Application criteria for selecting a location provider. See line 158 "getBestProvider"
    // https://developer.android.com/reference/android/location/Criteria.html
    private Criteria getCriteria(){
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        return criteria;
    }

        // Put the marker at given location and zoom into the location
    private void updateWithNewLocation(Location location) {
        if (location != null) {
            LatLng l = fromLocationToLatLng(location);
            currentPos = l;
            drawMyMarker(l);
            moveToCurrentLocation(l);
        }
    }

    // LatLng stores the "location" as two doubles, latitude and longitude.
    public static LatLng fromLocationToLatLng(Location location){
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

    // Remove old marker and place new marker.
    private void drawMyMarker(LatLng l){
        if (myPosMarker != null)
            myPosMarker.remove();
        myPosMarker = map.addMarker(new MarkerOptions()
                .position(l)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_self)));
    }

    /**
     * Method to zoom camera to current location
     * @param currentLocation our current location
     */
    private void moveToCurrentLocation(LatLng currentLocation)
    {
        if(currentLocation == null) {
            Log.d(TAG, "current location is null");
            return;
        }

        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        if(!bounds.contains(currentLocation) || zoomedOut ){
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
            // Zoom in, animating the camera.
            map.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 1 second.
            map.animateCamera(CameraUpdateFactory.zoomTo(zoom), 1000, null);
            zoomedOut = false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissions result");
        switch (requestCode) {
            case MapFragment.MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED)) {
                    // permissions not obtained
                    Toast.makeText(this,"failed request permission!", Toast.LENGTH_SHORT).show();
                    permissionsGranted = false;
                }
                else {
                    if(mapFragment != null) mapFragment.setupMap();
                    permissionsGranted = true;
                }
            }
        }
    }


    /*
    ================================================================================================
    for Spotify API
    ================================================================================================
     */

    public static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static String userID = null;
    public static String userEmail = null;
    public static String token = null;
    public static SpotifyApi spotifyApi;

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
            PlayerService.spotifyService = MainActivity.spotifyApi.getService();
        } else {
            logError("No valid access token");
        }

        // allows the get me request to work
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            UserPrivate me = PlayerService.spotifyService.getMe();
            logMessage("User ID: " + me.id);
            userID = me.id;
            userEmail = me.email;
        } catch (Exception e){
            logError("Access token expired.");
            SharedPreferences sp = getSharedPreferences(SHARED_PREF, 0);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CredentialsHandler.ACCESS_TOKEN, null);
            editor.apply();
        }

        startPlayerService();

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

    /*
    ================================================================================================
    For Facebook API
    ================================================================================================
     */

    /*
    ================================================================================================
    For general networking
    ================================================================================================
     */
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


    /*
    ================================================================================================
    General utility
    ================================================================================================
     */

    /**
     * Method to get the pending intent for starting this activity
     * @return pending intent tos tart the activity
     */
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

    private void savePlaylistIdInSharedPred(){
        if(playlist == null) {
            logError("Playlist is null, cannot be saved");
            return;
        }

        SharedPreferences sp = getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(PLAYLIST_ID_PREF, playlist.index);
        editor.apply();
    }
    public static void updateTracks(){
        if(playlist == null) return;

        Log.d(TAG, "Updating tracks!");
        tracks = new ArrayList<>();

        for(Song song : playlist.songs){
            String id = song.id;
            tracks.add(PlayerService.spotifyService.getTrack(id));
        }
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }
}
