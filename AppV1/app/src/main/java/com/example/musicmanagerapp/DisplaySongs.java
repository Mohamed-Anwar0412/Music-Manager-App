package com.example.musicmanagerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class DisplaySongs extends AppCompatActivity {

    RecyclerView recyclerView;


    public static LinearLayout smallPlayer;
    public static TextView songPlay;
    public static ImageButton buttonMain;

    public static SeekBar seekBar;

    public static boolean paused;


    static MediaPlayer mediaPlayer;


    LinearLayout mediaPlayerButton;
    ArrayList<StorageReference> arrayList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_songs);
        recyclerView =findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ModelRecycleView adapter = new ModelRecycleView(this, arrayList);
        smallPlayer = findViewById(R.id.smallPlayer);
        songPlay = findViewById(R.id.songPlay);
        buttonMain = findViewById(R.id.buttonMain);
        mediaPlayerButton = findViewById(R.id.mediaPlayerButton);
        paused = true;
        mediaPlayer = ModelRecycleView.mediaPlayer;


        mediaPlayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplaySongs.this, SongPlayer.class);
            startActivity(intent);
        });

        buttonMain.setOnClickListener(v -> {
            mediaPlayer = ModelRecycleView.mediaPlayer;
            if(paused)
            {
                mediaPlayer.start();
                buttonMain.setImageResource(R.drawable.ic_pause);
                paused = false;
            }
            else{
                mediaPlayer.pause();
                buttonMain.setImageResource(R.drawable.ic_play);
                paused = true;
            }

        });



        // Create a Cloud Storage reference from the app
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Songs");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference fileRef : listResult.getItems()) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            arrayList.add(fileRef);
                            Log.d("item", uri.toString());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = ModelRecycleView.mediaPlayer;
    }
}