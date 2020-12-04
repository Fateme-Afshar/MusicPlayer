package com.example.musicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.example.musicplayer.notification.CreateNotification;
import com.example.musicplayer.viewModel.Playable;

public class MusicPlayerApplication extends Application{

    public static final String CHANNEL_NAME = "Music Player Channel";

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    private void createChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CreateNotification.CHANNEL_ID,
                            CHANNEL_NAME,
                            NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
