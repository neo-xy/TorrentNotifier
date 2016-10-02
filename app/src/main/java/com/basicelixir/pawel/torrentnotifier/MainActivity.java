package com.basicelixir.pawel.torrentnotifier;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import layout.HomeTab;
import layout.ImdbTab;
import layout.MyListTab;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG = "pawell";
    private ArrayList<Fragment> fragmentList;
    TabLayout tableLayout;
    MyResultReciver rr;
    HomeTab homeTab;
    boolean isInForeground = true;
    Button messageBtn, openLoggInBtn, loggInBtn, createBtn, sendBtn, openCreateWindow;
    View messageLayout, loggInLayout, createLayout;
    private AlertDialog messageDialog, loggInDialog, createDialog;
    private TextView title, messageWindow;
    private EditText inputMessage, createUser, createPassword, userLogIn, passwordLoggIn;
    FirebaseAuth firebaseauth;
    FirebaseAuth.AuthStateListener authListener;
    DatabaseReference dbReference;
    DatabaseReference r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();
        messageLayout = inflater.inflate(R.layout.message_dialog, null);
        loggInLayout = inflater.inflate(R.layout.loggin_layout, null);
        createLayout = inflater.inflate(R.layout.create_dialog, null);
        messageBtn = (Button) findViewById(R.id.messageBtn);
        messageBtn.setOnClickListener(this);
        openLoggInBtn = (Button) findViewById(R.id.openLoggin);
        openLoggInBtn.setOnClickListener(this);
        loggInBtn = (Button) loggInLayout.findViewById(R.id.loggInBtn);
        loggInBtn.setOnClickListener(this);
        createUser = (EditText) createLayout.findViewById(R.id.craeteUser_ET);
        createPassword = (EditText) createLayout.findViewById(R.id.create_password);
        userLogIn = (EditText) loggInLayout.findViewById(R.id.username_ET);
        passwordLoggIn = (EditText) loggInLayout.findViewById(R.id.password_ET);
        createBtn = (Button) createLayout.findViewById(R.id.btn_create);
        createBtn.setOnClickListener(this);
        openCreateWindow = (Button) loggInLayout.findViewById(R.id.btn_open_create);
        openCreateWindow.setOnClickListener(this);

        firebaseauth =FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();


       authListener = new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

               FirebaseUser fireUser = firebaseAuth.getCurrentUser();
               
               if(fireUser!=null){
                   Log.i(TAG, "onAuthStateChanged: user is logged in "+fireUser.getEmail()+" "+fireUser.getUid());
               }if(fireUser==null) {
                   Log.i(TAG, "onAuthStateChanged: no user logged in");
               }else{
                   Log.i(TAG, "onAuthStateChanged: ellllse");
               }
           }
       };


        sendBtn = (Button) messageLayout.findViewById(R.id.send_Btn);
        sendBtn.setOnClickListener(this);
        messageWindow = (TextView) messageLayout.findViewById(R.id.message_window);
        inputMessage = (EditText) messageLayout.findViewById(R.id.input_messge_ET);


        title = (TextView) findViewById(R.id.tr);
        Typeface typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/ubuntu.ttf");
        title.setTypeface(typeface);
        ViewPager viewPager = (ViewPager) findViewById(R.id.my_pager);
        ImageView background = (ImageView) findViewById(R.id.background);
        Glide.with(this)
                .load(R.drawable.bg3)
                .into(background);
        tableLayout = (TabLayout) findViewById(R.id.my_tab);
        rr = new MyResultReciver(null);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NottifiactionService.class);
        intent.putExtra("reciver", rr);

        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Log.i(TAG, "onCreate: ");
            long time = Calendar.getInstance().getTimeInMillis();
            //   alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,10000,pendingIntent);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 5000, pendingIntent);
        } else {
            Log.i(TAG, "else");
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        homeTab = new HomeTab();
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add((Fragment) homeTab);
        fragmentList.add((Fragment) new ImdbTab());
        fragmentList.add((Fragment) new MyListTab());

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myPagerAdapter);

        tableLayout.setupWithViewPager(viewPager);

    }

    @Override
    public void onClick(View v) {
        if (v == messageBtn) {
            FirebaseDatabase db= FirebaseDatabase.getInstance();
            Log.i(TAG, "onClick: "+ firebaseauth.getCurrentUser().getUid());
            db.getReference().child("Users").child("biHdjszqYFhfF0oumdfOA5k481y2").child("messs").child("mes1").setValue("hello");
//            Log.i(TAG, "onClick: meesss");
//             r = FirebaseDatabase.getInstance().getReference().getRoot();
//            r.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    Log.i(TAG, "onDataChange: "+ dataSnapshot.getChildrenCount());
//                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
//
//                        Log.i(TAG, "onDataChange: "+postSnapshot.getKey());
//                    }
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//            Log.i(TAG, "onClick: open dialog");
//            if (messageDialog == null) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setView(messageLayout);
//                messageDialog = builder.create();
//                messageDialog.show();
//            } else {
//                messageDialog.show();
//            }
        }
        if (v == messageLayout.findViewById(R.id.send_Btn)) {
            messageWindow.setText(inputMessage.getText().toString());
        }
        if (v == findViewById(R.id.openLoggin)) {
            LoggInDialog logginDialog = new LoggInDialog(MainActivity.this);
            logginDialog.show();

        }
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
            //  if (resultCode == 1) {
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
        firebaseauth.removeAuthStateListener(authListener);
    }
}
