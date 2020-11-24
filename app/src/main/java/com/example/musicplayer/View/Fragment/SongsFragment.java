package com.example.musicplayer.View.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicplayer.Adapter.MusicAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.ViewModel.MusicPlayerViewModel;
import com.example.musicplayer.databinding.RecyclerViewBinding;

public class SongsFragment extends Fragment {
    private MusicAdapter mAdapter;
    private RecyclerViewBinding mBinding;

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
        mViewModel=new ViewModelProvider(this).get(MusicPlayerViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding= DataBindingUtil.inflate(inflater,
                R.layout.recycler_view,
                container,
                false);
        setupAdapter();
        return mBinding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onResume() {
        super.onResume();
            if (mAdapter.getMusicNameList().size()==0)
                mAdapter.setMusicNameList(mViewModel.getMusics());
    }

    private void setupAdapter(){
        mAdapter=new MusicAdapter(getContext());
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.recyclerView.setAdapter(mAdapter);
    }
}