package com.example.musicplayer.View;

import androidx.fragment.app.Fragment;

import com.example.musicplayer.View.Fragment.PagerFragment;

public class MainActivity extends SingleFragmentActivity {


    @Override
    public Fragment getFragment() {
        return PagerFragment.newInstance();
    }
}