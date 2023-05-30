package com.example.musicappv2;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SongPlayer extends AppCompatActivity {

    private com.google.android.material.floatingactionbutton.FloatingActionButton play, pause, stop;
    private MediaPlayer mediaPlayer;

    private TextView songInfo;

    private SharedPreferences sp;
    private String mp3URL;
    private String name;

    private SeekBar seekBar;

    Runnable runnable;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        songInfo = findViewById(R.id.songInfo);
        play = findViewById(R.id.playButton);
        pause = findViewById(R.id.pauseButtonId);
        stop = findViewById(R.id.stopButtonId);

        seekBar = findViewById(R.id.seekBarId);
        handler = new Handler();

        setPlayer();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sp = getApplicationContext().getSharedPreferences("songPref", Context.MODE_PRIVATE);

        if(sp!=null){
            name = sp.getString("name", "");
        }
        else
            name = "no track is playing";

        songInfo.setText(name);

        play.setOnClickListener(v -> {
            if(mediaPlayer == null)
            {
                Toast.makeText(this, "Media player is null", Toast.LENGTH_SHORT).show();
            }else {
                mediaPlayer.start();
                updateSeekbar();
            }
        });

        pause.setOnClickListener(v -> {
            if (mediaPlayer == null) {
                Toast.makeText(this, "Media player is null", Toast.LENGTH_SHORT).show();
            } else {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
        });

        stop.setOnClickListener(v -> {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            seekBar.setProgress(0);
        });
    }
    private void setPlayer()
    {
        mediaPlayer = ModelRecycleView.nowPlaying;
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setSecondaryProgress(mediaPlayer.getDuration());
        updateSeekbar();

    }



    public void updateSeekbar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currPos = mediaPlayer.getCurrentPosition();
            seekBar.setProgress(currPos);

            runnable = new Runnable() {
                @Override
                public void run() {
                    updateSeekbar();
                }
            };
            handler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateSeekbar();
    }
}