package com.example.brian.halos;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raych on 2/24/2017.
 * Adapter to implement viewpager and create the tabs and creating them when user clicks on each tab.
 * Sets up the name of each tab and number of tabs.
 */

public class Store_TabAdapter extends FragmentPagerAdapter implements Store_Tab_TopFree.OnFragmentInteractionListener,Store_Tab_TopPaid.OnFragmentInteractionListener,
        Store_Tab_HotTours.OnFragmentInteractionListener{
    final int tab_count =3;  //number of tabs in Store Activity
    private String tab_titles[] = new String[] {"Hot Tours","Top Paid","Top Free"};
    public Store_TabAdapter(FragmentManager fm) {
        super(fm);
    }

    //creates the tab on tab switch.
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Store_Tab_HotTours frag0 = new Store_Tab_HotTours();
                return frag0;
            case 1:
                Store_Tab_TopPaid frag1 = new Store_Tab_TopPaid();
                return frag1;
            case 2:
                Store_Tab_TopFree frag2 = new Store_Tab_TopFree();
                return frag2;
        }
        return null;
    }
    //return Tab count
    @Override
    public int getCount() {
        return tab_count;
    }

    //Return title of tab.
    @Override
    public CharSequence getPageTitle(int position) {
    return tab_titles[position];
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
