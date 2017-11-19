package com.wabalub.cs65.litlist.gson;

import java.util.ArrayList;

/**
 *  Object Song class for firebase
 */

public class Song {
    //for current track
    public Long down_votes;
    public Long up_votes;
    public ArrayList<String> downVote_list_user = new ArrayList<>();
    public ArrayList<String> upVote_list_user = new ArrayList<>();
    public String id;

    public Song() {
        //empty constructor
    }

    public Song(String id) {
        this.id = id;
        this.down_votes = (long) 0;
        this.up_votes = (long) 0;
        this.downVote_list_user = new ArrayList<String>();
        this.upVote_list_user =  new ArrayList<String>();
    }

    public Song(String id, Long down_votes, Long up_votes, ArrayList<String> downVote_list_user, ArrayList<String> upVote_list_user) {
        this.id = id;
        this.down_votes = down_votes;
        this.up_votes = up_votes;
        this.downVote_list_user = downVote_list_user;
        this.upVote_list_user = upVote_list_user;
    }
}
