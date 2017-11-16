package com.wabalub.cs65.litlist.search;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.wabalub.cs65.litlist.MainActivity;
import com.wabalub.cs65.litlist.my_libs.InternetMgmtLib;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.AbstractPreferences;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class SearchPresenter implements Search.ActionListener, InternetMgmtLib.InternetListener {

    private static final String TAG = SearchPresenter.class.getSimpleName();
    public static final int PAGE_SIZE = 20;
    private static final String ADD_URL = "...";
    private static final int ADD_REQUEST = 0;

    private final Context mContext;
    private final Search.View mView;
    private String mCurrentQuery;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchListener;


    public SearchPresenter(Context context, Search.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void init(String accessToken) {
        mSearchPager = new SearchPager(MainActivity.spotifyApi.getService());
    }


    @Override
    public void search(@Nullable String searchQuery) {
        if (searchQuery != null && !searchQuery.isEmpty() && !searchQuery.equals(mCurrentQuery)) {
            logMessage("query text submit " + searchQuery);
            mCurrentQuery = searchQuery;
            mView.reset();
            mSearchListener = new SearchPager.CompleteListener() {
                @Override
                public void onComplete(List<Track> items) {
                    mView.addData(items);
                }

                @Override
                public void onError(Throwable error) {
                    logError(error.getMessage());
                }
            };
            mSearchPager.getFirstPage(searchQuery, PAGE_SIZE, mSearchListener);
        }
    }


    @Override
    public void destroy() {
    }

    @Override
    @Nullable
    public String getCurrentQuery() {
        return mCurrentQuery;
    }

    @Override
    public void resume() {
        mContext.stopService(PlayerService.getIntent(mContext));
    }

    @Override
    public void pause() {
        mContext.startService(PlayerService.getIntent(mContext));
    }

    @Override
    public void loadMoreResults() {
        Log.d(TAG, "Load more...");
        mSearchPager.getNextPage(mSearchListener);
    }

    @Override
    public void selectTrack(Track item) {
        logMessage("Selected track " + item.toString());

        String previewUrl = item.preview_url;

        if (previewUrl == null) {
            logMessage("Track doesn't have a preview");
            return;
        }

        if (PlayerService.player == null) {
            logMessage("Player is null");
            return;
        }

        String currentTrackUrl = PlayerService.player.getCurrentTrack();

        if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl)) {
            logMessage("Playing song");
            PlayerService.player.play(previewUrl);
        } else if (PlayerService.player.isPlaying()) {
            logMessage("Pausing the song");
            PlayerService.player.pause();
        } else {
            logMessage("Resuming the song");
            PlayerService.player.resume();
        }
    }

    private void logError(String msg) {
        Toast.makeText(mContext, "Error: " + msg, Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }

    private void logMessage(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        Log.d(TAG, msg);
    }

    /**
     * Method to request adding a song to the playlist
     * @param track the track to add tot he playlist
     */
    private void requestAddToPlaylist(Track track){
        Map<String, String> args = new HashMap<String, String>();
        args.put("user_id", MainActivity.userID);
        args.put("playlist_id", MainActivity.playlist.getId());
        args.put("track_id", track.id);
        InternetMgmtLib.getString(this, MainActivity.queue, ADD_URL, ADD_REQUEST, args);
    }

    @Override
    public void onResponse(int requestCode, String res) {
        // TODO
        // get back a playlist from the server, save the playlist to MainActivity.playlist, then return to main activity
    }

    @Override
    public void onErrorResponse(int requestCode, String res) {

    }
}
