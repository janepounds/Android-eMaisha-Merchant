package com.cabral.emaishamerchant.network;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cabral.emaishamerchant.HomeActivity;
import com.cabral.emaishamerchant.orders.OrdersActivity;
import com.cabral.emaishamerchant.utils.NotificationHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);
        if(HomeActivity.context!=null)
        NetworkStateChecker.RegisterDeviceForFCM(getApplicationContext());

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Bitmap notificationBitmap = null;
        String notification_title, notification_message, notification_image = "";


        if (remoteMessage.getData().size() > 0) {
            notification_title = remoteMessage.getData().get("title");
            notification_message = remoteMessage.getData().get("message");
            //notification_image = remoteMessage.getData().get("image");
        } else {
            notification_title = remoteMessage.getNotification().getTitle();
            notification_message = remoteMessage.getNotification().getBody();
        }


        notificationBitmap = getBitmapFromUrl(notification_image);


        Intent notificationIntent = new Intent(getApplicationContext(), OrdersActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        NotificationHelper.showNewNotification
                (
                        getApplicationContext(),
                        notificationIntent,
                        notification_title,
                        notification_message,
                        notificationBitmap
                );

    }


    public Bitmap getBitmapFromUrl(String imageUrl) {
        if ("".equalsIgnoreCase(imageUrl)) {
            return null;
        }
        else {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
