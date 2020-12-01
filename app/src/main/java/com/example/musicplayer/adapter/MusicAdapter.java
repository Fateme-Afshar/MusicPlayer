package com.example.musicplayer.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ItemMusicBinding;
import com.example.musicplayer.messageLoop.MusicLoader;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.storage.SharePref;
import com.example.musicplayer.viewModel.MusicPlayerViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> implements Filterable {
    private final Map<Integer, Music> mMusicList = new HashMap<>();

    private Map<Integer, Music> mSearchResults;

    private Context mContext;

    private MusicAdapterCallback mCallback;

    private ItemMusicBinding mBinding;

    private MusicPlayerViewModel mViewModel;

    private MusicLoader<Holder> mMusicLoader;

    public void setCallback(MusicAdapterCallback callback) {
        mCallback = callback;
    }

    public Map<Integer, Music> getMusics() {
        return mMusicList;
    }

    public void setMusicNameList(Map<Integer, Music> musicList) {
        mMusicList.putAll(musicList);
        mSearchResults = new HashMap<>(musicList);
    }

    public MusicAdapter(Context context, MusicPlayerViewModel viewModel) {
        mContext = context.getApplicationContext();
        mViewModel = viewModel;
    }


    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(
                LayoutInflater.from(mContext),
                R.layout.item_music,
                parent,
                false);
        mBinding.setViewModel(mViewModel);
        return new Holder(mBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mMusicList.get(position));
        Music music=mMusicList.get(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.sendMusicInfo(music, position);
                mViewModel.setMusic(music);
                mViewModel.setPosition(position);
                SharePref.setLastMusicPath(mContext, music.getPath());
                mViewModel.checkPlayPauseMusic();
                }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

  public   class Holder extends RecyclerView.ViewHolder {
        private ItemMusicBinding mBinding;
        private Music mMusic;

        public Holder(@NonNull ItemMusicBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Music music) {
            mMusic=music;
            mViewModel.setCoverImg(music.getAlbumId(),mBinding.imgCover);
            //TODO: I think this way to set duration is heavy , so what is true way?
            music.setDuration(music.getPath());
            mBinding.setMusic(mMusic);
            mViewModel.getMusicLoader().createMediaPlayer(mMusic.getPath());
        }
    }
    public interface MusicAdapterCallback {
        void sendMusicInfo(Music music, int position);
    }

    private Filter mFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            Map<Integer, Music> filterList = new HashMap<>();

            if (constraint==null || constraint.length()==0)
                filterList.putAll(mSearchResults);
            else {
                String pattern = constraint.toString().toLowerCase().trim();

                for (int i = 0; i < mMusicList.size(); i++) {
                    Music music = mMusicList.get(i);
                    if (music.getName().contains(pattern) ||
                            music.getSingerName().contains(pattern))
                        filterList.put(i, music);
                }
            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=filterList;
            return filterResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mMusicList.clear();
            mMusicList.putAll((Map<? extends Integer, ? extends Music>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
