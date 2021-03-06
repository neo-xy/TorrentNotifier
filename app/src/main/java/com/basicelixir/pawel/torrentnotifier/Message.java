package com.basicelixir.pawel.torrentnotifier;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Message {
    public String userId;
    public Context context;
    public LinearLayout linearLayout;
    public ArrayList<String> listOfUsers;
    String TAG = "pawell";
    public ScrollView scrollView;
    static long timeAvailable = 300000;
    public ListListener listListener;

    public Message(Context context) {
        this.context = context;
    }

     void fillUpQuestionWindow(DatabaseReference questionReference, String userID, Context context, final LinearLayout linearLayout, ScrollView scrollView) {
        this.context = context;
        this.userId = userID;
        this.linearLayout = linearLayout;
        this.scrollView = scrollView;


        questionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                addConversationToAsk(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void addConversationToAsk(DataSnapshot dataSnapshot) {
        TextView textView;

        for (DataSnapshot d : dataSnapshot.getChildren()) {
            if (d.getKey().contains("ma")) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                textView = new TextView(context);
                textView.setText(d.getValue(String.class));

                lp.setMargins(20, 10, 150, 10);
                textView.setLayoutParams(lp);

                textView.setBackgroundResource(R.drawable.message_cloud_bg);

                //TODO look if context.getresources works
                textView.getBackground().setTint(context.getResources().getColor(R.color.darkGray));
                textView.setTextColor(Color.WHITE);
                textView.setPadding(40, 15, 15, 15);
                linearLayout.addView(textView);


            }
            if (d.getKey().contains("mb")) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView = new TextView(context);
                textView.setText(d.getValue().toString());
                lp.setMargins(150, 10, 20, 10);
                textView.setGravity(Gravity.RIGHT);

                textView.setTextColor(context.getResources().getColor(R.color.darkGray));
                textView.setBackgroundResource(R.drawable.message_cloud_bg);
                textView.getBackground().setTint(Color.WHITE);
                int b = textView.getPaddingBottom();
                textView.setPadding(15, 15, 40, 15);
                textView.setLayoutParams(lp);
                linearLayout.addView(textView);
                linearLayout.setFocusable(true);
            }

            scrollView.fullScroll(ViewGroup.FOCUS_DOWN);
        }

    }

     void getListOfUSers(AskForAtipFragment askForAtipFragment) {
        listListener = askForAtipFragment;
        listOfUsers = new ArrayList<>();
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {

                    if (d.child("quesTime").getValue() == null && d.child("eligebleForChat").getValue(Boolean.class)&& d.child("notificationAlowed").getValue(Boolean.class)) {
                        listOfUsers.add(d.getKey());
                    }
                }
                listListener.getList(listOfUsers);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void fillUpWindowMessage(DatabaseReference askReference, String userId, String lastUser, String timeStemp, final LinearLayout linearLayout, ScrollView scrollView, AskForAtipFragment ask) {
        this.linearLayout = linearLayout;
        this.userId = userId;
        this.scrollView = scrollView;

        askReference.child(lastUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linearLayout.removeAllViews();
                addConversationToAsk(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

     LinearLayout getConversation() {
        return linearLayout;
    }
}
