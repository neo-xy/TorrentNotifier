package com.basicelixir.pawel.torrentnotifier;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import br.com.goncalves.pugnotification.notification.PugNotification;

/**
 * Created by Pawel on 23/09/2016.
 */
public class NottifiactionService extends IntentService {


    String TAG = "pawell";
    ArrayList<NewTorrentMovies> newTorrentMovies;
    ArrayList<Movie> moviesAvailable;
    String[] camExcetions = {".CAM.", "CAMRip", "HDCAM", "HD-TC", ".CAM-", "HD-TS"};
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ArrayList<Movie> movieList;

    Context context;
    android.support.v4.os.ResultReceiver rr;
    boolean contains;

    public NottifiactionService() {
        super("NottifiactionService");
        Log.i(TAG, "NottifiactionService: ");
        moviesAvailable = new ArrayList<Movie>();
        context = this;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        rr = (android.support.v4.os.ResultReceiver) intent.getParcelableExtra("reciver");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "onHandleIntent: ");
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
                                    movie.setTitle(d.getKey().toString());
                                    for(DataSnapshot d2 :d.getChildren()) {
                                        if (d2.getKey().equals("url")) {
                                            movie.setMovieURL(d2.getValue().toString());
                                        }else if( d2.getKey().equals("available")){
                                            available = d2.getValue(Boolean.class);
                                            movie.setAvailableForDownload(d2.getValue(Boolean.class));
                                        }
                                    }
                                    if(available==false){
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
                } finally {

                }
            }
        }).start();


    }

    private void compareTorrentsWithMovieList(ArrayList<Movie> movieList, ArrayList<NewTorrentMovies> newTorrentMovies) {
        Log.i(TAG, "compareTorrentsWithMovieList: ");
        ArrayList<Movie> moviesAvailable = new ArrayList<>();
        for (int i = 0; i < movieList.size(); i++) {
            for (int j = 0; j <newTorrentMovies.size() ; j++) {
               if( newTorrentMovies.get(j).getImdbUrl().contains(movieList.get(i).getMovieURL())){

                   movieList.get(i).setTorrentFullName(newTorrentMovies.get(j).getFulltorrentName());
                   moviesAvailable.add(movieList.get(i));
               }
            }
        }

        //not allows duppicates -ta bort upprepade filmer
        Set<Movie> hs = new HashSet<>();
        hs.addAll(moviesAvailable);
        moviesAvailable.clear();
        moviesAvailable.addAll(hs);

        fireNotification(moviesAvailable);

        for (int i = 0; i < moviesAvailable.size(); i++) {
            firebaseDatabase.getReference().child("users").child(firebaseAuth.getCurrentUser().getUid()).child("movieList").child(moviesAvailable.get(i).getTitle()).child("available").setValue(true);
            moviesAvailable.get(i).setAvailableForDownload(true);
        }
        Log.i(TAG, "compareTorrentsWithMovieList: moviesavailable "+ moviesAvailable.size());

        Log.i(TAG, "compareTorrentsWithMovieList: movieList "+ movieList.size());
        Log.i(TAG, "compareTorrentsWithMovieList: newtorrents "+ newTorrentMovies.size());
    }

    private ArrayList<NewTorrentMovies> checkForCam(ArrayList<NewTorrentMovies> newTorrentMovies) {
        ArrayList<NewTorrentMovies> nt = new ArrayList<>();
        for (int i = 0; i < newTorrentMovies.size(); i++) {

            if (containsCam(newTorrentMovies.get(i).getFulltorrentName().toLowerCase()) == false) {
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
        Log.i(TAG, "fireNotification: ");
        ArrayList<String> titles =new  ArrayList<>();
        if (moviesToShow != null && moviesToShow.size() > 0) {
            for (int j = 0; j < moviesToShow.size(); j++) {
                titles.add(moviesToShow.get(j).getTorrentFullName());
            }
            Bundle bundle = new Bundle();

            bundle.putStringArrayList("titles",titles);
           // bundle.putString("torrent", fullTorrentName);
            rr.send(1, bundle);

            for (int i = 0; i < moviesToShow.size(); i++) {
                String fullTorrentName = moviesToShow.get(i).getTitle();


                int t = new Random().nextInt(Integer.MAX_VALUE);
                PugNotification.with(context)
                        .load()
                        .smallIcon(R.mipmap.ic_launcher)
                        .title("Your movie just got available")
                        .identifier(t)
                        .bigTextStyle(fullTorrentName)
                        .click(MainActivity.class)
                        .flags(Notification.DEFAULT_ALL)
                        .simple()
                        .build();
            }
        }
    }
}
