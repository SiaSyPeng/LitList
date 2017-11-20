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
    public Integer index;

    public Song() {
        //empty constructor
    }

    public Song(String id, Integer index) {
        this.id = id;
        this.index = index;
        this.downVote_list_user = new ArrayList<String>();
        this.upVote_list_user =  new ArrayList<String>();
    }

    public Song(String id, ArrayList<String> downVote_list_user, ArrayList<String> upVote_list_user, Integer index) {
        this.id = id;
        this.downVote_list_user = downVote_list_user;
        this.upVote_list_user = upVote_list_user;
        this.index = index;
    }
}
