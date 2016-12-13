package com.basicelixir.pawel.torrentnotifier;


public class Movie {
    String title;
    public String movieURL;
    public boolean availableForDownload;
    public boolean activated;
    public String torrentFullName;


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
