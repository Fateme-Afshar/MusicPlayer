package com.example.musicplayer.Adapter;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.Model.Music;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ExtractFromPath;
import com.example.musicplayer.databinding.ItemMusicBinding;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.Holder> {
    private final List<Music> mMusicList = new ArrayList<>();

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
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
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
            mBinding.musicName.setText(getNormalText(mMusic.getName()));
            mBinding.singerName.setText(getNormalText(mMusic.getSingerName()));

            music.setDuration(ExtractFromPath.getMusicDuration(music.getPath()));
            mBinding.songTime.setText(extractMusicDurationToTimeFormat());


                //------ scaling bitmap -----
               Uri sArtworkUri = Uri
                        .parse("content://media/external/audio/albumart");

                Uri uri = ContentUris.withAppendedId(sArtworkUri,
                        mMusic.getAlbumId());
                if (uri!=null)
                Glide.with(mContext).
                        load(uri).
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
            int second = (mMusic.getDuration()/1000);
            int minute = second / 60;
            int hour = minute / 60;

            if (hour != 0)
                return hour + " : " + minute + " : " + second%60;
            return minute + " : " + second%60;
        }
    }

    public interface MusicAdapterCallback {
        boolean sendMusicInfo(Music music);
    }
}
