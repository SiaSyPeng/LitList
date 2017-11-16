package com.wabalub.cs65.litlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wabalub.cs65.litlist.gson.Playlist;
import com.wabalub.cs65.litlist.gson.Song;
import com.wabalub.cs65.litlist.MapFragment.OnFragmentInteractionListener;
import com.wabalub.cs65.litlist.my_libs.InternetMgmtLib.InternetListener;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.search.Player;
import com.wabalub.cs65.litlist.search.PlayerService;
import com.wabalub.cs65.litlist.search.Search;
import com.wabalub.cs65.litlist.search.SearchActivity;
import com.wabalub.cs65.litlist.search.SearchPager;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

import kaaes.spotify.webapi.android.SpotifyApi;

public final class MainActivity extends AppCompatActivity
        implements InternetListener, OnFragmentInteractionListener, OnListFragmentInteractionListener {
    private static final String TAG = "MAIN" ;
    private ActionbarPagerAdapter pagerAdapter;

    public static RequestQueue queue;
    public static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static String userID = null;
    public static String token = null;
    public static SpotifyApi spotifyApi;

    public static Playlist playlist = new Playlist(new ArrayList<Song>(), "", "");


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
        } else {
            logError("No valid access token");
        }

        Intent serviceIntent = PlayerService.getIntent(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
        else {
            startService(serviceIntent);
        }
    }

    /**
     * Method for handling selecting a song in the playlist
     * @param item song selected
     */
    public void onListFragmentInteraction(@Nullable Song item) {
    }

    /**
     * Method for handling selection of a fragment in the tab layout
     * @param uri uri of frag
     */
    public void onFragmentInteraction(@Nullable Uri uri) {
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

    public void onStartExploringClicked(View view) {

        //When the player is logged in we ask the player to play the Spotify track with the URI
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    public void onAddSongClicked(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
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
