package com.chatting.chatsapp;

import android.app.NotificationManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived( remoteMessage );
        String title=remoteMessage.getNotification().getTitle();
        String body=remoteMessage.getNotification().getBody();

        NotificationCompat.Builder builder = new NotificationCompat.Builder( getApplicationContext(),"FrendReq" );
         builder.setContentTitle( title );
         builder.setContentTitle( body );
         builder.setSmallIcon( R.drawable.avator );

        NotificationManager manager= (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
        manager.notify(123,builder.build());
    }
}
