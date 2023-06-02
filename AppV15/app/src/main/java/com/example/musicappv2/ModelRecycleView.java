package com.example.musicappv2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class ModelRecycleView extends RecyclerView.Adapter<ModelRecycleView.ViewHolder> {

    public static ArrayList<MediaPlayer> arrayList;

    public static ArrayList<String> trackNames;
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public static MediaPlayer nowPlaying;

    public static SeekBar seekBar;

    ImageButton delecticon;

    public static boolean preparing;

    String mp3URL;

    private int currentSongIndex = -1;

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

        View view = LayoutInflater.from(context).inflate(R.layout.card_view_update,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        sp = context.getSharedPreferences("songPref", Context.MODE_PRIVATE);
        editor = sp.edit();
        return viewHolder;
    }

    public int getCurrentSongPosition() {
        if (nowPlaying != null && arrayList.contains(nowPlaying)) {
            return arrayList.indexOf(nowPlaying);
        }
        return -1; // Return -1 if no song is currently playing or not found in the list
    }




    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.textView.setText(trackNames.get(position));
        holder.cardView.setOnClickListener(v -> {
            if (!preparing)

            {
                if (nowPlaying != null)
                {
                    nowPlaying.pause();
                    nowPlaying.seekTo(0);
                    nowPlaying = null;
                }

                copyArrayListToSongPlayer();

                MediaPlayer mediaPlayer = arrayList.get(position);

                editor.putString("name", trackNames.get(position));
                editor.putInt("currentPosition", position);
                editor.apply();

                DisplaySongs.songPlay.setText(trackNames.get(position));
                DisplaySongs.buttonMain.setImageResource(R.drawable.ic_pause);
                DisplaySongs.smallPlayer.setVisibility(View.VISIBLE);
                DisplaySongs.paused = false;

                nowPlaying = mediaPlayer;


                nowPlaying.prepareAsync();
                preparing = true;
//                nowPlaying.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                    @Override
//                    public void onPrepared(MediaPlayer mp) {
//                        mediaPlayer.start();
//                        preparing = false;
//                    }
//                });


                //prepareAsync is the main problem:??
                mediaPlayer.reset();

                nowPlaying.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        nowPlaying.start();
                        preparing = false;
                        //mediaPlayer.start();
                    }
                });


//                try {
//                    nowPlaying.prepareAsync();
//                    preparing = true;
//                } catch (IllegalStateException e) {
//                    e.printStackTrace();
//                    // Handle the exception
//                    Toast.makeText(context, "ERROR" + e.toString(), Toast.LENGTH_SHORT).show();
//                }
            }
        });


    }


    public static void copyArrayListToSongPlayer() {
        // Access the arrayList in the SongPlayer class
        ArrayList<MediaPlayer> songPlayerArrayList = SongPlayer.arrayList;
        ArrayList<String> songNamePlayerArrayList = SongPlayer.trackNames;

        // Clear the arrayList in the SongPlayer class
        songPlayerArrayList.clear();
        songNamePlayerArrayList.clear();

        // Add all elements from the arrayList in the OtherClass to the arrayList in the SongPlayer class
        songPlayerArrayList.addAll(arrayList);
        songNamePlayerArrayList.addAll(trackNames);
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
            cardView=itemView.findViewById(R.id.cardViewUpdate);

            //textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            //textView.setSelected(true);

            delecticon = itemView.findViewById(R.id.deletetButton);
            delecticon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    deleteSong(position);
                }
            });
        }
    }




    public void deleteSong(int position) {
        if (arrayList.size() > position) {
            MediaPlayer mediaPlayer = arrayList.get(position);
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            arrayList.remove(position);
            String trackName = trackNames.get(position);
            trackNames.remove(position);
            notifyDataSetChanged();

            // Construct the reference to the song file using the track name
            String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            StorageReference songRef = FirebaseStorage.getInstance().getReference().child("Songs/" + userEmail + "/");
            songRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference fileRef : listResult.getItems()) {
                        if (fileRef.getName().equals(trackName)) {
                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Song file deleted successfully
                                    Toast.makeText(context, "Song file deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to delete song file
                                    Toast.makeText(context, "Failed to delete song file", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        }
                    }
                }
            });

            // Delete the song from the screen
            notifyItemRemoved(position);
        }
    }

}
