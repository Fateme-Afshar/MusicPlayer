package com.example.musicplayer.ViewModel;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;

import com.example.musicplayer.Model.Music;
import com.example.musicplayer.Repository.MusicRepository;

import java.util.List;

public class MusicPlayerViewModel extends AndroidViewModel {
    private MusicRepository mRepository;

    public  final String[] PERMISSIONS={
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public  final int REQUEST_PERMISSION_EXTERNAL=523;

    public MusicPlayerViewModel(@NonNull Application application) {
        super(application);
        mRepository=new MusicRepository();
    }

    public MusicRepository getRepository() {
        return mRepository;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public List<Music> getMusics(){
        return MusicRepository.getMusics(getApplication());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean arePermissionAgree(){
        for (int i = 0; i < PERMISSIONS.length; i++) {
            if (getApplication().checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true;
        }
        return false;
    }
}
