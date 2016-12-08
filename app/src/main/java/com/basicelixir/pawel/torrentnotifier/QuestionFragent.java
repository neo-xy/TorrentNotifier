package com.basicelixir.pawel.torrentnotifier;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class QuestionFragent extends Fragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private Button sendBtn;
    String TAG = "pawell";
    private String currentUser;
    private LinearLayout linearLayout;
    private View view;
    private EditText inserMessageEt;
    private long count;
    private String userWithTheQusetion;
    private String insertedText, timeStemp;
    private DatabaseReference questionReference;
    private Message message;
    private DatabaseReference dr;
    private ScrollView scrollView;
    private TextView countTV, timer;

    private SimpleDateFormat sdf;
    long timerLeft;

    private ChildEventListener questionListener;
    private TextView messageLength;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ask_for, container, false);
        sendBtn = (Button) view.findViewById(R.id.ask_for_btn_send2);
        messageLength = (TextView) view.findViewById(R.id.messageLength);

        sendBtn.setOnClickListener(this);
        sendBtn.setEnabled(false);
        if (!sendBtn.isEnabled()) {
            sendBtn.setTextColor(getResources().getColor(R.color.lightGray));
        }

        scrollView = (ScrollView) view.findViewById(R.id.ask_for_scroll);
        countTV = (TextView) view.findViewById(R.id.tv_count);
        timer = (TextView) view.findViewById(R.id.stoper_tv);
        sdf = new SimpleDateFormat("mm:ss");
        ImageButton reportButton  = (ImageButton) view.findViewById(R.id.ib_report);
        reportButton.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        inserMessageEt = (EditText) view.findViewById(R.id.ask_for_et_insert_message);
        inserMessageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                messageLength.setText(String.valueOf(30 - editable.length()));
            }
        });
        linearLayout = (LinearLayout) view.findViewById(R.id.ask_for_ll);
        currentUser = firebaseAuth.getCurrentUser().getUid();
        questionReference = firebaseDatabase.getReference().child("users").child(currentUser);


        questionListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("quesTime")) {
                    timeStemp = dataSnapshot.getValue().toString();
                    setTimer();
                }

                if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("question")) {
                    sendBtn.setEnabled(true);
                    sendBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                    String url = dataSnapshot.getValue().toString();

                    dr = firebaseDatabase.getReferenceFromUrl(url.trim());
                    userWithTheQusetion=dr.getParent().getParent().getKey();

                    dr.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            count = dataSnapshot.getChildrenCount();
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
        };

        return view;
    }

    private void setTimer() {
        timerLeft = System.currentTimeMillis() - Long.parseLong(timeStemp);
        timer.setVisibility(View.VISIBLE);
         new CountDownTimer(Message.timeAvailable - timerLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timer.setVisibility(View.VISIBLE);
                timer.setText(sdf.format(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                timer.setText("");
                timer.setVisibility(View.GONE);
                questionReference.child("quesTime").removeValue();
                sendBtn.setEnabled(false);

            }
        }.start();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_report) {
            PopupMenu popupMenu = new PopupMenu(getContext(), v);
            final Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater().inflate(R.menu.report_menu, menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item == menu.findItem(R.id.report_item)) {
                        LinearLayout ll = message.getConversation();
                        int t = ll.getChildCount();
                        ArrayList<String> textMessages = new ArrayList<String>();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            TextView mess = (TextView) ll.getChildAt(i);
                            textMessages.add(mess.getText().toString());

                        }
                        ReportedMessage rm = new ReportedMessage(textMessages, currentUser, userWithTheQusetion, new Date().getTime(), "question");
                        firebaseDatabase.getReference().child("Reported").push().setValue(rm);
                        Toast.makeText(getContext(), "User Reported", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
        if (v == view.findViewById(R.id.ask_for_btn_send2)) {
            insertedText = inserMessageEt.getText().toString();

            count = 0;
            dr.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    count = dataSnapshot.getChildrenCount();
                    if (count < 10) {
                        countTV.setText(String.valueOf(9 - count));
                        dr.child(count + "mb").setValue(insertedText);
                    }

                    inserMessageEt.setText("");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        questionReference.addChildEventListener(questionListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        questionReference.removeEventListener(questionListener);
    }
}
