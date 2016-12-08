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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class AskForAtipFragment extends Fragment implements View.OnClickListener,ListListener {

   private FirebaseDatabase firebase;
    private DatabaseReference askReference;


    private String currentUser, pickedUser, lastUser,timeStemp;
    private String TAG = "pawell";

    private LinearLayout linearLayout;
    private EditText insertMessageEt;
    private TextView timer, countBtn,messageLenght;
    private ScrollView scrollView;

    private Message message;
    private ArrayList users;
    private long numberOfMessages;
    private SimpleDateFormat sdf;

    public AskForAtipFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_ask_for, container, false);
        insertMessageEt = (EditText) view.findViewById(R.id.ask_for_et_insert_message);
        insertMessageEt.setHint(getContext().getResources().getString(R.string.hintMessage));
        insertMessageEt.setHintTextColor(getResources().getColor(R.color.lightGray));
        messageLenght =(TextView)view.findViewById(R.id.messageLength);
        insertMessageEt.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
         }

         @Override
         public void afterTextChanged(Editable editable) {
             Log.i(TAG, "afterTextChanged: "+editable.length());
             messageLenght.setText(String.valueOf(30-editable.length()));
         }
     });

        Button sendBtn = (Button) view.findViewById(R.id.ask_for_btn_send2);
        sendBtn.setOnClickListener(this);
        timer = (TextView) view.findViewById(R.id.stoper_tv);
        scrollView =(ScrollView)view.findViewById(R.id.ask_for_scroll);
        countBtn = (TextView) view.findViewById(R.id.tv_count);
        ImageButton reportButtn = (ImageButton)view.findViewById(R.id.ib_report);
        reportButtn.setOnClickListener(this);

        linearLayout = (LinearLayout) view.findViewById(R.id.ask_for_ll);
        message = new Message(getContext());

        firebase = FirebaseDatabase.getInstance();
        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        if(fireAuth.getCurrentUser()!=null){
            currentUser = fireAuth.getCurrentUser().getUid();
            askReference = firebase.getReference().child("users").child(currentUser).child("ask");
            pickedUser = "";
            lastUser = "";
            sdf = new SimpleDateFormat("mm:ss");
            countBtn.setText(R.string.number_of_mesg_left);

            fillUpQuestionDialog();
        }
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
                                    countBtn.setText(R.string.number_of_mesg_left);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

                if (timeStemp != null && lastUser != null) {
                    if ((System.currentTimeMillis() - Long.parseLong(timeStemp)) < 300000) {
                        message.fillUpWindowMessage(askReference, currentUser, lastUser, timeStemp, linearLayout,scrollView, AskForAtipFragment.this);

                    } else {
                        linearLayout.removeAllViews();
                        firebase.getReference().child("users").child(currentUser).child("timeStemp").removeValue();
                        firebase.getReference().child("users").child(currentUser).child("lastUser").removeValue();
                        countBtn.setText(R.string.number_of_mesg_left);
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
        long timePassed = System.currentTimeMillis() - Long.parseLong(timeStemp);
        new CountDownTimer(Message.timeAvailable - timePassed, 1000) {
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
                firebase.getReference().child("users").child(lastUser).child("quesTime").removeValue();
                askReference.removeValue();
                linearLayout.removeAllViews();
                countBtn.setText(R.string.number_of_mesg_left);
                timeStemp = null;
                lastUser = "";
                pickedUser = "";
            }
        }.start();
    }

    private void pickRandomUserFromTheList() {
        users = new ArrayList<>();
        message.getListOfUSers(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ib_report){
            PopupMenu popupMenu = new PopupMenu(getContext(),v);
            final Menu menu = popupMenu.getMenu();
            popupMenu.getMenuInflater().inflate(R.menu.report_menu,menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item==menu.findItem(R.id.report_item)){
                       LinearLayout ll = message.getConversation();
                        int t =ll.getChildCount();
                        ArrayList<String>textMessages = new ArrayList<>();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            TextView mess = (TextView)ll.getChildAt(i);
                            textMessages.add(mess.getText().toString());
                        }
                        ReportedMessage rm = new ReportedMessage(textMessages,currentUser, lastUser, new Date().getTime(),"ask");
                        firebase.getReference().child("Reported").push().setValue(rm);
                        Toast.makeText(getContext(),"User Reported",Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            });
            popupMenu.show();
        }
        if(v.getId()== R.id.ask_for_btn_send2) {
            if (lastUser != null && timeStemp != null && System.currentTimeMillis() - Long.parseLong(timeStemp) < 300000 && !lastUser.equals("")) {
                askReference.child(lastUser).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        numberOfMessages = dataSnapshot.getChildrenCount();
                        if (numberOfMessages < 10) {

                            countBtn.setText(String.valueOf(9 - numberOfMessages));
                            askReference.child(lastUser).child(numberOfMessages + "ma").setValue(insertMessageEt.getText().toString());
                            firebase.getReference().child("users").child(lastUser).child("question").setValue(askReference.child(lastUser).toString());
                            firebase.getReference().child("users").child(lastUser).child("quesTime").setValue(timeStemp);
                        } else {
                            Toast.makeText(getContext(), "max number of messages reached", Toast.LENGTH_SHORT).show();
                        }
                        insertMessageEt.setText("");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                pickRandomUserFromTheList();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void getList(ArrayList<String> list) {
        users = list;
        if(users!=null && users.size()>0&&!users.isEmpty()) {
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
                    countBtn.setText(String.valueOf(9 - numberOfMessages));
                    firebase.getReference().child("users").child(lastUser).child("question").setValue(askReference.child(lastUser).toString());
                    firebase.getReference().child("users").child(lastUser).child("quesTime").setValue(timeStemp);
                    message.fillUpWindowMessage(askReference, currentUser, lastUser, timeStemp, linearLayout, scrollView,AskForAtipFragment.this);
                    setTimer();
                    insertMessageEt.setText("");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(getContext(),"no available users right at this moment",Toast.LENGTH_SHORT).show();
        }
    }
}
