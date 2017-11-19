package com.wabalub.cs65.litlist.gson;

import java.util.ArrayList;

/**
 *   Object user class for firebase
 */

public class User {

    public String username;
    public Long userid;
    public ArrayList<String> playlist = new ArrayList<>();
    public ArrayList<String> downVote_list = new ArrayList<>();
    public ArrayList<String> upVote_List = new ArrayList<>();

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public void User(String username, Long userid, ArrayList<String> playlist, ArrayList<String> downVote_list, ArrayList<String> upVote_List) {
        this.username = username;
        this.userid = userid;
        this.playlist = playlist;
        this.downVote_list = downVote_list;
        this.upVote_List = upVote_List;
    }
}
