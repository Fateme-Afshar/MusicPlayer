package com.example.musicplayer.viewModel;

import android.Manifest;
import android.app.Application;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.Repository.MusicRepository;
import com.example.musicplayer.Utils.SeekBarRunnable;
import com.example.musicplayer.model.Music;

import java.io.IOException;
import java.util.List;


public class MusicPlayerViewModel extends AndroidViewModel {
    private Music mMusic;
    private MediaPlayer mMediaPlayer;
    private SeekBarRunnable mSeekBarRunnable;

    public final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public final int REQUEST_PERMISSION_EXTERNAL = 523;

    public MusicPlayerViewModel(@NonNull Application application) {
        super(application);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean arePermissionAgree() {
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (getApplication().checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<Music> getMusics(){
        return MusicRepository.getMusics(getApplication());
    }

    public Music getMusic() {
        return mMusic;
    }

    public void setMusic(Music music) {
        mMusic = music;
    }

    public void setSeekBarRunnable(SeekBar seekBar){
        mSeekBarRunnable=new SeekBarRunnable(mMediaPlayer,seekBar);
    }
    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
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

    public String getNormalText(String text,int textLength) {
        if (text.length() >= textLength) {
            String singerName = text.substring(0, 19);
            return singerName + "...";
        }
        return text;
    }

    public String extractMusicDurationToTimeFormat(int musicDuration) {
        int second = (musicDuration/1000);
        int minute = second / 60;
        int hour = minute / 60;

        if (hour != 0)
            return hour + " : " + minute + " : " + second%60;
        return minute + " : " + second%60;
    }

    public void setCoverImg(int albumId, ImageView view){
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri uri= ContentUris.withAppendedId(sArtworkUri,albumId);

        Glide.with(getApplication()).
                load(uri).
                centerCrop().
                placeholder(R.drawable.ic_null_cover_img).
                into(view);

    }

    /**
     * this method check {@music.isPlaying()} if music playing , pause music else play music
     * @param music
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkPlayPauseMusic(Music music){

        mMediaPlayer=new MediaPlayer();
        mMediaPlayer=createMediaPlayer(music.getPath());
        if (!mMusic.isPlaying()){
            mMusic.setPlaying(true);
            mMediaPlayer.start();
            new Thread(mSeekBarRunnable).start();
        }else {
            mMediaPlayer.pause();
            mMusic.setPlaying(false);
        }
    }

    public void releaseMediaPlayer(){
        mMediaPlayer.release();
        mMediaPlayer=null;
    }
}
