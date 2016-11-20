package com.basicelixir.pawel.torrentnotifier;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import layout.HomeTab;
import layout.ImdbTab;
import layout.MyListTab;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FacebookLogOutListener {
    String TAG = "pawell";
    private ArrayList<Fragment> fragmentList;
    TabLayout tableLayout;
    MyResultReciver rr;
    HomeTab homeTab;
    boolean isInForeground = true;
    Button btnChat, btnChatNotis;
    View messageLayout, createLayout;
    FirebaseAuth firebaseauth;
    FirebaseAuth.AuthStateListener authListener;
    DatabaseReference dbReference;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.myTooolbar);

        setSupportActionBar(toolbar);
        LayoutInflater inflater = getLayoutInflater();
        messageLayout = inflater.inflate(R.layout.dialog_chat, null);
        createLayout = inflater.inflate(R.layout.dialog_log_in, null);

        btnChat = (Button) findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(this);
        btnChatNotis = (Button) findViewById(R.id.btn_chat_notis);


        firebaseauth = FirebaseAuth.getInstance();

        dbReference = FirebaseDatabase.getInstance().getReference();


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser fireUser = firebaseAuth.getCurrentUser();


                if (fireUser != null) {

                    btnChat.setEnabled(true);
                    btnChat.getBackground().setTint(Color.WHITE);
                }
                if (fireUser == null) {
                    btnChat.setEnabled(false);
                    btnChat.getBackground().setTintList(null);
                } else {
                }
            }
        };


        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ubuntu.ttf");
        ViewPager viewPager = (ViewPager) findViewById(R.id.my_pager);
        ImageView background = (ImageView) findViewById(R.id.background);
//        Glide.with(this)
//                .load(R.drawable.bg3)
//                .into(background);

//        Intent serviceintent =new Intent(this, FirebaseBackgroundService.class);
//        PendingIntent pendingintent =PendingIntent.getService(this,0, serviceintent,0);
//        AlarmManager alarm =(AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
//        alarm.cancel(pendingintent);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5000, pendingintent);

        tableLayout = (TabLayout) findViewById(R.id.my_tab);
        rr = new MyResultReciver(null);
        this.getSupportFragmentManager();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NottifiactionService.class);
        intent.putExtra("reciver", rr);
        // startService(intent);

        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            long time = Calendar.getInstance().getTimeInMillis();
            //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,10000,pendingIntent);
            // alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 5000, pendingIntent);


        } else {
            Log.i(TAG, "else");
        }

        Intent intent1 = new Intent(this, FirebaseBackgroundService.class);
        this.startService(intent1);


        homeTab = new HomeTab();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add((Fragment) homeTab);
        fragmentList.add((Fragment) new ImdbTab());
        fragmentList.add((Fragment) new MyListTab());

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myPagerAdapter);

        tableLayout.setupWithViewPager(viewPager);
        if (firebaseauth.getCurrentUser() != null) {

            checkForMessages(firebaseauth.getCurrentUser().getUid(), dbReference);
        }

    }

    private void checkForMessages(String uid, DatabaseReference dbReference) {

        dbReference.child("users").child(uid).child("question").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    btnChatNotis.setText("1");
                    btnChatNotis.setVisibility(View.VISIBLE);
                } else {
                    btnChatNotis.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        {
        }

    }

    @Override
    public void onClick(View v) {


        if (v == findViewById(R.id.btn_chat)) {

            ChatDialog chatDialog = new ChatDialog();
            chatDialog.show(getSupportFragmentManager(), "rt");

        }
    }

    @Override
    public void logOutListener(boolean isSignedOut) {
        btnChat.setEnabled(false);
        firebaseauth.signOut();

    }


    @SuppressLint("ParcelCreator")
    public class MyResultReciver extends ResultReceiver {
        public MyResultReciver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Log.i(TAG, "onReceiveResult: ");
//              if (resultCode == 1) {
//                torrentName = (String) resultData.get("torrent");
//                imdbURl = (String) resultData.get("url");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (isInForeground == true) {
                        homeTab.updateAvailableTorrents();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isInForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isInForeground = false;
    }


    @Override
    protected void onStart() {
        super.onStart();
        firebaseauth.addAuthStateListener(authListener);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authListener != null) {
            firebaseauth.removeAuthStateListener(authListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_create:

                CreateDialog createDialog = new CreateDialog();
                createDialog.show(getSupportFragmentManager(), "rr");

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
