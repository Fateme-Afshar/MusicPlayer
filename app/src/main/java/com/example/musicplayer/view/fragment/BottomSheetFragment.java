package com.example.musicplayer.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.R;
import com.example.musicplayer.Utils.SeekBarRunnable;
import com.example.musicplayer.databinding.FragmentBottomSheetBinding;
import com.example.musicplayer.viewModel.MusicPlayerViewModel;

public class BottomSheetFragment extends Fragment {
    private MusicPlayerViewModel mViewModel;
    private FragmentBottomSheetBinding mBinding;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public static BottomSheetFragment newInstance() {
        BottomSheetFragment fragment = new BottomSheetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(getActivity()).
                get(MusicPlayerViewModel.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_bottom_sheet,
                container,
                false);

        mViewModel.setCoverImg(mViewModel.getMusic().getAlbumId(),
                mBinding.imgCoverBottomSheet);
        mBinding.setViewModel(mViewModel);
        mBinding.setFragment(this);
        setupSeekBar();
        return mBinding.getRoot();
    }

    private void setupSeekBar() {
        SeekBarRunnable seekBarRunnable =
                new SeekBarRunnable(mViewModel.getMediaPlayer(), mBinding.seekbar);
        mViewModel.setSeekBarRunnable(mBinding.seekbar);
        if (mViewModel.getMusic().isPlaying())
            new Thread(seekBarRunnable).start();

        mBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int x = (int) Math.ceil(progress / 1000f);

                if (x == 0 && mViewModel.getMediaPlayer() != null && !mViewModel.getMediaPlayer().isPlaying())
                    seekBar.setProgress(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mViewModel.getMediaPlayer() != null && mViewModel.getMediaPlayer().isPlaying()) {
                    mViewModel.getMediaPlayer().seekTo(seekBar.getProgress());
                }
            }
        });
    }

    public void setupVisibility() {
        if (mViewModel.getMusic().isPlaying()) {
            mBinding.btnPlay.setVisibility(View.GONE);
            mBinding.btnPause.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnPlay.setVisibility(View.VISIBLE);
            mBinding.btnPause.setVisibility(View.GONE);
        }
    }
}