package com.basicelixir.pawel.torrentnotifier;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class NottifiactionService extends IntentService {


    String TAG = "pawell";
    ArrayList<NewTorrentMovies> newTorrentMovies;
    ArrayList<Movie> moviesAvailable;
    String[] camExcetions = {".CAM.", "CAMRip", "HDCAM", "HD-TC", ".CAM-", "HD-TS"};
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Movie> movieList;

    public static final String NEW_FILMS ="newtorrents";

    Context context;
    android.support.v4.os.ResultReceiver rr;
    boolean contains;

    public NottifiactionService() {
        super("NottifiactionService");
        moviesAvailable = new ArrayList<Movie>();
        context = this;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        rr = intent.getParcelableExtra("reciver");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Torrent torrent = new Torrent();
                try {
                    newTorrentMovies = torrent.execute().get();
                    newTorrentMovies = checkForCam(newTorrentMovies);
                    movieList =new ArrayList<Movie>();

                    if(firebaseAuth.getCurrentUser()!=null){
                        firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("movieList").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for(DataSnapshot d:dataSnapshot.getChildren()){
                                    Movie movie = new Movie();
                                    boolean available=false;
                                    movie.setTitle(d.getKey());
                                    for(DataSnapshot d2 :d.getChildren()) {
                                        if (d2.getKey().equals("url")) {
                                            movie.setMovieURL(d2.getValue().toString());
                                        }else if( d2.getKey().equals("available")){
                                            available = d2.getValue(Boolean.class);
                                            movie.setAvailableForDownload(d2.getValue(Boolean.class));
                                        }
                                    }
                                    if(!available){
                                        movieList.add(movie);
                                    }
                                }
                                compareTorrentsWithMovieList( movieList, newTorrentMovies);
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void compareTorrentsWithMovieList(ArrayList<Movie> movieList, ArrayList<NewTorrentMovies> newTorrentMovies) {
        ArrayList<Movie> moviesAvailable = new ArrayList<>();
        for (int i = 0; i < movieList.size(); i++) {
            for (int j = 0; j <newTorrentMovies.size() ; j++) {
               if( newTorrentMovies.get(j).getImdbUrl().contains(movieList.get(i).getMovieURL())){

                   movieList.get(i).setTorrentFullName(newTorrentMovies.get(j).getFulltorrentName());
                   moviesAvailable.add(movieList.get(i));
               }
            }
        }

        //not allows duppicates -tar bort upprepade filmer
        Set<Movie> hs = new HashSet<>();
        hs.addAll(moviesAvailable);
        moviesAvailable.clear();
        moviesAvailable.addAll(hs);

        fireNotification(moviesAvailable);

        for (int i = 0; i < moviesAvailable.size(); i++) {
            firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("movieList").child(moviesAvailable.get(i).getTitle()).child("available").setValue(true);
            moviesAvailable.get(i).setAvailableForDownload(true);
        }
    }

    private ArrayList<NewTorrentMovies> checkForCam(ArrayList<NewTorrentMovies> newTorrentMovies) {
        ArrayList<NewTorrentMovies> nt = new ArrayList<>();
        for (int i = 0; i < newTorrentMovies.size(); i++) {

            if (!containsCam(newTorrentMovies.get(i).getFulltorrentName().toLowerCase())) {
                nt.add(newTorrentMovies.get(i));
            }

        }
        return nt;
    }

    public boolean containsCam(String torrentName) {
        contains = false;
        for (int i = 0; i < camExcetions.length; i++) {
            if (torrentName.contains(camExcetions[i].toLowerCase())) {
                contains = true;
            }
        }
        return contains;
    }

    public void fireNotification(ArrayList<Movie> moviesToShow) {
        NotificationCompat.Builder noti = new NotificationCompat.Builder(context);
        Notification f = new Notification();
        ArrayList<String> titles =new  ArrayList<>();
        if (moviesToShow != null && moviesToShow.size() > 0) {
            for (int j = 0; j < moviesToShow.size(); j++) {
                titles.add(moviesToShow.get(j).getTorrentFullName());
            }

            Bundle bundle = new Bundle();
            bundle.putStringArrayList("titles",titles);
            rr.send(1, bundle);

            for (int i = 0; i < moviesToShow.size(); i++) {
                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.putStringArrayListExtra(NEW_FILMS,titles);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setColor(Color.BLACK)
                        .setPriority(2)
                        .setContentText("new Movie Just got Available")
                        .setContentIntent(PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT))
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("Torrent Notifier")
                        .setDefaults(Notification.DEFAULT_ALL);

                NotificationManagerCompat.from(context).notify(3,builder.build());
            }

        }

    }
}
