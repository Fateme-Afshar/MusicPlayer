package com.example.musicplayer.View.Fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.Adapter.MusicPagerAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.FragmentPagerBinding;
import com.google.android.material.tabs.TabLayoutMediator;


public class PagerFragment extends Fragment {
    private FragmentPagerBinding mBinding;
    private String[] mPageName=new String[]{"Singers","Albums","Songs"};
    public PagerFragment() {
        // Required empty public constructor
    }

    public static PagerFragment newInstance() {
        PagerFragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding= DataBindingUtil.inflate(inflater,
                R.layout.fragment_pager,
                container,
                false);
        setupAdapter();
        setupTabLayout();
        return mBinding.getRoot();
    }

    private void setupAdapter() {
        MusicPagerAdapter viewPagerAdapter =
                new MusicPagerAdapter(getActivity());
        mBinding.viewPager2.setAdapter(viewPagerAdapter);
    }

    private void setupTabLayout() {
        new TabLayoutMediator(mBinding.tabLayout, mBinding.viewPager2,
                (tab, position) -> tab.setText(mPageName[position])
        ).attach();
    }
}