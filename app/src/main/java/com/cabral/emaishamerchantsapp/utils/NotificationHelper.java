package com.cabral.emaishamerchantsapp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationManagerCompat;

import com.cabral.emaishamerchantsapp.R;
import com.cabral.emaishamerchantsapp.HomeActivity;


/**
 * NotificationHelper is used to create new Notifications
 **/

public class NotificationHelper {
    
    
    public static final int NOTIFICATION_REQUEST_CODE = 100;
    
    
    //*********** Used to create Notifications ********//
    

    public static void showNewNotification(Context context, Intent intent, String title, String msg, Bitmap bitmap) {

        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent;

        if (intent != null) {
            notificationIntent = intent;
        }
        else {
            notificationIntent = new Intent(context.getApplicationContext(), HomeActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_ONE_SHOT);
    
    
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id = "e01", name =context.getString(R.string.app_name);
            String desc = context.getString(R.string.app_name);

            NotificationChannel channel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(desc);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(context , id);
        }
        else
            builder = new Notification.Builder(context);

        if (bitmap != null)
            builder.setStyle(new Notification.BigPictureStyle().bigPicture(bitmap));

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Create Notification
             notification = builder
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setTicker(context.getString(R.string.app_name))
                    .setSmallIcon(R.drawable.emaisha_logo)
                    .setSound(notificationSound)
                    .setLights(Color.WHITE, 3000, 3000)
                    .setVibrate(new long[]{1000, 1000})
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true).setColor(Color.GREEN)
                    .setContentIntent(pendingIntent)
                    .build();
        }else {
             notification = builder
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setTicker(context.getString(R.string.app_name))
                    .setSmallIcon(R.drawable.emaisha_logo)
                    .setSound(notificationSound)
                    .setLights(Color.WHITE, 3000, 3000)
                    .setVibrate(new long[]{1000, 1000})
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }


        notificationManager.notify(NOTIFICATION_REQUEST_CODE, notification);
        
    }
    
    
}

