package com.example.musicplayer.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationActionBroadCast extends BroadcastReceiver {

    public static final String ACTION_NAME = "actionName";
    public static final String MUSIC = "MUSIC";

    @Override
    public void onReceive(Context context, Intent intent) {
            context.sendBroadcast(new Intent(MUSIC).
                    putExtra(ACTION_NAME,intent.getAction()));
    }
}
