package com.wabalub.cs65.litlist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends Fragment{
    private static final String TAG = "SETTINGS";

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

        setupButtons(view);
        addItemsOnAlertSpinner();
    }


    /**
     * Method to setup all buttons
     * @param view The view to these buttons are in
     */
    private void setupButtons(View view) {

    }

    // add items into spinner dynamically
    public void addItemsOnAlertSpinner() {
        /*
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
        */
    }
}
