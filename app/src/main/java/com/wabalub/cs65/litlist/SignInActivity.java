package com.wabalub.cs65.litlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.*;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import java.util.concurrent.TimeUnit;

public class SignInActivity extends AppCompatActivity implements ConnectionStateCallback, Player.NotificationCallback {

    private static final String TAG = SignInActivity.class.getSimpleName();

    @SuppressWarnings("SpellCheckingInspection")
    private static final String CLIENT_ID = "8a91678afa49446c9aff1beaabe9c807";
    @SuppressWarnings("SpellCheckingInspection")
    private static final String REDIRECT_URI = "testschema://callback";

    private static final int REQUEST_CODE = 1337;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        String token = CredentialsHandler.getToken(this);
        if (token == null) {
            // if we don't have a valid token, make them sign in again
            setContentView(R.layout.activity_sign_in);
        } else {
            // otherwise open the main activity
            initializePlayer(token);
            startMainActivity(token);
        }
    }

    /**
     * Called when sign in button clicked
     * @param view the view
     */
    public void onSignInClicked(View view) {
        final AuthenticationRequest request = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(new String[]{"playlist-read", "user-read-private", "streaming"})
                .build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        logMessage("onActivityResult called for login");

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    String token = response.getAccessToken();
                    logMessage("Got token: " + token);
                    initializePlayer(token);
                    startMainActivity(token);
                    break;

                // Auth flow returned an error
                case ERROR:
                    logError("Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    logError("Auth result: " + response.getType());
            }
        }
    }

    /**
     * Method to init the spotify player
     * @param token access token
     */
    private void initializePlayer(String token){
        CredentialsHandler.setToken(this, token, 1, TimeUnit.HOURS);
        Config playerConfig = new Config(this, token, CLIENT_ID);
        Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
            @Override
            public void onInitialized(SpotifyPlayer spotifyPlayer) {
                logMessage("Player created");
                PlayerService.player = spotifyPlayer;
                PlayerService.player.addConnectionStateCallback(SignInActivity.this);
                PlayerService.player.addNotificationCallback(SignInActivity.this);
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
            }
        });
    }

    private void startMainActivity(String token) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(MainActivity.EXTRA_TOKEN, token);
        startActivity(intent);
        finish();
    }

    private void logError(String msg) {
        Toast.makeText(this, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Error error) {
    }

    @Override
    public void onTemporaryError() {
    }

    @Override
    public void onConnectionMessage(String s) {
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
    }

    @Override
    public void onPlaybackError(Error error) {
    }
}
