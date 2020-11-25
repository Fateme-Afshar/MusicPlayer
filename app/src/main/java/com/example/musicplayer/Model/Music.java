package com.example.musicplayer.Model;

import android.graphics.Bitmap;

import java.util.UUID;

public class Music {
        private UUID mId ;
        private String mName;
        private String mPath;
        private String mSingerName;
        private int mDuration;
        private Bitmap mCoverImg;

    public Music() {
        mId=UUID.randomUUID();
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        mPath = path;
    }

    public String getSingerName() {
        return mSingerName;
    }

    public void setSingerName(String singerName) {
        mSingerName = singerName;
    }

    public Bitmap getCoverImg() {
        return mCoverImg;
    }

    public void setCoverImg(Bitmap coverImg) {
        mCoverImg = coverImg;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }
}
