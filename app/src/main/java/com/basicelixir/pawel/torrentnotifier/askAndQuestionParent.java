package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Pawel on 09/10/2016.
 */

public class askAndQuestionParent extends Fragment implements View.OnClickListener{

    FirebaseDatabase firebase;
    FirebaseAuth fireAuth;
    View view;
    String userId;
    String TAG ="pawell";
    LinearLayout linearLayout;
    Button btnSend;
    EditText insertMessageEt;
    String messageToSand;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =inflater.inflate(R.layout.fragment_ask_for,container,false);

        insertMessageEt = (EditText)view.findViewById(R.id.ask_for_et_insert_message);

        linearLayout =(LinearLayout)view.findViewById(R.id.ask_for_ll);
        setUpConversationWindow();
        return view;
    }

    private void setUpConversationWindow() {
        fireAuth = FirebaseAuth.getInstance();
        userId = fireAuth.getCurrentUser().getUid();

        firebase = FirebaseDatabase.getInstance();
        DatabaseReference askReference = firebase.getReference().child("users").child(userId).child("ask");
        Message message =new Message(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnSend =(Button)view.findViewById(R.id.ask_for_btn_send2);

        btnSend.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onClick(View v) {

    }

}

