package com.wabalub.cs65.litlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
    }

    /**
     * Called when sign in button clicked
     * @param view the view
     */
    public void onSignInClicked(View view) {
        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
    }
}
