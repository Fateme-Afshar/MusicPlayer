package com.example.musicplayer.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.example.musicplayer.model.Music;

import java.util.HashMap;
import java.util.Map;

public class MusicRepository {

    /**
     * get music list from ExternalStorage
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Map<Integer,Music> getMusics(Context context) {
        Context leakSafeContext = context.getApplicationContext();
        Map<Integer,Music> musicList = new HashMap<>();

        Cursor songCursorEXTERNAL = getCursor(leakSafeContext);

        if (songCursorEXTERNAL != null) {
            musicList.putAll(getInfoFromCursor(songCursorEXTERNAL));
            songCursorEXTERNAL.close();
        }
        return musicList;
    }

    /**
     * Extract music list from cursor
     * @param cursor
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static Map<Integer,Music> getInfoFromCursor(Cursor cursor) {
        Map<Integer,Music> musicList = new HashMap<>();
        cursor.moveToNext();
        int counter=0;
        while (cursor.moveToNext()) {
            Music music = getMusic(cursor);

            musicList.put(counter, music);
            counter++;
        }

        return musicList;
    }

    /**
     * get cursor without any selection,selection args and etc.
     * this is default cursor for songUriFromEXTERNAL.
     *
     * @param leakSafeContext
     * @return
     */
    public static Cursor getCursor(Context leakSafeContext) {
        Uri songUriFromEXTERNAL =
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = leakSafeContext.getContentResolver();

        return contentResolver.
                query(songUriFromEXTERNAL,
                        null,
                        null,
                        null,
                        null);
    }

    /**
     * Query for specific music by path
     * @param leakSafeContext
     * @param path
     * @return
     */
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

    /**
     * Extract music information  from cursor
     * @param cursor
     * @return
     */
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

    /**
     * Extract specific music information from cursor.
     * @param context
     * @param path
     * @return
     */
    public static Music getMusic(Context context,String path) {
        Cursor cursor=getCursor(context,path);
        cursor.moveToFirst();
        int songName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int songSingerName = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int songPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int albumId = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

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
