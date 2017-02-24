package com.example.brian.halos;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by raych on 2/24/2017.
 */

public class Store_TabAdapter extends FragmentPagerAdapter{
    final int tab_count =5;  //number of tabs in Store Activity
    private String tab_titles[] = new String[] {"Hot Tours","Top Paid","Top Free","Top Free","Explore","Checkout"};
    public Store_TabAdapter(FragmentManager fm) {
        super(fm);
    }

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
            case 3:
                Store_Tab_Explore frag3 = new Store_Tab_Explore();
                return frag3;
            case 4:
                Store_Tab_Checkout frag4 = new Store_Tab_Checkout();
                return frag4;
        }
        return null;
    }

    @Override
    public int getCount() {
        return tab_count;
    }
    @Override
    public CharSequence getPageTitle(int position) {
    return tab_titles[position];
    }

}
