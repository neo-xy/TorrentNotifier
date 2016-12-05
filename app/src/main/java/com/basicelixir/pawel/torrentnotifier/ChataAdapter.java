package com.basicelixir.pawel.torrentnotifier;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.ArrayList;


class ChataAdapter extends FragmentStatePagerAdapter {
    String TAG="pawell";
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
        if(chatFragmnets==null){
            Log.i(TAG, "getCount: null");
        }
        return chatFragmnets.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    void setFragments(ArrayList<Fragment> fragments) {
        Log.i(TAG, "setFragments: ssss");
        Log.i(TAG, "setFragments: iiii "+fragments.size());
        chatFragmnets = fragments;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleOfChatTabs[position];
    }
}
