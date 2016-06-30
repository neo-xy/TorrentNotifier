package com.basicelixir.pawel.torrentnotifier;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import br.com.goncalves.pugnotification.notification.PugNotification;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class NottifiactionService extends IntentService {
    String TAG = "pawell";
    ArrayList<NewTorrentMovies> newTorrentMovies;
    ArrayList<Movie> moviesAvailable;
    String[] camExcetions = {".CAM.", "CAMRip", "HDCAM", "HD-TC", ".CAM-", "HD-TS"};

    Context context;
    ResultReceiver rr;
    Realm realm;
    String movieSaved = "";
    String torrentName;
    Realm realm2 = null;

    public NottifiactionService() {
        super("NottifiactionService");
        moviesAvailable = new ArrayList<Movie>();
        context = this;

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
                    realm = Realm.getDefaultInstance();
                    RealmQuery query = realm.where(Movie.class);
                    query.equalTo("availableForDownload", false);
                    RealmResults<Movie> movieRealmResults = query.findAll();

                    for (int i = 0; i < movieRealmResults.size(); i++) {
                        movieSaved = movieRealmResults.get(i).getMovieURL();
                        for (int j = 0; j < newTorrentMovies.size(); j++) {
                            if (newTorrentMovies.get(j).getImdbUrl().contains(movieSaved)) {
                                torrentName = newTorrentMovies.get(j).getFulltorrentName();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        //TODO instead of findFIrs change to findAll in case someone put the same movie twive in his list
                                        Movie movie = realm.where(Movie.class).equalTo("movieURL", movieSaved)
                                                .equalTo("availableForDownload", false)
                                                .findFirst();
                                        if (movie != null) {
                                            movie.setTorrentFullName(torrentName);
                                            movie.setAvailableForDownload(true);

                                            realm2 = Realm.getDefaultInstance();
                                            RealmResults<Movie> moviesToShowResult = realm2.where(Movie.class).notEqualTo("torrentFullName", "null")
                                                    .equalTo("availableForDownload", true)
                                                    .findAll();

                                            fireNotification(moviesToShowResult);
                                        }
                                    }
                                });
                            }
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } finally {
                    if (realm != null) {
                        realm.close();
                    }
                    if (realm2 != null) {
                        realm2.close();
                    }
                }
            }
        }).start();
    }

    private ArrayList<NewTorrentMovies> checkForCam(ArrayList<NewTorrentMovies> newTorrentMovies) {
        ArrayList<NewTorrentMovies> nt = new ArrayList<>();
        for (int i = 0; i < newTorrentMovies.size(); i++) {
            for (int j = 0; j < camExcetions.length; j++) {
                if (!newTorrentMovies.get(i).getFulltorrentName().toLowerCase().contains(camExcetions[j].toLowerCase())) {
                    nt.add(newTorrentMovies.get(i));
                }else{
                    Log.i(TAG, "checkForCam: "+ newTorrentMovies.get(i).getFulltorrentName());
                }
            }
        }
        return nt;
    }

    public void fireNotification(RealmResults<Movie> moviesToShow) {
        if (moviesToShow != null && moviesToShow.size() > 0) {
            for (int i = 0; i < moviesToShow.size(); i++) {
                String fullTorrentName = moviesToShow.get(i).getTorrentFullName();

                Bundle bundle = new Bundle();

                bundle.putString("torrent", fullTorrentName);
                rr.send(1, bundle);
                int t = new Random().nextInt(Integer.MAX_VALUE);
                PugNotification.with(context)
                        .load()
                        .smallIcon(R.mipmap.ic_launcher)
                        .title("Torrent Notifier")
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
