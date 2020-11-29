package com.example.musicplayer.View.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.musicplayer.R;
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

        setupBtnPlayPause();

        return mBinding.getRoot();
    }

    private void setupBtnPlayPause() {
        if (mViewModel.getMusic().isPlaying()) {
            mBinding.btnPlay.setVisibility(View.INVISIBLE);
            mBinding.btnPause.setVisibility(View.VISIBLE);
        } else {
            mBinding.btnPlay.setVisibility(View.VISIBLE);
            mBinding.btnPause.setVisibility(View.INVISIBLE);
        }
    }


}