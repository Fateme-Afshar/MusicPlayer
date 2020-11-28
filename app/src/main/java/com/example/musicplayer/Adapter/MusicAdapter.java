package com.example.musicplayer.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.example.musicplayer.model.Music;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ExtractFromPath;
import com.example.musicplayer.databinding.ItemMusicBinding;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> implements Filterable {
    private final List<Music> mMusicList = new ArrayList<>();
    private final List<Music> mSearchMusicList = new ArrayList<>();

    private Context mContext;

    private MusicAdapterCallback mCallback;

    private ItemMusicBinding mBinding;

    public void setCallback(MusicAdapterCallback callback) {
        mCallback = callback;
    }

    public List<Music> getMusicNameList() {
        return mMusicList;
    }

    public void setMusicNameList(List<Music> musicList) {
        mMusicList.addAll(musicList);
    }

    public MusicAdapter(Context context) {
        mContext = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_music, parent, false);

        return new Holder(mBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(mMusicList.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             mCallback.sendMusicInfo(mMusicList.get(position));

             mCallback.playMusic(mMusicList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    private Filter mFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Music> filterList=new ArrayList<>();

            if (constraint==null)
                mSearchMusicList.addAll(mMusicList);
            else {
                String pattern=constraint.toString().toLowerCase().trim();

                for (Music music : mMusicList) {
                    if (music.getName().contains(pattern))
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

    class Holder extends RecyclerView.ViewHolder {
        private Music mMusic = new Music();

        private ItemMusicBinding mBinding;

        public Holder(@NonNull ItemMusicBinding binding) {
            super(binding.getRoot());

            mBinding = binding;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Music music) {
            mMusic = music;

            music.setDuration(ExtractFromPath.getMusicDuration(music.getPath())+"");
            mBinding.setMusic(music);



                //------ scaling bitmap -----
               Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");

                Uri uri = ContentUris.withAppendedId(sArtworkUri,
                        mMusic.getAlbumId());
                if (uri!=null)
                    Glide.with(mContext).
                            load(uri).
                            centerCrop().
                            placeholder(R.drawable.ic_null_cover_img)
                            .into(mBinding.imgCover);

        }

        private String getNormalText(String text) {
            if (text.length() >= 20) {
                String singerName = text.substring(0, 19);
                return singerName + "...";
            }
            return text;
        }

        private String extractMusicDurationToTimeFormat() {
            int second = (Integer.parseInt(mMusic.getDuration())/1000);
            int minute = second / 60;
            int hour = minute / 60;

            if (hour != 0)
                return hour + " : " + minute + " : " + second%60;
            return minute + " : " + second%60;
        }
    }

    public interface MusicAdapterCallback {
        void sendMusicInfo(Music music);
        void playMusic(Music music);
    }
}
