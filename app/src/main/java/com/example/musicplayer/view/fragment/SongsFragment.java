package com.example.musicplayer.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.adapter.MusicAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.repository.MusicRepository;
import com.example.musicplayer.notification.CreateNotification;
import com.example.musicplayer.service.NotificationActionBroadCast;
import com.example.musicplayer.service.OnClearFromRecentService;
import com.example.musicplayer.storage.SharePref;
import com.example.musicplayer.databinding.MainViewBinding;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.viewModel.MusicPlayerViewModel;
import com.example.musicplayer.viewModel.Playable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class SongsFragment extends Fragment implements Playable {
    public static final String DEFAULT_MUSIC_PATH =
            "/storage/6507-0AD9/1212/02 - Delam Havato Kardeh.mp3";

    private MusicAdapter mAdapter;
    private MainViewBinding mBinding;

    private BottomSheetBehavior mBehavior;

    private SongsFragmentCallbacks mCallbacks;

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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof SongsFragmentCallbacks)
            mCallbacks = (SongsFragmentCallbacks) context;
        else
            throw new ClassCastException
                    ("At first, Must Implement SongsFragmentCallbacks Interface");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).
                get(MusicPlayerViewModel.class);
        mViewModel.getMusicLiveData().observe(this, new Observer<Music>() {
            @Override
            public void onChanged(Music music) {
                mViewModel.setCoverImg(mViewModel.getMusic().getAlbumId(),
                        mBinding.imgCoverFooter);
                mBinding.setViewModel(mViewModel);
                mBinding.notifyChange();

                SharePref.setLastMusicPath(getContext(),music.getPath());
            }
        });

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            getActivity().registerReceiver(mBroadcastReceiver,new IntentFilter(NotificationActionBroadCast.MUSIC));
            getActivity().startService(new Intent(getActivity().getBaseContext(), OnClearFromRecentService.class));
        }
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
        mBinding.setFragment(SongsFragment.this);

        /*mViewModel.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
                if (mViewModel.isShuffle()) {
                    mViewModel.playShuffleMusic();
                } else if (mViewModel.isRepeat()) {
                    mViewModel.repeatMusic();
                } else {
                    mViewModel.playNextMusic();
                }
                mViewModel.checkPlayPauseMusic();
            }
        });*/
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
                mAdapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
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

                switch (newState) {
                    case BottomSheetBehavior.STATE_SETTLING:
                        mCallbacks.startBottomSheetFragment();
                        mBinding.btnPauseBottomSheet.setVisibility(View.INVISIBLE);
                        mBinding.btnPlayBottomSheet.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (mViewModel.getMusic().isPlaying())
                            mBinding.btnPauseBottomSheet.setVisibility(View.VISIBLE);
                        else
                            mBinding.btnPlayBottomSheet.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
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
        if (mAdapter.getMusics().size() == 0) {
            mAdapter.setMusicNameList(mViewModel.getMusics());
        }

        setupLastMusicShow();
        CreateNotification.createNotification(getContext(),
                R.drawable.ic_play,
                R.drawable.ic_play,
                mViewModel.getMusics().size(),
                mViewModel.getMusic());
    }

    private void setupLastMusicShow() {
        if (SharePref.getLastMusicPath(getContext())==null){
            SharePref.setLastMusicPath(getContext(), DEFAULT_MUSIC_PATH);
        }
        mViewModel.setMusic(getLastMusic());
        updateBottomNavUI();
    }

    private Music getLastMusic() {

        return MusicRepository.getMusic(
                 getContext(),
                 SharePref.getLastMusicPath(getContext()));
    }

    private void setupAdapter() {
        if (mAdapter==null) {
            mAdapter = new MusicAdapter(getContext(), mViewModel);
            mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.recyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void updateBottomNavUI() {
        mViewModel.setCoverImg(mViewModel.getMusic().getAlbumId(),
                mBinding.imgCoverFooter);

        mBinding.setViewModel(mViewModel);

        setupVisibility();
    }

    public void setupVisibility() {
        if (mViewModel.getMusic().isPlaying()) {
            mBinding.btnPlayBottomSheet.setVisibility(View.INVISIBLE);
            mBinding.btnPauseBottomSheet.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnPlayBottomSheet.setVisibility(View.VISIBLE);
            mBinding.btnPauseBottomSheet.setVisibility(View.INVISIBLE);
        }
    }

    BroadcastReceiver mBroadcastReceiver=new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getExtras().getString(NotificationActionBroadCast.ACTION_NAME);

            switch (action){
                case CreateNotification.ACTION_PERV:
                    onMusicPrev();
                    break;
                case CreateNotification.ACTION_NEXT:
                    onMusicNext();
                    break;
                case CreateNotification.ACTION_PLAY:
                    if (mViewModel.getMusic().isPlaying()){
                        onMusicPause();
                    }else {
                        onMusicPlay();
                    }

            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        mViewModel.releaseMediaPlayer();


    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMusicPrev() {
            mViewModel.playPrevMusic();

            CreateNotification.createNotification(getContext(),
                    R.drawable.ic_play,
                    mViewModel.getPosition(),
                    mViewModel.getMusics().size(),
                    mViewModel.getMusic());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMusicPlay() {
            mViewModel.checkPlayPauseMusic();
        CreateNotification.createNotification(getContext(),
                R.drawable.ic_play,
                mViewModel.getPosition(),
                mViewModel.getMusics().size(),
                mViewModel.getMusic());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMusicPause() {
            mViewModel.checkPlayPauseMusic();
        CreateNotification.createNotification(getContext(),
                R.drawable.ic_play,
                mViewModel.getPosition(),
                mViewModel.getMusics().size(),
                mViewModel.getMusic());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onMusicNext() {
            mViewModel.playNextMusic();
        CreateNotification.createNotification(getContext(),
                R.drawable.ic_play,
                mViewModel.getPosition(),
                mViewModel.getMusics().size(),
                mViewModel.getMusic());
    }

    public interface SongsFragmentCallbacks {
        void startBottomSheetFragment();
    }
}