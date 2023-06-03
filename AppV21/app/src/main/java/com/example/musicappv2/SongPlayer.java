package com.example.musicappv2;


import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import android.graphics.Matrix;
import android.widget.ImageView;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

public class SongPlayer extends AppCompatActivity {
    public static final String ACTION_STOP = "com.example.musicappv2.ACTION_STOP";

    //private com.google.android.material.floatingactionbutton.FloatingActionButton play, pause, stop;


    private ModelRecycleView adapter;

    private ImageButton pause, stop, previous, next, shuffle;
    private ImageView musicLogo;
    private static MediaPlayer mediaPlayer;

    private TextView songInfo, currentDuration, totalDuration;
    private SharedPreferences sp;

    private String mp3URL;
    private String name;
    private boolean canPlayNextSong, canPlayPrevSong;


    private BroadcastReceiver stopReceiver;
    private SeekBar seekBar;

    private boolean isResume;

    Runnable runnable;
    Handler handler;

    private int nextSongIndex;

    public static ArrayList<MediaPlayer> arrayList=new ArrayList<>();
    public static ArrayList<String> trackNames = new ArrayList<>();
    private int currentPosition; // Index of the current song in the track list

    private String nextSongUriString;
    private String nextSongName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        songInfo = findViewById(R.id.songInfo);
        //play = findViewById(R.id.playButton);
        pause = findViewById(R.id.pauseButton);
        stop = findViewById(R.id.stopButtonId);
        previous = findViewById(R.id.previousButton);
        next = findViewById(R.id.nextButton);
        shuffle = findViewById(R.id.shuffleButton);
        currentDuration = findViewById(R.id.currentDuration);
        totalDuration = findViewById(R.id.totalDuration);

        musicLogo = findViewById(R.id.musicLogo);

        seekBar = findViewById(R.id.seekBarId);
        handler = new Handler();

        songInfo.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        songInfo.setSelected(true);

        setPlayer();

        //Rotation property of the musicLogo
        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(musicLogo, View.ROTATION, 0f, 360f);
        rotationAnimator.setDuration(2000); // Duration of rotation
        rotationAnimator.setRepeatCount(ObjectAnimator.INFINITE); // Set to repeat infinitely
        rotationAnimator.setInterpolator(new LinearInterpolator());

// Start the rotation animation
        rotationAnimator.start();


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

        if(sp!=null) {
            name = sp.getString("name", "");
            currentPosition = sp.getInt("currentPosition", -1);
            nextSongIndex = currentPosition;
        }
        else {
            //Error Handling
        }

        songInfo.setText(name);

        pause.setOnClickListener(v -> {
            if(isResume) {
                isResume = false;

                if(mediaPlayer == null)
                {
                    Toast.makeText(this, "Media player is null", Toast.LENGTH_SHORT).show();
                }else {
                    mediaPlayer.start();
                    updateSeekbar();
                }

                pause.setImageDrawable(getResources().getDrawable((
                        R.drawable.baseline_pause_24
                )));

                // Resume the rotation animation
                rotationAnimator.resume();
            }
            else {
                isResume = true;

                if (mediaPlayer == null) {
                    Toast.makeText(this, "Media player is null", Toast.LENGTH_SHORT).show();
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                }

                pause.setImageDrawable(getResources().getDrawable(
                        R.drawable.baseline_play_arrow_1
                ));

                // Pause the rotation animation
                rotationAnimator.pause();
            }
        });


        //fix updateseekbar position
        next.setOnClickListener( view -> {
            if((nextSongIndex < arrayList.size() - 1)) {

                if(mediaPlayer != null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();

                nextSongIndex++;
                MediaPlayer nextSong = arrayList.get(nextSongIndex);
                name = trackNames.get(nextSongIndex);

                ModelRecycleView.nowPlaying = nextSong;
                //mediaPlayer = nextSong;
                mediaPlayer = ModelRecycleView.nowPlaying;


                //fix updateseekbar position
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        songInfo.setText(name);
                        updateSeekbar();
                    }
                });

                mediaPlayer.prepareAsync();
            } else {
                Toast.makeText(this, "End of Playlist Reached", Toast.LENGTH_SHORT).show();
            }
        });


        previous.setOnClickListener(view -> {

            if (nextSongIndex >= 0 || arrayList.size() == 1) {

                if(mediaPlayer != null && mediaPlayer.isPlaying())
                    mediaPlayer.stop();

                if(nextSongIndex != 0)
                    nextSongIndex--;

                MediaPlayer prevSong = arrayList.get(nextSongIndex);
                name = trackNames.get(nextSongIndex);


                ModelRecycleView.nowPlaying = prevSong;
                mediaPlayer = ModelRecycleView.nowPlaying;

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        songInfo.setText(name);
                        updateSeekbar();
                    }
                });

                mediaPlayer.prepareAsync();
            }
            else {
                Toast.makeText(this, "Beginning of Playlist Reached", Toast.LENGTH_SHORT).show();
            }
        });


        stop.setOnClickListener(new View.OnClickListener() {
            boolean isHighlighted = false;

            @Override
            public void onClick(View v) {
                if (isHighlighted) {
                    stop.setBackgroundResource(android.R.color.transparent);
                    isHighlighted = false;
                    mediaPlayer.setLooping(false);
                    //Else play the next song automatically?
                } else {
                    stop.setBackgroundResource(R.drawable.highlight_background);
                    isHighlighted = true;
                    mediaPlayer.setLooping(true);
                }

                // Your existing code here
//                mediaPlayer.seekTo(0);
//                seekBar.setProgress(0);
            }
        });

        shuffle.setOnClickListener(v -> {
            // Call shufflePlaylist() to shuffle the playlist and get a random song
            shufflePlaylist();

            if(mediaPlayer != null && mediaPlayer.isPlaying())
                mediaPlayer.stop();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                    updateSeekbar();
                }
            });

            mediaPlayer.prepareAsync();
        });
    }

    private void shufflePlaylist() {
        int playlistSize = arrayList.size();

        // Generate a random index within the range of the playlist
        int randomIndex = (int) (Math.random() * playlistSize);

        // Get the MediaPlayer and track name at the random index
        MediaPlayer randomSong = arrayList.get(randomIndex);
        String randomTrackName = trackNames.get(randomIndex);

        // Update the nextSongIndex to the random index
        nextSongIndex = randomIndex;

        // Set the nowPlaying MediaPlayer to the random song
        ModelRecycleView.nowPlaying = randomSong;
        mediaPlayer = ModelRecycleView.nowPlaying;

        // Update the song info
        songInfo.setText(randomTrackName);
    }
    private void setPlayer()
    {
        mediaPlayer = ModelRecycleView.nowPlaying;
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setSecondaryProgress(mediaPlayer.getDuration());
        updateSeekbar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DisplaySongs.songPlay.setText(name);
    }

    public void updateSeekbar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currPos = mediaPlayer.getCurrentPosition();
            int totalTime = mediaPlayer.getDuration();

            // Calculate the progress ratio
            int progress = (int) (((float) currPos / totalTime) * seekBar.getMax());

            // Update the SeekBar progress
            seekBar.setProgress(progress);

            // Schedule the next update
            //handler.postDelayed(this::updateSeekbar, 100);

            // Update the song info text
            int seconds = currPos / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;

            //Total Duration
            int secondsTotal = totalTime / 1000;
            int minutesTotal = secondsTotal / 60;
            secondsTotal = secondsTotal % 60;


            String currentTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
            String totalTimeString = String.format(Locale.getDefault(), "%02d:%02d", minutesTotal, secondsTotal);

            currentDuration.setText(currentTime);
            totalDuration.setText(totalTimeString);


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


    public static void stopMusicPlayer() {
        if (ModelRecycleView.nowPlaying != null && ModelRecycleView.nowPlaying.isPlaying()) {
            ModelRecycleView.nowPlaying.stop();
            ModelRecycleView.nowPlaying.release();
            ModelRecycleView.nowPlaying = null;
        }
    }


}