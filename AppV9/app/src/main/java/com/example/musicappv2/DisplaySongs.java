package com.example.musicappv2;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class DisplaySongs extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseAuth auth;
    Button logoutbutton ;

    FirebaseUser user;

    public static LinearLayout smallPlayer;
    public static TextView songPlay;
    public static ImageButton buttonMain;

    public static SeekBar seekBar;

    public static boolean paused;

    private MediaPlayer nowPlaying;

    ImageButton detectButton;


    LinearLayout mediaPlayerButton;
    ArrayList<MediaPlayer> arrayList=new ArrayList<>();
    ArrayList<String> trackNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_songs);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                return true;
            }   else if (id == R.id.navigation_search) {
                startActivity(new Intent(getApplicationContext(), Search.class));
                finish();
                return true;
            }   else if (id == R.id.navigation_shazam) {
                startActivity(new Intent(getApplicationContext(), detetct_Activity.class));
                finish();
                return true;
            }

            return false;
        });

        auth = FirebaseAuth.getInstance();
        logoutbutton =findViewById(R.id.logoutButton);
        recyclerView =findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ModelRecycleView adapter = new ModelRecycleView(this, arrayList, trackNames);

        smallPlayer = findViewById(R.id.smallPlayer);
        songPlay = findViewById(R.id.songPlay);
        buttonMain = findViewById(R.id.buttonMain);
        detectButton = findViewById(R.id.detectButton);
        mediaPlayerButton = findViewById(R.id.mediaPlayerButton);
        paused = true;
        checkPlaying();
        user=auth.getCurrentUser();
        //mlhash lazma
        if(user==null)
        {
            Intent intent =new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        mediaPlayerButton.setOnClickListener(v -> {
            Intent intent = new Intent(DisplaySongs.this, SongPlayer.class);
            startActivity(intent);
        });

        detectButton.setOnClickListener(v -> {
            startActivity(new Intent(this, detetct_Activity.class));
        });

        buttonMain.setOnClickListener(v -> {
            nowPlaying = ModelRecycleView.nowPlaying;
            if(paused)
            {
                nowPlaying.start();
                buttonMain.setImageResource(R.drawable.ic_pause);
                paused = false;
            }
            else{
                nowPlaying.pause();
                buttonMain.setImageResource(R.drawable.ic_play);
                paused = true;
            }

        });
        logoutbutton.setOnClickListener(v -> {
            SongPlayer.stopMusicPlayer();
            FirebaseAuth.getInstance().signOut();
            Intent intent =new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Create a Cloud Storage reference from the app
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Songs/" + userEmail + "/");

        storageReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {

                for (StorageReference fileRef : listResult.getItems()) {
                    if(fileRef.getName().equals(".folderMarker")){
                        fileRef.delete();
                        continue;
                    }
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUrl = uri.toString();
                            arrayList.add(setUpMediaPlayer(downloadUrl));
                            trackNames.add(fileRef.getName());
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
        nowPlaying = ModelRecycleView.nowPlaying;
        checkPlaying();
    }


    //Might be causing some crashes
    private MediaPlayer setUpMediaPlayer(String mp3URL){
        MediaPlayer mediaPlayer = new MediaPlayer();

        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        //mediaPlayer.reset();

        try {
            mediaPlayer.setDataSource(mp3URL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return mediaPlayer;
    }
    private void checkPlaying()
    {
        if(nowPlaying!=null && nowPlaying.isPlaying())
        {
            buttonMain.setImageResource(R.drawable.ic_pause);
            paused = false;
        }
        else if(nowPlaying!=null && !nowPlaying.isPlaying())
        {
            buttonMain.setImageResource(R.drawable.ic_play);
            paused = true;
        }
    }

}