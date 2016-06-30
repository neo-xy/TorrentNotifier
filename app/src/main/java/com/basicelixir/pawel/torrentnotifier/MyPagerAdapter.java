package com.basicelixir.pawel.torrentnotifier;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Pawel on 19/05/2016.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    String[]ty ={"Home","Add Movie","My List"};
    ArrayList<Fragment>fragmentList;
    public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment>fragmentList) {
        super(fm);
        this.fragmentList =fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ty[position];
    }

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);

    }


}
