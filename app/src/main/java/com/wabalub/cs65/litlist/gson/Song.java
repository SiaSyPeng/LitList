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

    public Song() {
        //empty constructor
    }

    public void Song(Long down_votes, Long up_votes, ArrayList<String> downVote_list_user, ArrayList<String> upVote_list_user) {
        this.down_votes = down_votes;
        this.up_votes = up_votes;
        this.downVote_list_user = downVote_list_user;
        this.upVote_list_user = upVote_list_user;
    }


}
