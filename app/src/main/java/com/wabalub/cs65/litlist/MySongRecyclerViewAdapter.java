package com.wabalub.cs65.litlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Joiner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.gson.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Track} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MySongRecyclerViewAdapter extends RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder> {

    private final List<Song> mValues;
    public static final String TAG = "SONG_ADAPTER";
    private final Context context;
    private final OnListFragmentInteractionListener mListener;

    public MySongRecyclerViewAdapter(Context context, List<Song> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_song, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Song song = mValues.get(position);
        Track track = PlayerService.spotifyService.getTrack(song.id);

        holder.title.setText(track.name);

        List<String> names = new ArrayList<>();
        for (ArtistSimple i : track.artists) {
            names.add(i.name);
        }
        Joiner joiner = Joiner.on(", ");
        holder.artist.setText(joiner.join(names));

        Image image = track.album.images.get(0);
        if (image != null) {
            Picasso.with(context).load(image.url).into(holder.albumArt);
        }

        holder.mItem = mValues.get(position);
        holder.title.setText(track.name);
        holder.voteCount.setText(String.format("%s", song.upVote_list_user.size() - song.downVote_list_user.size()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.upvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO upvote the track ID, then get the updated playlist
                Log.d(TAG, "Upvote clicked");
                Toast.makeText(context, "Upvote clicked", Toast.LENGTH_SHORT).show();
                upvoteSong(song);
            }
        });

        holder.downvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO downvote the track ID, then get the updated playlist
                Log.d(TAG, "Downvote clicked");
                Toast.makeText(context, "Downvote clicked", Toast.LENGTH_SHORT).show();
                downvoteSong(song);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView title;
        public final TextView artist;
        public final ImageView albumArt;
        public final ImageButton upvoteBtn;
        public final ImageButton downvoteBtn;
        public final TextView voteCount;
        public Song mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.song_title);
            artist = view.findViewById(R.id.song_artist);
            albumArt = view.findViewById(R.id.song_album_art);
            upvoteBtn = view.findViewById(R.id.upvote_button);
            downvoteBtn = view.findViewById(R.id.downvote_button);
            voteCount = view.findViewById(R.id.vote_count);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }

    /*
    // upVote && downVote transactions
    private void upVoteClicked(DatabaseReference songRef) {
        songRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Song s = mutableData.getValue(Song.class);
                if (s == null) {
                    return Transaction.success(mutableData);
                }

                if (s.vote.containsKey(MainActivity.userID)) {
                    // downVote the track
                    s.up_votes = s.up_votes + 1;
                    s.up_votes.put(MainActivity.userID, true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }
    */


    /*
    private void downVoteClicked(DatabaseReference songRef) {
        songRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Song s = mutableData.getValue(Song.class);
                if (s == null) {
                    return Transaction.success(mutableData);
                }

                if (s.vote.containsKey(MainActivity.userID)) {
                    // downVote the track
                    s.down_votes = s.down_votes + 1;
                    s.down_votes.put(getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(p);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b,
                                   DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

    }
    */

    /**
     * Method to upvote a song
     */
    private void upvoteSong(Song song){
        if(song.downVote_list_user.contains(MainActivity.userID)){
            song.downVote_list_user.remove(MainActivity.userID);
        }
        else if(!song.upVote_list_user.contains(MainActivity.userID))
            song.upVote_list_user.add(MainActivity.userID);
        sortSongsByVote();
        notifyDataSetChanged();
    }

    /**
     * Method to downvote a song
     */
    private void downvoteSong(Song song){
        if(song.upVote_list_user.contains(MainActivity.userID)){
            song.upVote_list_user.remove(MainActivity.userID);
        }
        else if(!song.downVote_list_user.contains(MainActivity.userID))
            song.downVote_list_user.add(MainActivity.userID);
        sortSongsByVote();
        notifyDataSetChanged();
    }

    private void sortSongsByVote() {
        Collections.sort(MainActivity.playlist.songs,
                new Comparator<Song>(){

                    @Override
                    public int compare(Song song1, Song song2) {
                        return (song1.upVote_list_user.size() - song1.downVote_list_user.size()) -
                                ((song2.upVote_list_user.size()) - song2.downVote_list_user.size());
                    }
                });
    }
}
