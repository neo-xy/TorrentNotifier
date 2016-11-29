package com.basicelixir.pawel.torrentnotifier;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;


class ChataAdapter extends FragmentStatePagerAdapter {
    private ArrayList<Fragment> chatFragmnets;

    private String[] titleOfChatTabs = {"Ask for a Tip", "Question asked"};

    ChataAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int position) {
        return chatFragmnets.get(position);
    }

    @Override
    public int getCount() {
        return chatFragmnets.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    void setFragments(ArrayList<Fragment> fragments) {
        chatFragmnets = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleOfChatTabs[position];
    }
}
