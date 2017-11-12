package com.wabalub.cs65.litlist;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.wabalub.cs65.litlist.GsonClasses.Playlist;
import com.wabalub.cs65.litlist.GsonClasses.Song;
import com.wabalub.cs65.litlist.MapFragment.OnFragmentInteractionListener;
import com.wabalub.cs65.litlist.MyLibs.InternetMgmtLib.InternetListener;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MainActivity extends AppCompatActivity
        implements InternetListener, OnFragmentInteractionListener, OnListFragmentInteractionListener {
    private ActionbarPagerAdapter pagerAdapter;
    private RequestQueue queue;

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
    }
    public void onStartExploringClicked(View view) {
        this.setIntent(new Intent(this, MapActivity.class));
        this.startActivity(this.getIntent());
    }
}
