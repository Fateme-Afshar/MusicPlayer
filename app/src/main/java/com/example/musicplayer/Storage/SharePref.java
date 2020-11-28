package com.example.musicplayer.Storage;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.util.Observable;

public class SharePref{
    public static final String KEY_STATE_MUSIC = "stateMusic";

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

    private static SharedPreferences getSharedPreferences(Context leekSafeContext) {
        return leekSafeContext.
                getSharedPreferences(
                        leekSafeContext.getPackageName(),
                        Context.MODE_PRIVATE);
    }
}
