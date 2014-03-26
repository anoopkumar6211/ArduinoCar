package com.barunster.arduinocar.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;

import com.barunster.arduinocar.fragments.top_menu.MenuFragment;

import java.util.List;

/**
 * Created by itzik on 3/24/14.
 */
public class MenuFragmentPageAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    /**
     * @param fm
     * @param fragments
     */
    public MenuFragmentPageAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }

    public MenuFragment getFragmentByPosition(int position){
        return ((MenuFragment)fragments.get(position));
    }

}
