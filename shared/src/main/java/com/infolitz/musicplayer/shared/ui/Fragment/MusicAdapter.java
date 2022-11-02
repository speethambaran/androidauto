package com.infolitz.musicplayer.shared.ui.Fragment;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.infolitz.musicplayer.shared.R;
import com.infolitz.musicplayer.shared.model.MusicModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {
    View view;
    Context mContext;
    List<MusicModel> musicModelList;
    ExoPlayer player;


    public MusicAdapter(Context mContext, List<MusicModel> musicModelList, ExoPlayer player) {
        this.mContext = mContext;
        this.musicModelList = musicModelList;
        this.player = player;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.single_musiclistlayout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (holder instanceof ViewHolder) {
            MusicModel musicModel = musicModelList.get(position);

            ((ViewHolder) holder).titleholder.setText(musicModel.getTitle());
            ((ViewHolder) holder).durationHolder.setText("" + getDuration(musicModel.getDuration()));
//            Log.d("AdaDura", "duration " + getDuration(musicModel.getDuration()));
            ((ViewHolder) holder).sizeHolder.setText("" + getSize(musicModel.getSize()));
//
            Uri artWorkUri = musicModel.getArtworkUri();
            if (artWorkUri != null) {
                ((ViewHolder) holder).artWorkHolder.setImageURI(artWorkUri);
            }
            if (((ViewHolder) holder).artWorkHolder.getDrawable() == null) {
                ((ViewHolder) holder).artWorkHolder.setImageResource(R.drawable.ic_baseline_photo_24);
            }

  /*          ((ViewHolder) holder).itemView.setOnClickListener(view -> {
                if (!player.isPlaying()) {
                    player.setMediaItems(getMediaItems(), position, 0);
                } else {
                    player.pause();
                    player.seekTo(position, 0);
                }
                player.prepare();
                player.play();
                Toast.makeText(mContext, musicModel.getTitle(), Toast.LENGTH_SHORT).show();

            });
            ((ViewHolder) holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    player.seekToNext();
                    return true;
                }
            });*/
        }

    }

    private List<MediaItem> getMediaItems() {
        List<MediaItem> mediaItems = new ArrayList<>();
        for (MusicModel musicModel : musicModelList) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(musicModel.getUri())
                    .setMediaMetadata(getMetaData(musicModel))
                    .build();
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetaData(MusicModel musicModel) {
        return new MediaMetadata.Builder()
                .setTitle(musicModel.getTitle())
                .setArtworkUri(musicModel.getArtworkUri())
                .build();
    }

    @Override
    public int getItemCount() {
        return musicModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView artWorkHolder;
        TextView titleholder, durationHolder, sizeHolder;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            artWorkHolder = itemView.findViewById(R.id.artWorkView);
            titleholder = itemView.findViewById(R.id.titleView);
            durationHolder = itemView.findViewById(R.id.durationView);
            sizeHolder = itemView.findViewById(R.id.sizeView);
        }
    }



    private String getDuration(long duration) {
        String totalDuration;
        long hrs = duration / (1000 * 60 * 60);
        long min = (duration % (1000 * 60 * 60)) / (1000 * 60);
        long sec = (((duration % (1000 * 60 * 60)) % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        if (hrs < 1) {
            totalDuration = String.format("%02d:%02d", min, sec);
        } else {
            totalDuration = String.format("%1d:%02d:%02d", hrs, min, sec);
        }
        return totalDuration;
    }

    private String getSize(long bytes) {
        String totalSize;
        double k = bytes / 1024.0;
        double m = ((bytes / 1024.0) / 1024.0);
        double g = (((bytes / 1024.0) / 1024.0) / 1024.0);
        double t = ((((bytes / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            totalSize = dec.format(t).concat(" TB");
        } else if (g > 1) {
            totalSize = dec.format(g).concat(" GB");
        } else if (m > 1) {
            totalSize = dec.format(m).concat(" MB");
        } else if (k > 1) {
            totalSize = dec.format(k).concat(" KB");
        } else {
            totalSize = dec.format(g).concat(" Bytes");
        }
        return totalSize;
    }

}
