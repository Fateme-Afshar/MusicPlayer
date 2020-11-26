package com.example.musicplayer.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;

import java.io.File;

public class ExtractFromPath {

    public static Bitmap getImgCoverSong(String songPath, Context context) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(songPath);
        byte[] art = retriever.getEmbeddedPicture();

        return Bitmap.createScaledBitmap(BitmapFactory
                .decodeByteArray(art, 0, art.length),
                120,
                120,
                false);
    }

    public static String getMusicName(String path) {
        String[] nameSection = path.split(File.separator);
        String fullMusicName = nameSection[nameSection.length - 1];
        return fullMusicName.substring(0, fullMusicName.lastIndexOf("."));
    }

    public static int getMusicDuration(String path) {
        MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
        metaRetriever.setDataSource(path);
        return Integer.parseInt(metaRetriever.
                extractMetadata(MediaMetadataRetriever.
                        METADATA_KEY_DURATION));
    }
}
