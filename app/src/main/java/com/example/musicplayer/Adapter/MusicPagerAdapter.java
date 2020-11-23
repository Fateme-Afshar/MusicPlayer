package com.example.musicplayer.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.musicplayer.View.Fragment.AlbumsFragment;
import com.example.musicplayer.View.Fragment.SingersFragment;
import com.example.musicplayer.View.Fragment.SongsFragment;

public class MusicPagerAdapter extends FragmentStateAdapter {

    public static final int TAB_COUNT = 3;

    public MusicPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return SongsFragment.newInstance();
            case 1:
               return SingersFragment.newInstance();
            case 2:
                return AlbumsFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}
