package com.wabalub.cs65.litlist;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.session.MediaSession;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.common.base.Joiner;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
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
        Notification.Builder builder;

        // if we don't have a song, notify with the default notification
        if(currentTrack == null) {
            // build the notification
            builder = new Notification.Builder(this)
                    .setContentTitle("LitList Audio Player")
                    .setContentText("Songs will be displayed here")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setStyle(new Notification.MediaStyle())
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setShowWhen(true);
            return;
        }

        // otherwise show the song that's playing
        else {

            // setup a pending intent so if the user clicks on the notification it can open the main activity
            PendingIntent resultPendingIntent = getPendingIntent();

            //get the album art
            Image album_art = currentTrack.album.images.get(0);
            Bitmap icon = null;
            try {
                icon = BitmapFactory.decodeStream((InputStream) new URL(album_art.url).getContent());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }

            // build the notification
            builder = new Notification.Builder(this)
                    .setContentTitle("Track: " + currentTrack.name)
                    .setContentText("Artist: " + namesToString(currentTrack.artists))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(icon)
                    .setStyle(new Notification.MediaStyle())
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setShowWhen(true)
                    .setContentIntent(resultPendingIntent);
        }

        Notification notification = builder.build();
        //startForeground(SERVICE_ID, notification);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Log.d(TAG, "Notifying!");
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
        else {
            Log.e(TAG,"Notification manager is null!");
        }
    }

    @Override
    public void onDestroy() {
        previewPlayer.release();
        //player.shutdown();
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private PendingIntent getPendingIntent(){
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(MainActivity.EXTRA_TOKEN, MainActivity.token);

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
