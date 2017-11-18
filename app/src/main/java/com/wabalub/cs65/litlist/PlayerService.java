package com.wabalub.cs65.litlist;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.common.base.Joiner;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Track;

public class PlayerService extends Service {

    public static TrackPlayer previewPlayer = new TrackPlayer();
    public static SpotifyPlayer player;
    public static Track currentTrack = null;
    public static final int SERVICE_ID = 1;
    public static final int NOTIFICATION_ID = 2048;
    public static final String TAG = "PLAYER_SERVICE";

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

     @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);

        // setup the notification
        updateNotification();
        return START_STICKY;
    }


    /**
     * Method to setup or update the notification
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void updateNotification(){
        Log.d(TAG, "Updating notification");
        if(currentTrack == null) return;

        // setup a pending intent so if the user clicks on the notification it can open the main activity
        PendingIntent resultPendingIntent = getPendingIntent();

        // build the notification
        Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Playing " + currentTrack.name)
                        .setContentText("Artist " + namesToString(currentTrack.artists))
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setShowWhen(true)
                        .setContentIntent(resultPendingIntent);

        Notification notification = builder.build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    @Override
    public void onDestroy() {
        previewPlayer.release();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private PendingIntent getPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack
        stackBuilder.addParentStack(MainActivity.class);

        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        // Gets a PendingIntent containing the entire back stack
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, PlayerService.class);
    }

    public static String namesToString(List<ArtistSimple> artists){
        List<String> names = new ArrayList<>();
        for (ArtistSimple i : artists) {
            names.add(i.name);
        }
        Joiner joiner = Joiner.on(", ");
        return joiner.join(names);
    }
}
