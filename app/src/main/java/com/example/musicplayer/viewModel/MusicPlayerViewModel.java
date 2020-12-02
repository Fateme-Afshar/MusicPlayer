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
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.example.musicplayer.R;
import com.example.musicplayer.messageLoop.MusicLoader;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.repository.MusicRepository;
import com.example.musicplayer.storage.SharePref;
import com.example.musicplayer.utils.SeekBarRunnable;

import java.util.Map;


public class MusicPlayerViewModel extends AndroidViewModel {
    public static final String BASE_URI_ALBUM_ART =
            "content://media/external/audio/albumart";
    private Music mMusic;
    private MediaPlayer mMediaPlayer;
    private SeekBarRunnable mSeekBarRunnable;
    private MusicLoader<MusicPlayerViewModel> mMusicLoader;

    private MutableLiveData<Music> mMusicLiveData = new MutableLiveData<>();
    private MutableLiveData<MediaPlayer> mMediaPlayerLiveData = new MutableLiveData<>();

    private boolean mIsShuffle = false;
    private boolean mIsRepeat = false;

    private String mCurrentPath = SharePref.getLastMusicPath(getApplication());
    private int mPosition;

    public final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public final int REQUEST_PERMISSION_EXTERNAL = 523;

    public MusicPlayerViewModel(@NonNull Application application) {
        super(application);

        setupLooper();
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
    public Map<Integer, Music> getMusics(){
        return MusicRepository.getMusics(getApplication());
    }

    /**
     * this method check {@music.isPlaying()} if music playing , pause music else play music
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkPlayPauseMusic(){
        if (!mMusic.isPlaying()) {
            mMusic.setPlaying(true);

            if (!mCurrentPath.equals(mMusic.getPath()) || mMediaPlayer == null) {
                mCurrentPath = mMusic.getPath();
                if (mMediaPlayer!=null)
                    mMediaPlayer.stop();

                mMusicLoader.setCallbacks(new MusicLoader.MusicLoaderCallback<MusicPlayerViewModel>() {
                    @Override
                    public void onMusicLoader(MusicPlayerViewModel target, MediaPlayer mediaPlayer) {
                        mMediaPlayer = mediaPlayer;
                        mMediaPlayerLiveData.postValue(mediaPlayer);
                        mMediaPlayer.start();
                    }
                });

                mMusicLoader.createMessage(this, mMusic.getPath());
            } else {
                mMediaPlayer.start();
            }

            // for according to seek bar
            new Thread(mSeekBarRunnable).start();
        } else {
            mMediaPlayer.pause();
            mMusic.setPlaying(false);
        }
    }

    /**
     * this is overload from {@checkPlayPauseMusic()}.
     * @param music
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void checkPlayPauseMusic(Music music) {
        if (!music.isPlaying()) {
            music.setPlaying(true);

            if (!mCurrentPath.equals(music.getPath()) || mMediaPlayer == null) {
                mCurrentPath = music.getPath();
                if (mMediaPlayer!=null)
                    mMediaPlayer.stop();
                mMusicLoader.setCallbacks(new MusicLoader.MusicLoaderCallback<MusicPlayerViewModel>() {
                    @Override
                    public void onMusicLoader(MusicPlayerViewModel target, MediaPlayer mediaPlayer) {
                        mMediaPlayer = mediaPlayer;
                        mMediaPlayer.start();
                    }
                });

                mMusicLoader.createMessage(this, mMusic.getPath());
            } else {
                mMediaPlayer.start();
            }

            // for according to seek bar
            new Thread(mSeekBarRunnable).start();
        } else {
            mMediaPlayer.pause();
            music.setPlaying(false);
        }
    }

    public void releaseMediaPlayer() {
        if (mMediaPlayer != null)
            mMediaPlayer.release();
    }

    public void setupLooper() {
        mMusicLoader = new MusicLoader<>();
        mMusicLoader.start();
        mMusicLoader.getLooper();
    }

    public String getNormalText(String text,int textLength) {
        if (text.length() >= textLength) {
            String singerName = text.substring(0, 19);
            return singerName + "...";
        }
        return text;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void playPrevMusic() {
        mPosition--;
        if (mPosition>=0 && mPosition<=getMusics().size()-1){
            setMusic(getMusics().get(mPosition - 1));
        }else {
            setMusic(getMusics().get(0));
        }
        checkPlayPauseMusic();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void playNextMusic() {
        if (mPosition >= 0 && mPosition <= getMusics().size() - 1) {
            setMusic(getMusics().get(mPosition + 1));
            mPosition++;
        } else {
            setMusic(getMusics().get(0));
        }
        checkPlayPauseMusic();
    }

    public SeekBarRunnable getSeekBarRunnable() {
        return mSeekBarRunnable;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void playShuffleMusic() {
        mIsShuffle = true;
        int randomPosition = randomPosition(getMusics().size() - 1, 0);
        setMusic(getMusics().get(randomPosition));

        checkPlayPauseMusic();

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public void repeatMusic() {
        if (mIsRepeat) {
            mIsRepeat = false;
            mMediaPlayer.setLooping(false);
        } else {
            mMediaPlayer.setLooping(true);
        }
    }

    public void autoPlayMusic() {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (isShuffle()) {
                    playShuffleMusic();
                } else if (isRepeat()) {
                    repeatMusic();
                } else {
                    playNextMusic();
                }
                checkPlayPauseMusic();
            }
        });
    }
    
    public String extractMusicDurationToTimeFormat(int musicDuration) {
        int second = (musicDuration/1000);
        int minute = second / 60;
        int hour = minute / 60;

        if (hour != 0)
            return hour + " : " + minute + " : " + second%60;
        return minute + " : " + second%60;
    }

    public void setCoverImg(int albumId, ImageView view) {
        Uri sArtworkUri = Uri
                .parse(BASE_URI_ALBUM_ART);
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);

        Glide.with(getApplication()).
                load(uri).
                centerCrop().
                placeholder(R.drawable.ic_null_cover_img).
                into(view);
    }

    public Music getMusic() {
        return mMusic;
    }

    public void setMusic(Music music) {
        mMusic = music;
        mMusicLiveData.postValue(music);
    }

    public MutableLiveData<Music> getMusicLiveData() {
        return mMusicLiveData;
    }

    public MutableLiveData<MediaPlayer> getMediaPlayerLiveData() {
        return mMediaPlayerLiveData;
    }

    public void setSeekBarRunnable(SeekBar seekBar) {
        mSeekBarRunnable = new SeekBarRunnable(mMediaPlayer, seekBar);
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public MusicLoader<MusicPlayerViewModel> getMusicLoader() {
        return mMusicLoader;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public boolean isShuffle() {
        return mIsShuffle;
    }

    public void setShuffle(boolean shuffle) {
        mIsShuffle = shuffle;
    }

    public boolean isRepeat() {
        return mIsRepeat;
    }

    public void setRepeat(boolean repeat) {
        mIsRepeat = repeat;
    }

    public int randomPosition(int max,int min){
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }
}
