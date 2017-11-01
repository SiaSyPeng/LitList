package com.wabalub.cs65.litlist;


        import android.content.Context;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentPagerAdapter;

public class ActionbarPagerAdapter extends FragmentPagerAdapter {
    private final int PAGE_COUNT = 4;
    private String tabTitles[] = new String[] { "Play", "History", "Ranking", "Settings" };
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
}
