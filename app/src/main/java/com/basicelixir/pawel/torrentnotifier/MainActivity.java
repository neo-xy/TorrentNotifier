package com.basicelixir.pawel.torrentnotifier;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.target.Target;
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
    private HomeTab homeTab;
    private boolean isInForeground = true;
    private FirebaseAuth firebaseauth;
    private FirebaseAuth.AuthStateListener authListener;
    private String currentUser;
    private Switch notificationSwitch;
    Intent messageIntent;
    private Boolean notificationAllowed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



            notificationAllowed = (Boolean) this.getSharedPreferences("notificationAllowed",MODE_PRIVATE).getBoolean("notificationAllowed",true);
        

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myTooolbar);
        setSupportActionBar(toolbar);

        messageIntent = new Intent(this, FirebaseBackgroundService.class);
        this.startService(messageIntent);

        notificationSwitch = (Switch) findViewById(R.id.switch_view);
        notificationSwitch.setChecked(notificationAllowed);

        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Log.i(TAG, "onCheckedChanged: " + b);
                if (b) {

                    notificationAllowed=true;
                    getApplication().startService(messageIntent);
                    Toast.makeText(getBaseContext(), "'Question' notifications are turn ON", Toast.LENGTH_SHORT)
                            .show();
                }else{
                    notificationAllowed=false;
                    getApplication().stopService(messageIntent);
                    Toast.makeText(getBaseContext(), "'Question' notifications are turn OFF", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        firebaseauth = FirebaseAuth.getInstance();
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    currentUser = firebaseUser.getUid();
                }
                if (firebaseUser == null) {
                }
            }
        };


        ViewPager viewPager = (ViewPager) findViewById(R.id.my_pager);
        ImageView background = (ImageView) findViewById(R.id.background);
        Glide.with(this)
                .load(R.drawable.tnbg)
                .asBitmap()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .into(background);


//        Intent serviceintent =new Intent(this, FirebaseBackgroundService.class);
//        PendingIntent pendingintent =PendingIntent.getService(this,0, serviceintent,0);
//        AlarmManager alarm =(AlarmManager)getSystemService(getApplicationContext().ALARM_SERVICE);
//        alarm.cancel(pendingintent);
//        alarm.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5000, pendingintent);

        TabLayout tableLayout = (TabLayout) findViewById(R.id.my_tab);
        MyResultReciver rr = new MyResultReciver(null);
        this.getSupportFragmentManager();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NottifiactionService.class);
        intent.putExtra("reciver", rr);
        startService(intent);
//tylkok to potrzebne a moze nawet nie moze  wystarczy tylko "startService" -check it
        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            long time = Calendar.getInstance().getTimeInMillis();
            //te dwa nie chyba
            //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,time,10000,pendingIntent);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 120000, pendingIntent);
        }

        homeTab = new HomeTab();
        ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(homeTab);
        fragmentList.add(new ImdbTab());
        fragmentList.add(new MyListTab());

        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myPagerAdapter);

        tableLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void logOutListener(boolean isSignedOut) {
        firebaseauth.signOut();

    }


    @SuppressLint("ParcelCreator")
    public class MyResultReciver extends ResultReceiver {
        MyResultReciver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            if (resultCode == 1) {
                // Log.i(TAG, "onReceiveResult:11 " + resultData.get("titles").toString());

//              torrentName = (String) resultData.get("torrent");
//                imdbURl = (String) resultData.get("url");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //  if (isInForeground == true) {
                        homeTab.updateAvailableTorrents((ArrayList<String>) resultData.get("titles"));
                        // }
                    }
                });
            }
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
        Log.i(TAG, "onStop: ");
        getSharedPreferences("notificationAllowed",MODE_PRIVATE).edit().putBoolean("notificationAllowed",notificationAllowed).commit();
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

    public String getCurrentUser() {
        return currentUser;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: "+notificationSwitch.isChecked());

    }





}
