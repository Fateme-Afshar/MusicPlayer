package com.example.musicplayer.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;

public class ExtractFromPath {

    public static Bitmap getImgCoverSong(String songPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(songPath);
        byte[] art = retriever.getEmbeddedPicture();

        return BitmapFactory
                .decodeByteArray(art, 0, art.length);
    }

    public static String getMusicName(String path) {
        String[] nameSection = path.split(File.separator);
        String fullMusicName = nameSection[nameSection.length - 1];
        return fullMusicName.substring(0, fullMusicName.lastIndexOf("."));
    }
}
