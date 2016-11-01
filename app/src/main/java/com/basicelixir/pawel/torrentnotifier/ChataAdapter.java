package com.basicelixir.pawel.torrentnotifier;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Pawel on 04/10/2016.
 */

public class ChataAdapter extends FragmentStatePagerAdapter {
ArrayList<Fragment> fs ;

String[]r={"Ask for a Tip","Question asked"};

    AskForAtipFragment ask;
    public ChataAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {

        return fs.get(position);
    }

    @Override
    public int getCount() {
        return fs.size();
    }

    @Override
    public int getItemPosition(Object object) {

        return super.getItemPosition(object);

    }

    public void setFragments(ArrayList<Fragment> fragments) {
        fs = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  r[position];
    }
}
