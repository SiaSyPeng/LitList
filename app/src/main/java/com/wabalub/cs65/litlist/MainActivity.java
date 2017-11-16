package com.wabalub.cs65.litlist;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wabalub.cs65.litlist.gson.Playlist;
import com.wabalub.cs65.litlist.gson.Song;
import com.wabalub.cs65.litlist.MapFragment.OnFragmentInteractionListener;
import com.wabalub.cs65.litlist.my_libs.InternetMgmtLib.InternetListener;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.search.Player;
import com.wabalub.cs65.litlist.search.SearchActivity;

import java.util.ArrayList;

import org.jetbrains.annotations.Nullable;

public final class MainActivity extends AppCompatActivity
        implements InternetListener, OnFragmentInteractionListener, OnListFragmentInteractionListener {
    private ActionbarPagerAdapter pagerAdapter;
    private RequestQueue queue;
    public static final String EXTRA_TOKEN = "EXTRA_TOKEN";
    public static String token = null;

    public static Player player;


    public static Playlist playlist = new Playlist(new ArrayList<Song>(), "");

    public void onListFragmentInteraction(@Nullable Song item) {
    }

    public void onFragmentInteraction(@Nullable Uri uri) {
    }

    public void onErrorResponse(int requestCode, @Nullable String res) {
    }

    public void onResponse(int requestCode, @Nullable String res) {
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        queue = Volley.newRequestQueue(this);

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

        // setup the player
        Intent intent = getIntent();
        token = intent.getStringExtra(EXTRA_TOKEN);
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
}
