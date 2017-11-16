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

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.models.Track;

public class SearchPresenter implements Search.ActionListener {

    private static final String TAG = SearchPresenter.class.getSimpleName();
    public static final int PAGE_SIZE = 20;

    private final Context mContext;
    private final Search.View mView;
    private String mCurrentQuery;

    private SearchPager mSearchPager;
    private SearchPager.CompleteListener mSearchListener;


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            logMessage("Creating player service");
            MainActivity.player = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            MainActivity.player = null;
        }
    };

    public SearchPresenter(Context context, Search.View view) {
        mContext = context;
        mView = view;
    }

    @Override
    public void init(String accessToken) {
        logMessage("Api Client created");
        SpotifyApi spotifyApi = new SpotifyApi();

        if (accessToken != null) {
            spotifyApi.setAccessToken(accessToken);
        } else {
            logError("No valid access token");
        }

        mSearchPager = new SearchPager(spotifyApi.getService());

        mContext.bindService(PlayerService.getIntent(mContext), mServiceConnection, Activity.BIND_AUTO_CREATE);
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
        mContext.unbindService(mServiceConnection);
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

        if (MainActivity.player == null) {
            logMessage("Player is null");
            return;
        }

        String currentTrackUrl = MainActivity.player.getCurrentTrack();

        if (currentTrackUrl == null || !currentTrackUrl.equals(previewUrl)) {
            logMessage("Playing song");
            MainActivity.player.play(previewUrl);
        } else if (MainActivity.player.isPlaying()) {
            logMessage("Pausing the song");
            MainActivity.player.pause();
        } else {
            logMessage("Resuming the song");
            MainActivity.player.resume();
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
}
