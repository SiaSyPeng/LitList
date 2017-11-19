package com.wabalub.cs65.litlist.gson;

import java.util.ArrayList;

/**
 *  * Object playlists class for firebase
 */

public class FPlaylists {
    public ArrayList<FPlaylist> playlists = new ArrayList<>();

    public FPlaylists() {
        //empty constructor

    }

    public FPlaylists(ArrayList<FPlaylist> playlists) {
        this.playlists = playlists;
    }
}