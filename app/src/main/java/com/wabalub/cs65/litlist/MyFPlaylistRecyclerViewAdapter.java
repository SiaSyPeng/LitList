package com.wabalub.cs65.litlist;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wabalub.cs65.litlist.RankingFragment.OnListFragmentInteractionListener;

import com.wabalub.cs65.litlist.gson.FPlaylist;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link com.wabalub.cs65.litlist.gson.FPlaylist} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyFPlaylistRecyclerViewAdapter extends RecyclerView.Adapter<MyFPlaylistRecyclerViewAdapter.ViewHolder> {

    private final List<FPlaylist> mValues;
    private final OnListFragmentInteractionListener mListener;
    public static String TAG = "RANKING_ADAPTER";

    public MyFPlaylistRecyclerViewAdapter(List<FPlaylist> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "oncreateViewholder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_playlist_item, parent, false);
        sortPlaylistsByListeners();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.name.setText(mValues.get(position).name);
        holder.creator.setText(mValues.get(position).creator);
        holder.listenerCount.setText(String.format("%s", countListeners(mValues.get(position))));

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
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView name;
        public final TextView creator;
        public final TextView listenerCount;
        public FPlaylist mItem;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            name = (TextView) view.findViewById(R.id.playlist_item_name);
            creator = (TextView) view.findViewById(R.id.playlist_item_creator);
            listenerCount = (TextView) view.findViewById(R.id.playlist_item_listener_count);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + name.getText() + "'";
        }
    }


    private void sortPlaylistsByListeners() {
        Collections.sort(MainActivity.playlists.playlists,
                new Comparator<FPlaylist>() {
                    @Override
                    public int compare(FPlaylist playlist1, FPlaylist playlist2) {
                        return countListeners(playlist2) - countListeners(playlist1);
                    }
                }
        );
    }

    private int countListeners(FPlaylist playlist){
        int total = 0;
        for(String userID : playlist.users_listening){
            if(userID != null)
                total ++;
        }
        return total;
    }
}
