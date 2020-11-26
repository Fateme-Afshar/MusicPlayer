package com.example.musicplayer.Storeg;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePref {
    public static final String KEY_STATE_MUSIC = "stateMusic";

    public static String getStateMusic(Context context) {
        Context leekSafeContext = context.getApplicationContext();

        return getSharedPreferences(leekSafeContext).
                getString(KEY_STATE_MUSIC,null);
    }

    public static void setStateMusic(Context context, String string){
        Context leekSafeContext = context.getApplicationContext();

        getSharedPreferences(leekSafeContext).
                edit().
                putString(KEY_STATE_MUSIC,string).
                apply();
    }

    private static SharedPreferences getSharedPreferences(Context leekSafeContext) {
        return leekSafeContext.
                getSharedPreferences(
                        "com.example.musicplayer",
                        Context.MODE_PRIVATE);
    }
}
