package com.example.musicplayer.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.MessageLoop.MusicLoader;
import com.example.musicplayer.Storage.SharePref;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.R;
import com.example.musicplayer.databinding.ItemMusicBinding;
import com.example.musicplayer.viewModel.MusicPlayerViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> implements Filterable {
    private final List<Music> mMusicList = new ArrayList<>();

    private  List<Music> mSearchResults;

    private Context mContext;

    private MusicAdapterCallback mCallback;

    private ItemMusicBinding mBinding;

    private MusicPlayerViewModel mViewModel;

    private MusicLoader<Holder> mMusicLoader;

    public void setCallback(MusicAdapterCallback callback) {
        mCallback = callback;
    }

    public List<Music> getMusicNameList() {
        return mMusicList;
    }

    public void setMusicNameList(List<Music> musicList) {
        mMusicList.addAll(musicList);
        mSearchResults=new ArrayList<>(musicList);
    }

    public MusicAdapter(Context context, MusicPlayerViewModel viewModel, MusicLoader<Holder> musicLoader) {
        mContext = context.getApplicationContext();
        mViewModel=viewModel;
        mMusicLoader=musicLoader;
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
                mCallback.sendMusicInfo(music);
                mViewModel.setMusic(music);
                SharePref.setLastMusic(mContext,music.getPath());
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
            mMusicLoader.createMessage(this,mMusic.getPath());
        }

        public void bindMediaPlayer(MediaPlayer mediaPlayer){
              mMusic.setMediaPlayer(mediaPlayer);
        }
    }
    public interface MusicAdapterCallback {
        void sendMusicInfo(Music music);
    }

    private Filter mFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Music> filterList=new ArrayList<>();

            if (constraint==null || constraint.length()==0)
                filterList.addAll(mSearchResults);
            else {
                String pattern=constraint.toString().toLowerCase().trim();

                for (Music music : mSearchResults) {
                    if (music.getName().contains(pattern) ||
                            music.getSingerName().contains(pattern))
                        filterList.add(music);
                }
            }

            FilterResults filterResults=new FilterResults();
            filterResults.values=filterList;
            return filterResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mMusicList.clear();
            mMusicList.addAll((List<Music>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return mFilter;
    }
}
