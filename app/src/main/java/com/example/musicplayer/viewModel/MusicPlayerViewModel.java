package com.example.musicplayer.viewModel;

import android.Manifest;
import android.app.Application;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.Bindable;
import androidx.lifecycle.AndroidViewModel;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Repository.MusicRepository;
import com.example.musicplayer.Storage.SharePref;
import com.example.musicplayer.model.Music;

import java.io.IOException;
import java.util.List;


public class MusicPlayerViewModel extends AndroidViewModel {
    private MusicRepository mRepository;
    private MediaPlayer mMediaPlayer;
    private Music mMusic;

    public Music getMusic() {
        return mMusic;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setMusic(Music music) {
        mMusic = music;
        mMediaPlayer = createMediaPlayer(mMusic.getPath());
    }


    public final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public final int REQUEST_PERMISSION_EXTERNAL = 523;

    public MusicPlayerViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MusicRepository();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<Music> getMusics(){
        return MusicRepository.getMusics(getApplication());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean arePermissionAgree() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (getApplication().checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private MediaPlayer createMediaPlayer(String path) {
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.setVolume(0.5f, 0.5f);
            mediaPlayer.setLooping(false);
            return mediaPlayer;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playMusic() {
        assert mMediaPlayer != null;
        mMediaPlayer.start();

        SharePref.setStateMusic(getApplication(), true);
    }

    public void pauseMusic() {
        mMediaPlayer.stop();
        SharePref.setStateMusic(getApplication(), true);
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
            SharePref.setStateMusic(getApplication(),false);
    }

    public void setMusicImg(ImageView imageView) {
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Uri uri = ContentUris.withAppendedId(sArtworkUri,
                mMusic.getAlbumId());
        Glide.with(getApplication()).
                load(uri).
                centerCrop().
                placeholder(R.drawable.ic_null_cover_img).
                into(imageView);
    }

    public boolean getMusicState() {
        return SharePref.getStateMusic(getApplication());
    }
}
