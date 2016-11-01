package com.basicelixir.pawel.torrentnotifier;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Pawel on 06/10/2016.
 */

public class QuestionFragent extends Fragment implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    Button sendBtn;
    String TAG = "pawell";
    String currentUser;
    LinearLayout linearLayout;
    View view;
    EditText inserMessageEt;
    long count;
    String userWithTheQusetion;
    String insertedText;
    DatabaseReference questionReference;
    Message message;
    DatabaseReference dr;
    ScrollView scrollView;
    TextView countTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ask_for, container, false);
        sendBtn = (Button) view.findViewById(R.id.ask_for_btn_send2);

        sendBtn.setOnClickListener(this);
        sendBtn.setClickable(false);
        scrollView = (ScrollView) view.findViewById(R.id.ask_for_scroll);
        countTV = (TextView) view.findViewById(R.id.tv_count);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        inserMessageEt = (EditText) view.findViewById(R.id.ask_for_et_insert_message);
        linearLayout = (LinearLayout) view.findViewById(R.id.ask_for_ll);
        currentUser = firebaseAuth.getCurrentUser().getUid();
        questionReference = firebaseDatabase.getReference().child("users").child(currentUser);
        questionReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("question")) {
                    sendBtn.setClickable(true);
                    String url = dataSnapshot.getValue().toString();

                    dr = firebaseDatabase.getReferenceFromUrl(url.trim());

                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count= dataSnapshot.getChildrenCount();
                            countTV.setText(String.valueOf(10 - count));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    message = new Message(getContext());
                    message.fillUpQuestionWindow(dr, currentUser, getContext(), linearLayout, scrollView);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }


    @Override
    public void onClick(View v) {
        if (v == view.findViewById(R.id.ask_for_btn_send2)) {
            insertedText = inserMessageEt.getText().toString();

            count = 0;
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    count = dataSnapshot.getChildrenCount();
                    if (count < 10) {
                        countTV.setText(String.valueOf(9 - count));
                        Log.i(TAG, "onDataChange: jjjjj");
                        dr.child(count + "mb").setValue(insertedText);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

    }

    private long getMessageCount() {

        count = 0;
        firebaseDatabase.getReference().child("users").child(currentUser).child("question").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                count = dataSnapshot.getChildrenCount();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    userWithTheQusetion = d.getKey().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return count;
    }
}
