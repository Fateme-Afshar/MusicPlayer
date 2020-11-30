package com.example.musicplayer.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.example.musicplayer.model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static List<Music> getMusics(Context context) {
        Context leakSafeContext = context.getApplicationContext();
        List<Music> musicList = new ArrayList<>();

        Cursor songCursorEXTERNAL = getCursor(leakSafeContext);

        if (songCursorEXTERNAL != null) {
            musicList.addAll(getInfoFromCursor(songCursorEXTERNAL));
        }

        songCursorEXTERNAL.close();
        return musicList;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static List<Music> getInfoFromCursor(Cursor cursor) {
        List<Music> musicList = new ArrayList<>();
        cursor.moveToNext();

        while (cursor.moveToNext()) {
            Music music = getMusic(cursor);

            musicList.add(music);
        }

        return musicList;
    }

    public static Cursor getCursor(Context leakSafeContext) {
        Uri mSongUriFromEXTERNAL =
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = leakSafeContext.getContentResolver();

        return contentResolver.
                query(mSongUriFromEXTERNAL,
                        null,
                        null,
                        null,
                        null);
    }

    public static Cursor getCursor(Context leakSafeContext, String path) {
        Uri mSongUriFromEXTERNAL =
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = leakSafeContext.getContentResolver();

        return contentResolver.
                query(mSongUriFromEXTERNAL,
                        null,
                        "_data = ?",
                        new String[]{path},
                        null);
    }

    public static Music getMusic(Cursor cursor) {
        int songName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int songSingerName = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int songPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

        return new Music(cursor.getString(songName)
                , cursor.getString(songSingerName)
                , cursor.getString(songPath)
                , cursor.getInt(albumId));
    }

    public static Music getMusic(Context context,String path) {
        Cursor cursor=getCursor(context,path);
        cursor.moveToFirst();
        int songName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int songSingerName = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int songPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
        if (cursor == null)
            return null;
        try {
            return new Music(cursor.getString(songName)
                    , cursor.getString(songSingerName)
                    , cursor.getString(songPath)
                    , cursor.getInt(albumId));
        } finally {
            cursor.close();
        }
    }

    //Cursor curTaskList = db.query("timebasedlist", null, "col1 = ? AND col2 = ?", new String[]{"val1", "val2"}, null, null, null);

}
