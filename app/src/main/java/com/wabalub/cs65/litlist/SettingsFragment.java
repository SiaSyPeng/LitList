package com.wabalub.cs65.litlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.share.Share;

public class SettingsFragment extends Fragment{
    private static final String TAG = "SETTINGS";
    private Spinner alertTypeSpinner;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEditTexts(view);
        setupSlider(view);
        addItemsOnAlertSpinner();
    }

    private void setupEditTexts(View view) {
        TextView nameEditText = view.findViewById(R.id.profile_name);
        nameEditText.setText(MainActivity.userEmail);
    }


    /**
     * Method to setup all buttons
     * @param view The view to these buttons are in
     */
    private void setupSlider(View view) {
        SeekBar zoomSlider = view.findViewById(R.id.zoom_slider);

        SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, 0);
        int zoom = sp.getInt(MainActivity.ZOOM_PREF, 0);

        zoomSlider.setProgress((int)MainActivity.zoom);
        zoomSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MainActivity.zoom = 7.5f * (float) seekBar.getProgress() / (float) seekBar.getMax() + 12.5f;
                SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(MainActivity.ZOOM_PREF, (int) ((MainActivity.zoom - 12.5) / 7.5f));
                editor.apply();
            }
        });

    }

    // add items into spinner dynamically
    public void addItemsOnAlertSpinner() {

        alertTypeSpinner = getActivity().findViewById(R.id.alert_type_spinner);
        Log.d(TAG, "Spinner = " + alertTypeSpinner.toString());

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.alert_types, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        alertTypeSpinner.setAdapter(adapter);
        alertTypeSpinner.setOnItemSelectedListener((MainActivity) getActivity());

        SharedPreferences sp = getActivity().getSharedPreferences(MainActivity.SHARED_PREF, 0);

        alertTypeSpinner.setSelection(sp.getInt("noti_type", 0));

    }
}
