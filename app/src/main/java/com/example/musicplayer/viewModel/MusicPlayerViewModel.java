package com.example.musicplayer.viewModel;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.musicplayer.Model.Music;
import com.example.musicplayer.Repository.MusicRepository;

import java.io.IOException;
import java.util.List;

public class MusicPlayerViewModel extends AndroidViewModel {
    private MusicRepository mRepository;
    private MediaPlayer mMediaPlayer;

    public Music getMusic() {
        return mMusic;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setMusic(Music music) {
        mMusic = music;
        mMediaPlayer = createMediaPlayer(mMusic.getPath());
    }

    private Music mMusic;

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
    }

    public void pauseMusic() {
            mMediaPlayer.stop();
    }

}
