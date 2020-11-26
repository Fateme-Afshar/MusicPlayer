package com.example.musicplayer.View.Fragment;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.Adapter.MusicAdapter;
import com.example.musicplayer.Model.Music;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ExtractFromPath;
import com.example.musicplayer.viewModel.MusicPlayerViewModel;
import com.example.musicplayer.databinding.MainViewBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;

public class SongsFragment extends Fragment {
    private MusicAdapter mAdapter;
    private MainViewBinding mBinding;
    private BottomSheetBehavior mBehavior;

    private MusicPlayerViewModel mViewModel;

    public SongsFragment() {
        // Required empty public constructor
    }

    public static SongsFragment newInstance() {
        SongsFragment fragment = new SongsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MusicPlayerViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.main_view,
                container,
                false);
        setupBottomSheet();
        setupAdapter();

        mBinding.btnPauseBottomSheet.setVisibility(View.GONE);

        mBinding.btnPlayBottomSheet.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mViewModel.playMusic();
                mBinding.btnPlayBottomSheet.setVisibility(View.GONE);
                mBinding.btnPauseBottomSheet.setVisibility(View.VISIBLE);
            }
        });

        mBinding.btnPauseBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.pauseMusic();
                mBinding.btnPlayBottomSheet.setVisibility(View.VISIBLE);
                mBinding.btnPauseBottomSheet.setVisibility(View.GONE);
            }
        });
        return mBinding.getRoot();
    }

    private void setupBottomSheet() {
        mBehavior = BottomSheetBehavior.from(mBinding.bottomSheet);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mBinding.containerMusicInfo.setVisibility(View.GONE);
                    mBinding.btnPauseBottomSheet.setVisibility(View.GONE);
                    mBinding.btnPlayBottomSheet.setVisibility(View.GONE);
                } else {
                    mBinding.containerMusicInfo.setVisibility(View.VISIBLE);
                    mBinding.btnPauseBottomSheet.setVisibility(View.VISIBLE);
                    mBinding.btnPlayBottomSheet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter.getMusicNameList().size() == 0)
            mAdapter.setMusicNameList(mViewModel.getMusics());
    }

    private void setupAdapter() {
        mAdapter = new MusicAdapter(getContext());
        mAdapter.setCallback(new MusicAdapter.MusicAdapterCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean sendMusicInfo(Music music) {
                mViewModel.setMusic(music);
                mBinding.singerNameBottomSheet.setText(music.getSingerName());
                mBinding.songNameBottomSheet.setText(music.getName());
                try {
                    mBinding.imgCoverBottomSheet.setImageBitmap(
                            ExtractFromPath.getImgCoverSong(music.getPath()));
                } catch (Exception e) {
                    mBinding.imgCoverBottomSheet.setImageDrawable(
                            getActivity().getResources().getDrawable(R.drawable.ic_null_cover_img));
                }
                return false;
            }
        });
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mViewModel.releaseMediaPlayer();
    }
}