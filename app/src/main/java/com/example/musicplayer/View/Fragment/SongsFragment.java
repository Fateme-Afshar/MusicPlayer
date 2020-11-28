package com.example.musicplayer.View.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.Adapter.MusicAdapter;
import com.example.musicplayer.Storage.SharePref;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.R;
import com.example.musicplayer.viewModel.MusicPlayerViewModel;
import com.example.musicplayer.databinding.MainViewBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

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

        setHasOptionsMenu(true);
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

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_songs,menu);

        MenuItem menuItem=menu.findItem(R.id.menu_search);
        SearchView searchView= (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                setupAdapter();
                return false;
            }
        });
    }

    private void setupBottomSheet() {
        mBehavior = BottomSheetBehavior.from(mBinding.bottomSheet);
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

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
        if (mAdapter==null) {
            mAdapter = new MusicAdapter(getContext());
            mAdapter.setCallback(new MusicAdapter.MusicAdapterCallback() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void sendMusicInfo(Music music) {
                    mViewModel.setMusic(music);
                    if (SharePref.getStateMusic(getContext())) {
                        mViewModel.pauseMusic();
                        SharePref.setStateMusic(getContext(),false);
                    }
                    mViewModel.setMusicImg(mBinding.imgCoverBottomSheet);
                    mBinding.setViewModel(mViewModel);

                }

                @Override
                public void playMusic(Music music) {

                }
            });
            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.recyclerView.setAdapter(mAdapter);
        }else {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mViewModel.releaseMediaPlayer();
    }
}