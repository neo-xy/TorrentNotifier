package com.basicelixir.pawel.torrentnotifier;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Pawel on 19/05/2016.
 */
public class Movie extends RealmObject {
    String title;
    @PrimaryKey
   private String movieURL;
   boolean availableForDownload;
    boolean activated;
    String torrentFullName;


    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public String getTorrentFullName() {
        return torrentFullName;
    }

    public void setTorrentFullName(String torrentFullName) {
        this.torrentFullName = torrentFullName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMovieURL() {
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
