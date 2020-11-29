package com.example.musicplayer.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Observable;

public class SharePref{
    public static final String KEY_STATE_MUSIC = "stateMusic";
    public static final String KEY_LAST_MUSIC="lastMusic";

    public static boolean getStateMusic(Context context) {
        Context leekSafeContext = context.getApplicationContext();

        return getSharedPreferences(leekSafeContext).
                getBoolean(KEY_STATE_MUSIC,false);
    }

    public static void setStateMusic(Context context, boolean state){
        Context leekSafeContext = context.getApplicationContext();

        getSharedPreferences(leekSafeContext).
                edit().
                putBoolean(KEY_STATE_MUSIC,state).
                apply();
    }

    public static String getLastMusic(Context context){
        Context leekSafeContext = context.getApplicationContext();

        return getSharedPreferences(leekSafeContext).
                getString(KEY_LAST_MUSIC,null);
    }

    public static void setLastMusic(Context context, String lastMusicPath){
        Context leekSafeContext = context.getApplicationContext();

        getSharedPreferences(leekSafeContext).
                edit().
                putString(KEY_LAST_MUSIC,lastMusicPath).
                apply();
    }

    private static SharedPreferences getSharedPreferences(Context leekSafeContext) {
        return leekSafeContext.
                getSharedPreferences(
                        leekSafeContext.getPackageName(),
                        Context.MODE_PRIVATE);
    }
}
