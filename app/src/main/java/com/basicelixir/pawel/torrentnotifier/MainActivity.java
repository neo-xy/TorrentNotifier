package com.basicelixir.pawel.torrentnotifier;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
    Button messageBtn, openLoggInBtn,loggInBtn, createBtn,sendBtn;
    View messageLayout,loggInLayout,createLayout;
    private AlertDialog messageDialog,loggInDialog,createDialog;
    private TextView title, messageWindow;
    private EditText inputMessage, createUser, createPassword, userLogIn, passwordLoggIn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LayoutInflater inflater = getLayoutInflater();
        messageLayout = inflater.inflate(R.layout.message_dialog,null);
        loggInLayout = inflater.inflate(R.layout.loggin_layout,null);
        createLayout = inflater.inflate(R.layout.create_dialog,null);
        messageBtn = (Button)findViewById(R.id.messageBtn);
        messageBtn.setOnClickListener(this);
        openLoggInBtn =(Button)findViewById(R.id.openLoggin);
        openLoggInBtn.setOnClickListener(this);
        loggInBtn =(Button)loggInLayout.findViewById(R.id.loggInBtn);
        loggInBtn.setOnClickListener(this);
        createUser =(EditText)createLayout.findViewById(R.id.craeteUser_ET);
        createPassword = (EditText)createLayout.findViewById(R.id.create_password);
        userLogIn = (EditText)loggInLayout.findViewById(R.id.username_ET);
        passwordLoggIn =(EditText)loggInLayout.findViewById(R.id.password_ET); 
        createBtn = (Button)createDialog.findViewById(R.id.createBtn);
        createBtn.setOnClickListener(this);
        



        sendBtn =(Button)messageLayout.findViewById(R.id.send_Btn);
        sendBtn.setOnClickListener(this);
        messageWindow =(TextView)messageLayout.findViewById(R.id.message_window);
        inputMessage =(EditText)messageLayout.findViewById(R.id.input_messge_ET);



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
        if(v==messageBtn){
            Log.i(TAG, "onClick: open dialog");
            if(messageDialog ==null ) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(messageLayout);
                messageDialog = builder.create();
                messageDialog.show();
            }else{
                messageDialog.show();
            }
        }
        if(v==messageLayout.findViewById(R.id.send_Btn)){
            messageWindow.setText(inputMessage.getText().toString());
        }
        if(v==findViewById(R.id.openLoggin)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(loggInLayout);
            loggInDialog =builder.create();
            loggInDialog.show();

        }
        if(v==loggInDialog.findViewById(R.id.loggInBtn)){
            /////////
            //////////7

        }
        if(v==loggInDialog.findViewById(R.id.open_create_Btn)){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(createLayout);
            createDialog = builder.create();
            createDialog.show();
            
        }
        
        if(v==createDialog.findViewById(R.id.createBtn)){
            String username="";
            String password="";
            createNewUser(username,password);
        }
    }

    private void createNewUser(String username, String password) {

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
}
