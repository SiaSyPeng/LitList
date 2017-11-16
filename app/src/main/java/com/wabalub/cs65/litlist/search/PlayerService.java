package com.wabalub.cs65.litlist.search;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.wabalub.cs65.litlist.MainActivity;
import com.wabalub.cs65.litlist.R;

public class PlayerService extends Service {

    public static TrackPlayer player = new TrackPlayer();
    private static final String ACTION_STOP_SERVICE = "STOP";
    public static final int SERVICE_ID = 1;
    public static final String TAG = "PLAYER_SERVICE";
    private static final int PLAYER_NOTI_ID = 0;

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

        // get the current cat we are tracking based on the id passed in
        int catIndex = intent.getIntExtra("catindex", 0);

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

        // setup a pending intent so if the user clicks on the notification it can open the main activity
        PendingIntent resultPendingIntent = getPendingIntent();

        // build the notification
        Notification.Builder builder = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon)
                        .setContentTitle("Playing ")
                        .setContentText("Artist")
                        .setShowWhen(true)
                        .setContentIntent(resultPendingIntent);

        Notification notification = builder.build();
        startForeground(SERVICE_ID, notification);
    }

    @Override
    public void onDestroy() {
        player.release();
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
}