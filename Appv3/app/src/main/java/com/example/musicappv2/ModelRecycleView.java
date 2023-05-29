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

    ArrayList<StorageReference> arrayList;
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public static SeekBar seekBar;
    public static MediaPlayer mediaPlayer;

    String mp3URL;

    public ModelRecycleView(Context context ,ArrayList<StorageReference> arrayList)
    {
        this.arrayList=arrayList;
        this.context=context;

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
        holder.textView.setText(arrayList.get(position).getName());
        holder.cardView.setOnClickListener(v ->  {
                    if(mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    StorageReference fileRef = arrayList.get(position);
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            // Continue with your code here, e.g., starting the intent
                            mp3URL = downloadUrl;
                            editor.putString("mp3url", downloadUrl);
                            editor.putString("name", fileRef.getName());
                            editor.commit();
                            DisplaySongs.songPlay.setText(fileRef.getName());
                            DisplaySongs.buttonMain.setImageResource(R.drawable.ic_pause);
                            DisplaySongs.smallPlayer.setVisibility(View.VISIBLE);
                            DisplaySongs.paused = false;
                            setPlayer();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure case, such as showing an error message
                            e.printStackTrace();
                        }
                    });
                }
        );
    }

    private void setPlayer()
    {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(mp3URL);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
