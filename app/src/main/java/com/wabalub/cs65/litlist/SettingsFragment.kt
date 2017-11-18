package com.wabalub.cs65.litlist

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.android.volley.*
import com.android.volley.toolbox.Volley
import org.jetbrains.anko.defaultSharedPreferences
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * Fragment for "Settings" Tab
 */
class SettingsFragment:  PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val USER_PREFS = "profile_data" //Shared with other activities

    //TODO: URL for server

    // Constant key to get value from sharedPref
    private val USER_STRING = "Username"
    private val ALER_STRING = "alert"

    //volley request
    private lateinit var queue: RequestQueue


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */

    fun PlaylistFragment() {}

    companion object {
        @JvmStatic
        fun newInstance(): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //init
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.fragment_preferences)

        // Instantiate the RequestQueue.
        queue = Volley.newRequestQueue(activity)

        //Set default values (depending on whether values have previously been set)
        PreferenceManager.setDefaultValues(context, USER_PREFS,
                Context.MODE_PRIVATE, R.xml.fragment_preferences, true)

        //Get the preferences we'll be assigning listeners to here
        val signoutPref = findPreference(getString(R.string.prefs_signout_key))
        val aboutPref = findPreference(getString(R.string.prefs_about_key))
        val prefs = activity.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)

        // Clean up when the user sign out
        signoutPref.setOnPreferenceClickListener { _ ->

            //Remove everything from user sharedPrefs
            activity.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()

            //Go back to login
            val i = Intent(activity.applicationContext,SignInActivity::class.java)
            startActivity(i)
            true
        }

        //If user clicks on the "about" page, go to our git
        aboutPref.setOnPreferenceClickListener { _ ->
            val url = "https://gitlab.cs.dartmouth.edu/wubalub/LitList"
            val intent = Intent()

            intent.data = Uri.parse(url)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            activity.applicationContext.startActivity(intent) //sends them to our git
            true
        }

    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * When user change setting preference fragements,
     * Update local storage and server storage
     */
    override fun onSharedPreferenceChanged(prefs: SharedPreferences?, key: String?) {
        val jsonReq = JSONObject()

        // POST Request must have name and password, we get it from sharedpref, which is saved/updated when login
        // And since POST in server right now use overwrite not update, we'll have to keep track of all the fields
        // This can be done by a GET request,
        // or just simply get from sharedpref since we saved data from GET into local when logging in
        val userPrefs = activity.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
        var alert = userPrefs.getString(ALER_STRING, null)

        //Update Local
        when (key) {
        //List preference for alerts
            getString(R.string.prefs_alert_key) -> {
                alert = activity.defaultSharedPreferences.getString(key,"v")

                //Put it into sharedPrefs storage
                activity.getSharedPreferences(USER_PREFS, Context.MODE_PRIVATE)
                        .edit()
                        .putString(key,alert)
                        .apply()
            }
        }

        //Update json request
        try {
            jsonReq.put(ALER_STRING, alert)
        } catch (e: JSONException) {
            // Warn the user that something is wrong; do not connect
            Log.d("JSON", "Invalid JSON: " + e.toString())
            Toast.makeText(context, "Invalid JSON", Toast.LENGTH_SHORT).show()
        }

        // TODO: POST jsonrequest to the reserver

    }
}