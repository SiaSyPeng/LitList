package com.wabalub.cs65.litlist.gson;


import java.util.ArrayList;
/**
 * Object playlist class for firebase
 */

public class FPlaylist {

    // basic info
    public String curr_track_id;
    public Double curr_song_pos;
    public Double lat;
    public Double lon;

    // Songs in the list
    public ArrayList<Song> songs = new ArrayList<>();
    // public List<Song> songs = new List<>();

    // User in the list
    public ArrayList<String> users_listening = new ArrayList<>();
    //public List<String> users_listening = new List<>();
    public Integer user_lis_tot = 0; // will be used in the rank to rank the playlist as a whole


    public FPlaylist() {
        // Default constructor required for calls to DataSnapshot.getValue(Playlist.class)
    }


    // Made this into a constructor, was a method for some reason
    public FPlaylist(String curr_track_id, Double curr_song_pos,
                    Double lat, Double lon,ArrayList<Song> songs, ArrayList<String> users_listening, Integer user_lis_tot) {
        this.curr_track_id = curr_track_id;
        this.curr_song_pos = curr_song_pos;
        this.lat = lat;
        this.lon = lon;
        this.songs = songs;
        this.users_listening = users_listening;
        // if(!users_listening.isEmpty()) this.user_lis_tot = len(users_listening)-1;
        this.user_lis_tot = user_lis_tot;
    }
}