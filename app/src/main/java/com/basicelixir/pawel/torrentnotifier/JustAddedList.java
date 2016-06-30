package com.basicelixir.pawel.torrentnotifier;

import io.realm.Realm;
import io.realm.RealmObject;

/**
 * Created by Pawel on 15/06/2016.
 */

public class JustAddedList extends RealmObject {
    String TorrentName;
    String imdbLink;
    int i;

    public JustAddedList() {
    }

    public String getTorrentName() {
        return TorrentName;
    }

    public void setTorrentName(String torrentName) {
        TorrentName = torrentName;
    }

    public String getImdbLink() {
        return imdbLink;
    }

    public void setImdbLink(String imdbLink) {
        this.imdbLink = imdbLink;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }
}
