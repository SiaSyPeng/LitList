package com.wabalub.cs65.litlist;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class CreatePlaylistActivity extends AppCompatActivity {
    public static String EXTRA_NAME = "name";
    public static String EXTRA_CREATOR = "creator";
    public static String EXTRA_TAG = "tag";
    public static String EXTRA_IS_PUBLIC = "is_public";

    EditText nameEditText;
    EditText creatorEditText;
    EditText tagEditText;
    Switch isPublicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_playlist);
        //TODO make this have edit texts and return a new playlist object based on what the user made
    }

    @Override
    protected void onStart() {
        super.onStart();
        nameEditText = findViewById(R.id.new_playlist_name_edit_text);
        creatorEditText = findViewById(R.id.new_playlist_creator_edit_text);
        tagEditText = findViewById(R.id.new_playlist_tag_edit_text);
        isPublicSwitch = findViewById(R.id.public_private_switch);
    }

    /**
     * Method to return the results of what the user editted in the activity
     * @param view view
     */
    public void onCreateClicked(View view) {


        Intent data = new Intent();
        //---set the data to pass back---
        data.putExtra(EXTRA_NAME, nameEditText.getText().toString());
        data.putExtra(EXTRA_CREATOR, creatorEditText.getText().toString());
        data.putExtra(EXTRA_TAG, tagEditText.getText().toString());
        data.putExtra(EXTRA_IS_PUBLIC, isPublicSwitch.isChecked());

        setResult(RESULT_OK, data);
        //---close the activity---
        finish();
    }
}
