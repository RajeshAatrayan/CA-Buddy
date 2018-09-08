package com.ibrickedlabs.drops;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;



import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by RajeshAatrayan on 08-09-2018.
 */

public class AlarmReciever extends BroadcastReceiver {
    public static final String LOG_TAG=AlarmReciever.class.getSimpleName();
    private static final String CHANNEL_ID = "personal_notification";
    private static final int NOTIFICATION_ID = 001;
    String exam = "CA";
    private  Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        Log.i(LOG_TAG+"in recieve method","recived");

        String subjectRecieved=intent.getStringExtra("subject");

        Log.i(LOG_TAG + "+fire", "Firing");
        createNotificationChannel();
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        long[] pattern = {500, 500, 500, 500, 500, 500, 500, 500, 500};

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.not2)
                .setContentTitle("It's to time to read")
                .setContentText("You have " + subjectRecieved + " CA tomorrow!")
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setVibrate(pattern)
                .setLights(Color.RED, 3000, 3000)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setContentIntent(contentIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, mBuilder.build());








    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "personal notfication";
            String description = "Includes all the personal Notofication";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
