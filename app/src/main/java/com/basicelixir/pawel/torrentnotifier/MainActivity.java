package com.basicelixir.pawel.torrentnotifier;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.*;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.os.ResultReceiver;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

import layout.HomeTab;
import layout.ImdbTab;
import layout.MyListTab;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, FacebookLogOutListener {
    String TAG = "pawell";
    private HomeTab homeTab;
    private FirebaseAuth firebaseauth;
    private FirebaseAuth.AuthStateListener authListener;
    private String currentUser;
    private Switch notificationSwitch;
    Intent messageIntent;
    private Boolean notificationAllowed;
    ChatDialog chatDialog;
    Set foo;
    boolean isOnForground;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onNewIntent(getIntent());

        notificationAllowed = this.getSharedPreferences("notificationAllowed", MODE_PRIVATE).getBoolean("notificationAllowed", true);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.myTooolbar);
        setSupportActionBar(toolbar);

       if(!isMyServiceRunning(FirebaseBackgroundService.class)){
           messageIntent = new Intent(this, FirebaseBackgroundService.class);
           if(notificationAllowed) {

               this.startService(messageIntent);
           }
       }

         
        notificationSwitch = (Switch) findViewById(R.id.switch_view);
        notificationSwitch.setChecked(notificationAllowed);


        notificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    notificationAllowed = true;
                    getApplication().startService(messageIntent);
                    Toast.makeText(getBaseContext(), "'Question' notifications are turn ON", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    notificationAllowed = false;
                    getApplication().stopService(messageIntent);
                    Toast.makeText(getBaseContext(), "'Question' notifications are turn OFF", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        firebaseauth = FirebaseAuth.getInstance();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if (firebaseUser != null) {
                    currentUser = firebaseUser.getUid();
                    notificationSwitch.setEnabled(true);
                }
                if (firebaseUser == null) {
                    notificationSwitch.setEnabled(false);
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

        TabLayout tableLayout = (TabLayout) findViewById(R.id.my_tab);
        MyResultReciver rr = new MyResultReciver(null);
        this.getSupportFragmentManager();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, NottifiactionService.class);

        intent.putExtra("yes", true);
        intent.putExtra("reciver", rr);
        startService(intent);
        PendingIntent pendingIntent = PendingIntent.getService(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 5000, 120000, pendingIntent);

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
    public void logOutListener(boolean isSignedOut) {
        firebaseauth.signOut();
    }

    @Override
    public void onClick(View view) {
        if (firebaseauth.getCurrentUser() != null) {
            if (view == findViewById(R.id.ibtnchat)) {
                chatDialog =new ChatDialog();
                Bundle b = new Bundle();
                b.putInt("nr",homeTab.nrItem);

                chatDialog.setArguments(b);


                chatDialog.show(getSupportFragmentManager(), "dialogchat");
            }
        } else {
            Toast.makeText(getBaseContext(), "Log In to Usa this Fucnction", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ParcelCreator")
    public class MyResultReciver extends ResultReceiver {
        MyResultReciver(Handler handler) {
            super(handler);

        }

        @Override
        protected void onReceiveResult(int resultCode, final Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            Log.i(TAG, "onReceiveResult: " + resultCode);
            if (resultCode == 1) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isOnForground) {
                            homeTab.updateAvailableTorrents((ArrayList<String>) resultData.get("titles"));
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isOnForground = false;
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
        getSharedPreferences("notificationAllowed", MODE_PRIVATE).edit().putBoolean("notificationAllowed", notificationAllowed).apply();
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
                FacebookLogginDialog facebookLogginDialog = new FacebookLogginDialog();
                facebookLogginDialog.show(getSupportFragmentManager(), "rr");
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
    protected void onResume() {
        super.onResume();
        isOnForground = true;
    }
    public ChatDialog getchatDialog(){
        return chatDialog;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
