package com.example.musicplayer.MessageLoop;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.musicplayer.model.Music;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class MusicLoader<T> extends HandlerThread {
    public static final int WHAT_LOAD_MESSAGE = 1;
    private Handler mHandlerRequest;
    public static final String HANDLER_NAME = "MusicLoader";

    private ConcurrentHashMap<T,String> mRequestMap=new ConcurrentHashMap<>();

    private MusicLoaderCallback<T> mCallbacks;
    public MusicLoader() {
        super(HANDLER_NAME);
    }

    public void setCallbacks(MusicLoaderCallback<T> callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        mHandlerRequest=new Handler(){
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what==WHAT_LOAD_MESSAGE){
                    if (msg.obj==null)
                        return;
                    T target= (T) msg.obj;
                    String path=mRequestMap.get(target);

                    MediaPlayer mediaPlayer=createMediaPlayer(path);

                    mHandlerRequest.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mRequestMap.get(target)!=path)
                                return;
                            mCallbacks.onMusicLoader(target,mediaPlayer);
                        }
                    });
                }
            }
        };
    }

    public  void createMessage(T target,String path){
        mRequestMap.put(target,path);
        Message message=mHandlerRequest.obtainMessage(WHAT_LOAD_MESSAGE,target);

        message.sendToTarget();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MediaPlayer createMediaPlayer(String path) {
        MediaPlayer mediaPlayer=new MediaPlayer();
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

    public interface MusicLoaderCallback<T>{
        void onMusicLoader(T target, MediaPlayer mediaPlayer);
    }
}
