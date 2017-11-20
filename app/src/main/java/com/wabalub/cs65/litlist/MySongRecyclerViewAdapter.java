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

import com.google.common.base.Joiner;
import com.squareup.picasso.Picasso;
import com.wabalub.cs65.litlist.PlaylistFragment.OnListFragmentInteractionListener;
import com.wabalub.cs65.litlist.gson.Song;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Track} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MySongRecyclerViewAdapter extends RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder> {

    private final List<Track> mValues;
    public static final String TAG = "SONG_ADAPTER";
    private final Context context;
    private final OnListFragmentInteractionListener mListener;

    public MySongRecyclerViewAdapter(Context context, List<Track> items, OnListFragmentInteractionListener listener) {
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
        Track track = mValues.get(position);

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
        holder.title.setText(mValues.get(position).name);

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
                // TODO upvote the track ID

            }
        });

        holder.downvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO downvote the track ID

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
        public Track mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            title = view.findViewById(R.id.song_title);
            artist = view.findViewById(R.id.song_artist);
            albumArt = view.findViewById(R.id.song_album_art);
            upvoteBtn = view.findViewById(R.id.upvote_button);
            downvoteBtn = view.findViewById(R.id.downvote_button);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + title.getText() + "'";
        }
    }

}
