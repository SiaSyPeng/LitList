package com.wabalub.cs65.litlist;

import android.*;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public static final String TAG = "MAP_FRAGMENT";

    public static final int MY_PERMISSIONS_REQUEST = 301;
    private ImageView playlistImage;
    private Button joinCreateButton;
    SupportMapFragment googleMapsFragment;
    private TextView playlistNameText, playlistCreatorText;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        requestPermissions();
    }

    public void requestPermissions(){
        // Here, thisActivity is the current activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED ||
                    getActivity().checkSelfPermission(android.Manifest.permission.INTERNET)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST);
            }
            else {
                setupMap();
            }
        }
    }

    public void setupMap(){
        Log.d(TAG, "Setting up map");
        FragmentManager fm = getChildFragmentManager();
        googleMapsFragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (googleMapsFragment == null) {
            googleMapsFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, googleMapsFragment).commit();
        }
        googleMapsFragment.getMapAsync((MainActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "on view created!");
        super.onViewCreated(view, savedInstanceState);
        playlistImage = view.findViewById(R.id.map_playlist_image);
        playlistNameText = view.findViewById(R.id.map_playlist_name);
        playlistCreatorText = view.findViewById(R.id.map_playlist_creator);
        joinCreateButton = view.findViewById(R.id.join_create_button);
        if(playlistNameText == null) Log.e(TAG, "name text is null");
        if(playlistCreatorText == null) Log.e(TAG, "creator text is null");
        if(joinCreateButton == null) Log.e(TAG, "join button is null");

        updatePanel();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e(TAG, "onAttach called");
    }



    @Override
    public void onDetach() {
        super.onDetach();

        // solves the Illegal state exception error
        try {
            Field childFragmentManager = Fragment.class
                    .getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

     /**
     * Method to update the cat panel at the bottom of the screen
     */
    public void updatePanel(){

        // if we have a playlist
        if(MainActivity.viewedPlaylist == null){
            Log.d(TAG, "No playlist viewed");
            if(playlistNameText != null) playlistNameText.setVisibility(View.GONE);
            if(playlistCreatorText != null) playlistCreatorText.setText(R.string.click_markers_prompt);
            if(joinCreateButton != null)joinCreateButton.setText(R.string.create);
        }

        // otherwise we are looking at a playlist, so update the panel view
        else {
            Log.d(TAG, "Playlist viewed");
            if(playlistNameText != null) {
                playlistNameText.setVisibility(View.VISIBLE);
                playlistNameText.setText(MainActivity.viewedPlaylist.name);
            }
            if(playlistCreatorText != null) playlistCreatorText.setText(MainActivity.viewedPlaylist.creator);
            if(joinCreateButton != null) joinCreateButton.setText(R.string.join);
        }
        boolean enabled = MainActivity.viewedPlaylist != MainActivity.playlist || MainActivity.viewedPlaylist == null;
        if(joinCreateButton != null) {
            joinCreateButton.setEnabled(enabled);
            if(enabled) joinCreateButton.setAlpha(1f);
            else joinCreateButton.setAlpha(0.5f);
        }
    }
}
