package com.example.musicappv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import com.example.musicappv2.ModelRecycleView.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class ModelRecycleView extends RecyclerView.Adapter<ModelRecycleView.ViewHolder> {

    ArrayList<MediaPlayer> arrayList;

    ArrayList<String> trackNames;
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public static MediaPlayer nowPlaying;

    public static SeekBar seekBar;

    public static boolean preparing;

    String mp3URL;

    public ModelRecycleView(Context context ,ArrayList<MediaPlayer> arrayList, ArrayList<String> trackNames)
    {
        this.arrayList=arrayList;
        this.context=context;
        this.trackNames = trackNames;
        preparing = false;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_view,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        sp = context.getSharedPreferences("songPref", Context.MODE_PRIVATE);
        editor = sp.edit();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(trackNames.get(position));
        holder.cardView.setOnClickListener(v ->  {
            if(!preparing) {
                if (nowPlaying != null) {
                    nowPlaying.pause();
                    nowPlaying.seekTo(0);
                    nowPlaying = null;
                }
                MediaPlayer mediaPlayer = arrayList.get(position);
                editor.putString("name", trackNames.get(position));
                editor.commit();
                DisplaySongs.songPlay.setText(trackNames.get(position));
                DisplaySongs.buttonMain.setImageResource(R.drawable.ic_pause);
                DisplaySongs.smallPlayer.setVisibility(View.VISIBLE);
                DisplaySongs.paused = false;
                nowPlaying = mediaPlayer;
                nowPlaying.prepareAsync();
                preparing = true;
                nowPlaying.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        preparing = false;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            textView=itemView.findViewById(R.id.textView);
            cardView=itemView.findViewById(R.id.cardview);
        }
    }
}
