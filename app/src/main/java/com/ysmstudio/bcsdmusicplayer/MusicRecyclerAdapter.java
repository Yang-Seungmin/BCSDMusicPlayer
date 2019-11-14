package com.ysmstudio.bcsdmusicplayer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicRecyclerAdapter extends RecyclerView.Adapter<MusicRecyclerAdapter.ViewHolder> {

    private ArrayList<MusicItem> musicItemArrayList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public MusicRecyclerAdapter(ArrayList<MusicItem> musicItemArrayList) {
        this.musicItemArrayList = musicItemArrayList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MusicRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_data, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicRecyclerAdapter.ViewHolder holder, final int position) {
        holder.textViewMusicTitle.setText(musicItemArrayList.get(position).getMusicTitle());
        holder.textViewMusicArtist.setText(musicItemArrayList.get(position).getMusicArtist());
        holder.textViewMusicDuration.setText(musicItemArrayList.get(position).getMusicDuration());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null) onItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewMusicTitle, textViewMusicArtist, textViewMusicDuration;
        public View container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewMusicTitle = itemView.findViewById(R.id.item_text_view_title);
            textViewMusicArtist = itemView.findViewById(R.id.item_text_view_artist);
            textViewMusicDuration = itemView.findViewById(R.id.item_text_view_length);

            container = itemView.findViewById(R.id.item_container);
        }
    }
}
