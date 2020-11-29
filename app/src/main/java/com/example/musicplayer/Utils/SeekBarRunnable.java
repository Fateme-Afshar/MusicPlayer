package com.example.musicplayer.Utils;

import android.media.MediaPlayer;
import android.widget.SeekBar;

public class SeekBarRunnable implements Runnable{
    private MediaPlayer mMediaPlayer;
    private SeekBar mSeekBar;

    public SeekBarRunnable(MediaPlayer mediaPlayer, SeekBar seekBar) {
        mMediaPlayer = mediaPlayer;
        mSeekBar = seekBar;
    }

    @Override
    public void run() {

        int currentPosition = mMediaPlayer.getCurrentPosition();
        int total = mMediaPlayer.getDuration();


        while (mMediaPlayer != null && mMediaPlayer.isPlaying() && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mMediaPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }

            mSeekBar.setProgress(currentPosition);
        }
    }
}
