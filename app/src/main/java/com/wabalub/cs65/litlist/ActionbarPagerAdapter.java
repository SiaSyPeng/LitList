package com.wabalub.cs65.litlist;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.google.android.gms.maps.SupportMapFragment;

public class ActionbarPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "ACTIONBAR_ADAPTER";
    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Map", "Playlist", "Ranking", "Settings" };
    private Context context;


    ActionbarPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    /**
     * Calls specific fragments based on the position of the pressed tab
     * @param position The position
     * @return fragment for this tab
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment;

        switch (position) {
            case 0:
                fragment = MapFragment.newInstance();
                break;

            case 1:
                fragment = PlaylistFragment.newInstance();
                break;

            case 2:
                fragment = RankingFragment.newInstance();
                break;

            case 3:
                fragment = SettingsFragment.newInstance();
                break;

            default:
                fragment = BlankFragment.newInstance();
                break;
        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        Log.d(TAG, "Notified dataset changed!");
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }
}
