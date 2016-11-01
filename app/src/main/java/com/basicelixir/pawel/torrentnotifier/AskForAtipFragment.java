package com.basicelixir.pawel.torrentnotifier;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;


/**
 * Created by Pawel on 04/10/2016.
 */

public class AskForAtipFragment extends Fragment implements View.OnClickListener {

    FirebaseDatabase firebase;
    FirebaseAuth fireAuth;
    DatabaseReference askReference;

    View view;
    String currentUser;
    String pickedUser;
    String lastUser;
    String timeStemp;
    String TAG = "pawell";


    LinearLayout linearLayout;
    EditText insertMessageEt;
    Button sendBtn;
    TextView timer, countBtn;
    ScrollView scrollView;

    String messageToSand;
    Message message;
    ArrayList users;
    long numberOfMessages;
    SimpleDateFormat sdf;
    Date de;

    CountDownTimer countDownTimer;
    Handler handler;
    long g;


    public AskForAtipFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ask_for, container, false);
        insertMessageEt = (EditText) view.findViewById(R.id.ask_for_et_insert_message);
        sendBtn = (Button) view.findViewById(R.id.ask_for_btn_send2);
        sendBtn.setOnClickListener(this);
        timer = (TextView) view.findViewById(R.id.stoper_tv);
        scrollView =(ScrollView)view.findViewById(R.id.ask_for_scroll);
        countBtn = (TextView) view.findViewById(R.id.tv_count);

        linearLayout = (LinearLayout) view.findViewById(R.id.ask_for_ll);
        message = new Message(getContext());

        firebase = FirebaseDatabase.getInstance();
        fireAuth = FirebaseAuth.getInstance();
        currentUser = fireAuth.getCurrentUser().getUid();
        askReference = firebase.getReference().child("users").child(currentUser).child("ask");
        pickedUser = "";
        lastUser = "";
        sdf = new SimpleDateFormat("mm:ss");
        de = new Date(180000 - g);
        countBtn.setText("number of left messages: 10");


        fillUpQuestionDialog();

        return view;
    }

    private void fillUpQuestionDialog() {
        getLastUser();

    }

    private String getLastUser() {


        firebase.getReference().child("users").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    if (d.getKey().equals("timeStemp")) {
                        timeStemp = d.getValue().toString();

                        setTimer();
                    }
                    if (d.getKey().equals("lastUser")) {
                        lastUser = d.getValue().toString();

                        askReference.child(lastUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                numberOfMessages = dataSnapshot.getChildrenCount();
                                countBtn.setText(String.valueOf(10-numberOfMessages));
                                if(numberOfMessages==10){
                                    countBtn.setText("number of left messages: 10");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                if (timeStemp != null && lastUser != null) {
                    if ((System.currentTimeMillis() - Long.parseLong(timeStemp)) < 100000) {
                       // countBtn.setText(String.valueOf(10-numberOfMessages));
                        message.fillUpWindowMessage(askReference, currentUser, lastUser, timeStemp, linearLayout,scrollView);

                    } else {
                        linearLayout.removeAllViews();
                        firebase.getReference().child("users").child(currentUser).child("timeStemp").removeValue();
                        firebase.getReference().child("users").child(currentUser).child("lastUser").removeValue();
                        countBtn.setText("number of left messages: 10");
                        askReference.removeValue();
                    }
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return timeStemp;
    }

    private void setTimer() {
        g = System.currentTimeMillis() - Long.parseLong(timeStemp);
        countDownTimer = new CountDownTimer(100000 - g, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timer.setVisibility(View.VISIBLE);
                timer.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {

                timer.setText("");
                timer.setVisibility(View.GONE);
                firebase.getReference().child("users").child(currentUser).child("timeStemp").removeValue();
                firebase.getReference().child("users").child(currentUser).child("lastUser").removeValue();
                firebase.getReference().child("users").child(lastUser).child("question").removeValue();
                askReference.removeValue();
                linearLayout.removeAllViews();
                countBtn.setText("number of left messages: 10");
                timeStemp = null;
                lastUser = "";
                pickedUser = "";

            }
        }.start();


    }

    private String pickRandomUserFromTheList() {
        users = new ArrayList<>();
        users = message.getListOfUSers();
        while (pickedUser.equals("") || pickedUser.equals(currentUser)) {

            Random random = new Random();
            int pick = random.nextInt(users.size());
            pickedUser = (String) users.get(pick);
            lastUser = pickedUser;
        }
        timeStemp = String.valueOf(System.currentTimeMillis());
        firebase.getReference().child("users").child(currentUser).child("timeStemp").setValue(timeStemp);
        firebase.getReference().child("users").child(currentUser).child("lastUser").setValue(lastUser);


        askReference.child(lastUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberOfMessages = dataSnapshot.getChildrenCount();
                askReference.child(lastUser).child(numberOfMessages + "ma").setValue(insertMessageEt.getText().toString());
                countBtn.setText(String.valueOf(9-numberOfMessages));
                firebase.getReference().child("users").child(lastUser).child("question").setValue(askReference.child(lastUser).toString());
                message.fillUpWindowMessage(askReference, currentUser, lastUser, timeStemp, linearLayout,scrollView);
                setTimer();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return pickedUser;
    }

    @Override
    public void onClick(View v) {
        if (lastUser != null && timeStemp != null && System.currentTimeMillis() - Long.parseLong(timeStemp) < 300000 && !lastUser.equals("")) {


            askReference.child(lastUser).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    numberOfMessages = dataSnapshot.getChildrenCount();
                    if(numberOfMessages<10) {

                        countBtn.setText(String.valueOf(9-numberOfMessages));
                        askReference.child(lastUser).child(numberOfMessages + "ma").setValue(insertMessageEt.getText().toString());
                        firebase.getReference().child("users").child(lastUser).child("question").setValue(askReference.child(lastUser).toString());
                    }else{
                        Toast.makeText(getContext(),"max number of messages reached",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            pickRandomUserFromTheList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
