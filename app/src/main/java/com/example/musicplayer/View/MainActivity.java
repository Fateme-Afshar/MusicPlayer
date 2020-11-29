package com.example.musicplayer.View;

import androidx.fragment.app.Fragment;

import com.example.musicplayer.R;
import com.example.musicplayer.View.Fragment.BottomSheetFragment;
import com.example.musicplayer.View.Fragment.PagerFragment;
import com.example.musicplayer.View.Fragment.SongsFragment;
import com.example.musicplayer.databinding.FragmentBottomSheetBinding;

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