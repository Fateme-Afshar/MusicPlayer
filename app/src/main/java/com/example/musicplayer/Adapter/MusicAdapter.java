package com.example.musicplayer.Adapter;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.Model.Music;
import com.example.musicplayer.R;
import com.example.musicplayer.Utils.ExtractFromPath;
import com.example.musicplayer.databinding.ItemMusicBinding;

import java.io.IOException;
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
                /*if (!isStarted){
                    mMediaPlayer.start();
                    isStarted=true;
                }else
                    mMediaPlayer.stop();
*/
                mCallback.sendMusicInfo(mMusicList.get(position).getPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private Music mMusic = new Music();
        private MediaPlayer mMediaPlayer;

        private ItemMusicBinding mBinding;

        public Holder(@NonNull ItemMusicBinding binding) {
            super(binding.getRoot());

            mBinding = binding;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Music music) {
            mMusic = music;
          createMediaPlayer(mMusic.getPath());
            mBinding.musicName.setText(mMusic.getName());
            if (mMusic.getSingerName().length() < 20)
                mBinding.singerName.setText(mMusic.getSingerName());
            else if (mMusic.getSingerName().length() >=20) {
                String singerName = mMusic.getSingerName().substring(0, 19);

                mBinding.singerName.setText(singerName+"...");
            }

            try {
                mBinding.imgCover.setImageBitmap(ExtractFromPath.getImgCoverSong(mMusic.getPath()));
            } catch (Exception e) {
                mBinding.imgCover.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.ic_null_cover_img));
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        private void createMediaPlayer(String path) {
            mMediaPlayer=new MediaPlayer();
            try {
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(mMusic.getPath());
                // convert duration to minute:seconds
                String duration =
                        metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                mBinding.songTime.setText(duration);
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepare();
                mMediaPlayer.setVolume(0.5f, 0.5f);
                mMediaPlayer.setLooping(false);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface MusicAdapterCallback {
        boolean sendMusicInfo(String path);
    }
}
