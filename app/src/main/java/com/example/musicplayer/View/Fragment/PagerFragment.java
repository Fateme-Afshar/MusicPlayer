package com.example.musicplayer.View.Fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicplayer.Adapter.MusicPagerAdapter;
import com.example.musicplayer.R;
import com.example.musicplayer.ViewModel.MusicPlayerViewModel;
import com.example.musicplayer.databinding.FragmentPagerBinding;
import com.google.android.material.tabs.TabLayoutMediator;


public class PagerFragment extends Fragment {
    private FragmentPagerBinding mBinding;
    private String[] mPageName=new String[]{"Songs","Singers","Albums"};

    private MusicPlayerViewModel mViewModel;
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

        mViewModel=new ViewModelProvider(this).
                get(MusicPlayerViewModel.class);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mViewModel.arePermissionAgree()){
                ((ActivityManager) (getActivity().getSystemService(Context.ACTIVITY_SERVICE))).clearApplicationUserData();
                getActivity().recreate();
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                onResume();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mViewModel.arePermissionAgree()){
            requestPermissions(mViewModel.PERMISSIONS,mViewModel.REQUEST_PERMISSION_EXTERNAL);
            return;
        }
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