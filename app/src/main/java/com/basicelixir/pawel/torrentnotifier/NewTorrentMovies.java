package com.basicelixir.pawel.torrentnotifier;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Pawel on 12/06/2016.
 */

public class NewTorrentMovies {
    String imdbUrl;
    String fulltorrentName;

    public NewTorrentMovies() {
    }

    public String getImdbUrl() {
        return imdbUrl;
    }

    public void setImdbUrl(String imdbUrl) {
        this.imdbUrl = imdbUrl;
    }

    public String getFulltorrentName() {
        return fulltorrentName;
    }

    public void setFulltorrentName(String fulltorrentName) {
        this.fulltorrentName = fulltorrentName;
    }


}
