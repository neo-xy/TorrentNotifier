package com.basicelixir.pawel.torrentnotifier;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pawel on 19/05/2016.
 */
public class Movie {
    String title;
    @PrimaryKey
    private String movieURL;
    private boolean availableForDownload;
    private boolean activated;
    private String torrentFullName;


    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

     String getTorrentFullName() {
        return torrentFullName;
    }

     void setTorrentFullName(String torrentFullName) {
        this.torrentFullName = torrentFullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

     String getMovieURL() {
        return movieURL;
    }

    public void setMovieURL(String movieURL) {
        this.movieURL = movieURL;
    }

    public boolean isAvailableForDownload() {
        return availableForDownload;
    }

    public void setAvailableForDownload(boolean availableForDownload) {
        this.availableForDownload = availableForDownload;
    }


}
