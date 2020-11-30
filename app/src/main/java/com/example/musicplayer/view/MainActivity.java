package com.example.musicplayer.view;

import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;
import com.example.musicplayer.view.fragment.BottomSheetFragment;
import com.example.musicplayer.view.fragment.PagerFragment;
import com.example.musicplayer.view.fragment.SongsFragment;

public class MainActivity extends SingleFragmentActivity
        implements SongsFragment.SongsFragmentCallbacks {


    @Override
    public Fragment getFragment() {
        return PagerFragment.newInstance();
    }

    @Override
    public void startBottomSheetFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.bottom_sheet_fragment_container,
                        BottomSheetFragment.newInstance()).
                commit();
    }

}