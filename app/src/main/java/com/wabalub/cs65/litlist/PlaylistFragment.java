package com.wabalub.cs65.litlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.wabalub.cs65.litlist.gson.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 *
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PlaylistFragment extends Fragment {

    private static final String TAG = "PLAYLIST FRAGMENT";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PlaylistFragment() {
    }

    public static PlaylistFragment newInstance() {
        PlaylistFragment fragment = new PlaylistFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");

        View view = inflater.inflate(R.layout.fragment_playlist, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.playlist);

        // Set the adapter
        Context context = view.getContext();
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        //recyclerView.setAdapter(new MySongRecyclerViewAdapter(MainActivity.playlist.getSongList(), mListener));
        List<Song> songs;
        if(MainActivity.playlist != null)
             songs = MainActivity.playlist.songs;
        else songs = new ArrayList<Song>();

        recyclerView.setAdapter(new MySongRecyclerViewAdapter(getContext(), songs, mListener));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // if the user has not joined a playlist, hide the views!
        if(MainActivity.playlist == null) {
            view.findViewById(R.id.share_button).setVisibility(View.GONE);
            view.findViewById(R.id.play_button).setVisibility(View.GONE);
            view.findViewById(R.id.add_by_search_button).setVisibility(View.GONE);
            view.findViewById(R.id.mute_button).setVisibility(View.GONE);
        }
        else {
            view.findViewById(R.id.share_button).setVisibility(View.VISIBLE);
            view.findViewById(R.id.play_button).setVisibility(View.VISIBLE);
            view.findViewById(R.id.add_by_search_button).setVisibility(View.VISIBLE);
            view.findViewById(R.id.mute_button).setVisibility(View.VISIBLE);
        }

        ImageButton playButton = view.findViewById(R.id.play_button);
        if(PlayerService.player == null || (PlayerService.player != null && PlayerService.player.getPlaybackState().isPlaying))
            playButton.setImageResource(R.drawable.pause);
        else playButton.setImageResource(R.drawable.play);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void onListFragmentInteraction(Song mItem);
    }
}
