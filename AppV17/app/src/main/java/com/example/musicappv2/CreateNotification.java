package com.example.musicappv2;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicappv2.services.NotificationActionService;

public class CreateNotification {


    public static final String CHANNEL_ID = "actionprevious";

    public static final String ACTION_PREVIOUS = "actionprevious";
    public static final String ACTION_PLAY = "actionplay";
    public static final String ACTION_NEXT = "actionnext";

    public static Notification notification;

    @SuppressLint("MissingPermission")
    public static void createNotification(Context context, String track, int playbutton, int pos, int size) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            MediaSessionCompat mediaSessionCompact = new MediaSessionCompat(context, "tag");


            PendingIntent pendingIntentPrevious;
            int drw_previous;
            if(pos == 0) {
                pendingIntentPrevious = null;
                drw_previous = 0;
            }
            else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_PREVIOUS);
                pendingIntentPrevious = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_IMMUTABLE);
                drw_previous = R.drawable.baseline_skip_previous_24;
            }



            Intent intentPlay = new Intent(context, NotificationActionService.class)
                    .setAction(ACTION_PLAY);
            PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(context, 0,
                    intentPlay, PendingIntent.FLAG_IMMUTABLE);


            PendingIntent pendingIntentNext;
            int drw_next;
            if(pos == 0) {
                pendingIntentNext = null;
                drw_next = 0;
            }
            else {
                Intent intentPrevious = new Intent(context, NotificationActionService.class)
                        .setAction(ACTION_NEXT);
                pendingIntentNext = PendingIntent.getBroadcast(context, 0,
                        intentPrevious, PendingIntent.FLAG_IMMUTABLE);
                drw_next = R.drawable.baseline_skip_next_24;
            }


            //create Notification
            notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_music_note_24)
                    .setContentTitle(track)
                    .setOnlyAlertOnce(true)
                    .setShowWhen(false)
                    .addAction(drw_previous, "Previous", pendingIntentPrevious)
                    .addAction(playbutton, "Play", pendingIntentPlay)
                    .addAction(drw_next, "Next", pendingIntentNext)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setShowActionsInCompactView(0, 1, 2)
                            .setMediaSession(mediaSessionCompact.getSessionToken()))
                    .setPriority(NotificationCompat.PRIORITY_LOW)
                    .build();


            notificationManagerCompat.notify(1, notification);
        }
    }



}
