package com.example.musicplayer.Repository;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.example.musicplayer.Model.Music;

import java.util.ArrayList;
import java.util.List;

public class MusicRepository {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static List<Music> getMusics(Context context) {
        Context leakSafeContext = context.getApplicationContext();
        List<Music> musicList = new ArrayList<>();

        ContentResolver contentResolver = leakSafeContext.getContentResolver();
        Uri songUriFromEXTERNAL = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songCursorEXTERNAL = contentResolver.
                query(songUriFromEXTERNAL,
                        null,
                        null,
                        null,
                        null);

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

        int songName = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int songSingerName = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int songPath = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int albumId=cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

        while (cursor.moveToNext()) {
            Music music = new Music();

            music.setPath(cursor.getString(songPath));
            music.setName(cursor.getString(songName));
            music.setSingerName(cursor.getString(songSingerName));
            music.setAlbumId(cursor.getInt(albumId));
            musicList.add(music);
        }

        return musicList;
    }
}
