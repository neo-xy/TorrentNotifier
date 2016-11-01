package com.basicelixir.pawel.torrentnotifier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Created by Pawel on 02/10/2016.
 */

public class ChatDialog extends DialogFragment implements View.OnClickListener {

    String TAG = "pawell";
    FirebaseDatabase firebaseDatabase;
    String currentUser;
    ViewPager viewPager;
    ChataAdapter chataAdapter;
    View vi;
    TabLayout.Tab t;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    vi = inflater.inflate(R.layout.message_dialog, container, false);

        vi.setOnClickListener(this);
        int widthOfPhone = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        widthOfPhone = widthOfPhone - widthOfPhone / 8;
        vi.setMinimumWidth(widthOfPhone);
        firebaseDatabase = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        TabLayout tableLayout = (TabLayout) vi.findViewById(R.id.tab_chat);

        QuestionFragent questionFragent = new QuestionFragent();
        AskForAtipFragment askForAtipFragment = new AskForAtipFragment();

        viewPager = (ViewPager) vi.findViewById(R.id.view_pager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(askForAtipFragment);
        fragments.add(questionFragent);



        chataAdapter = new ChataAdapter(getChildFragmentManager());
        chataAdapter.setFragments(fragments);
        viewPager.setAdapter(chataAdapter);

        tableLayout.setupWithViewPager(viewPager);

        return vi;
    }

    @Override
    public void onClick(View v) {
        }

}
