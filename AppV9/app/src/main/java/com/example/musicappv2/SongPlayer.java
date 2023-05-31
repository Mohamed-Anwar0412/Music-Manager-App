package com.example.musicappv2;


import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class SongPlayer extends AppCompatActivity {
    public static final String ACTION_STOP = "com.example.musicappv2.ACTION_STOP";

    //private com.google.android.material.floatingactionbutton.FloatingActionButton play, pause, stop;

    private ImageButton pause, stop, previous, next, shuffle;
    private MediaPlayer mediaPlayer;

    private TextView songInfo;
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

        if(sp!=null) {
            name = sp.getString("name", "");
            //canPlayNextSong = sp.getBoolean("playNext", false);
            //canPlayPrevSong = sp.getBoolean("playPrev", false);
            currentPosition = sp.getInt("currentPosition", -1);
            //nextSongName = sp.getString("nextName", "");
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
            }
        });


        /////           Neeeeds FIXING          /////
        //ERROR HANDLING (PRESSING BACK)
        next.setOnClickListener( view -> {
            if((nextSongIndex < arrayList.size() - 1)) {

                nextSongIndex++;
                MediaPlayer nextSong = arrayList.get(nextSongIndex);
                String nextSongNameLocal = trackNames.get(nextSongIndex);

                // Stop and release the current media player
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = nextSong;
                //mediaPlayer.prepareAsync();

                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                songInfo.setText(nextSongNameLocal);
                mediaPlayer.start();
                updateSeekbar();

            } else {
                Toast.makeText(this, "End of Playlist Reached", Toast.LENGTH_SHORT).show();
            }
        });


        /////           Neeeeds FIXING         /////
        ////            Crashes when pressing next then prev (fast i think)
        previous.setOnClickListener(view -> {

            if (nextSongIndex >= 0) {
                if(nextSongIndex != 0)
                    nextSongIndex--;
                MediaPlayer prevSong = arrayList.get(nextSongIndex);
                String prevSongNameLocal = trackNames.get(nextSongIndex);

                try {
                    //mediaPlayer.release();
                    if(!mediaPlayer.equals(prevSong)) {
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                        mediaPlayer = prevSong;
                        mediaPlayer.prepare();
                        songInfo.setText(prevSongNameLocal);
                        updateSeekbar();
                    } else {
                        mediaPlayer.seekTo(0);
                        seekBar.setProgress(0);
                    }
                    mediaPlayer.start();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to play previous song", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Beginning of Playlist Reached", Toast.LENGTH_SHORT).show();
            }
        });



//
//
//        play.setOnClickListener(v -> {
//            if(mediaPlayer == null)
//            {
//                Toast.makeText(this, "Media player is null", Toast.LENGTH_SHORT).show();
//            }else {
//                mediaPlayer.start();
//                updateSeekbar();
//            }
//        });
//
//        pause.setOnClickListener(v -> {
//            if (mediaPlayer == null) {
//                Toast.makeText(this, "Media player is null", Toast.LENGTH_SHORT).show();
//            } else {
//                if (mediaPlayer.isPlaying()) {
//                    mediaPlayer.pause();
//                }
//            }
//        });

        stop.setOnClickListener(v -> {
            //mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            seekBar.setProgress(0);
        });
    }

    //Testing these 2 / Might help in handling crahses

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (mediaPlayer == null) {
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        // Release the MediaPlayer instance
//        if (mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//    }



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
        unregisterStopReceiver();
    }

    private void registerStopReceiver() {
        if (stopReceiver == null) {
            stopReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(ACTION_STOP)) {
                        stopMusicPlayer();
                    }
                }
            };
            registerReceiver(stopReceiver, new IntentFilter(ACTION_STOP));
        }
    }
    private void unregisterStopReceiver() {
        if (stopReceiver != null) {
            unregisterReceiver(stopReceiver);
            stopReceiver = null;
        }
    }
    public static void stopMusicPlayer() {
        if (ModelRecycleView.nowPlaying != null && ModelRecycleView.nowPlaying.isPlaying()) {
            ModelRecycleView.nowPlaying.stop();
            ModelRecycleView.nowPlaying.release();
            ModelRecycleView.nowPlaying = null;
        }
    }

}