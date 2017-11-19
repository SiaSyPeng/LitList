package com.wabalub.cs65.litlist;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.SupportMapFragment;

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
    private ImageView playlistImage;
    private Button joinCreateButton;
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
            // mParam1 = getArguments().getString(ARG_PARAM1);
        }
        setupGoogleMapsFragment();
    }

    private void setupGoogleMapsFragment(){
            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.google_map);

            if(mapFragment == null) {
                Log.e(TAG, "Map fragment is null");
                return;
            }

            mapFragment.getMapAsync((MainActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        playlistImage = view.findViewById(R.id.map_playlist_image);
        playlistNameText = view.findViewById(R.id.map_playlist_name);
        playlistCreatorText = view.findViewById(R.id.map_playlist_creator);
        joinCreateButton = view.findViewById(R.id.join_create_button);

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
        if(MainActivity.playlist == null){
            playlistNameText.setText(R.string.click_markers_prompt);
            playlistCreatorText.setText("");
            joinCreateButton.setText(R.string.create);
        }

        // otherwise we are on a cat, so update the panel view
        else {
            playlistNameText.setText(MainActivity.playlist.name);
            playlistCreatorText.setText(MainActivity.playlist.creator);
            joinCreateButton.setText(R.string.join);
        }
    }
}
