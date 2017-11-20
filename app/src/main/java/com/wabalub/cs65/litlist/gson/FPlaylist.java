package com.wabalub.cs65.litlist.gson;


import java.util.ArrayList;
/**
 * Object playlist class for firebase
 */

public class FPlaylist {

    // basic info
    public String name; // TODO add to firebase, we need a name
    public String creator; // TODO add to firebase, also need a creator
    public String curr_track_id;
    public Double curr_song_pos;
    public Double lat;
    public Double lon;
    public int id; // TODO need to assign IDs to playlists so users know which one to rejoin
    public String key;

    // Songs in the list
    public ArrayList<Song> songs = new ArrayList<>();

    // User in the list
    public ArrayList<String> users_listening = new ArrayList<>();

    public FPlaylist() {
        // Default constructor required for calls to DataSnapshot.getValue(Playlist.class)
    }


    // Made this into a constructor, was a method for some reason
    public FPlaylist(String name, String creator, String curr_track_id, Double curr_song_pos,
                    Double lat, Double lon, ArrayList<Song> songs, ArrayList<String> users_listening, String key) {
        this.name = name;
        this.creator = creator;
        this.curr_track_id = curr_track_id;
        this.curr_song_pos = curr_song_pos;
        this.lat = lat;
        this.lon = lon;
        this.songs = songs;
        this.users_listening = users_listening;
        this.key = key;
        // if(!users_listening.isEmpty()) this.user_lis_tot = len(users_listening)-1;
    }
}