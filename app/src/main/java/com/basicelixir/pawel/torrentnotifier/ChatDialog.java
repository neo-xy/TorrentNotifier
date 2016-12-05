package com.basicelixir.pawel.torrentnotifier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Pawel on 02/10/2016.
 */

public class ChatDialog extends DialogFragment implements View.OnClickListener {

    private String TAG = "pawell";
    ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.dialog_chat, container, false);


        view.setOnClickListener(this);
        int widthOfPhone = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        widthOfPhone = widthOfPhone - widthOfPhone / 8;
        view.setMinimumWidth(widthOfPhone);
        TabLayout tableLayout = (TabLayout) view.findViewById(R.id.tab_chat);

        QuestionFragent questionFragent = new QuestionFragent();
        AskForAtipFragment askForAtipFragment = new AskForAtipFragment();

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(askForAtipFragment);
        fragments.add(questionFragent);

        ChataAdapter chataAdapter = new ChataAdapter(getChildFragmentManager());
        chataAdapter.setFragments(fragments);
        viewPager.setAdapter(chataAdapter);

        tableLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onClick(View v) {
    }
}
