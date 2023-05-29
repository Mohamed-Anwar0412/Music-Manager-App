package com.example.animatingbutton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skyfishjy.library.RippleBackground;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

public class MainActivity extends AppCompatActivity {


    private TextView artistTextView;
    private TextView songTextView;

    private String artist;
    private String song;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RippleBackground rippleBackground = (RippleBackground)findViewById(R.id.content);
        final ImageView imageView = (ImageView)findViewById(R.id.centerImage);
        final Handler handler = new Handler();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!rippleBackground.isRippleAnimationRunning()) {
                    imageView.setColorFilter(Color.argb(255, 255, 255, 255));
                    rippleBackground.startRippleAnimation();

                    // Set the duration of the animation in milliseconds
                    int animationDuration = 9000; // Example: 9 seconds

                    // Schedule a task to stop the animation after the specified duration
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Sequential? Ignore this note
                            imageView.setColorFilter(null);
                            rippleBackground.stopRippleAnimation();
                            showDialog();
                        }
                    }, animationDuration);
                }   else {
                    imageView.setColorFilter(null);
                    rippleBackground.stopRippleAnimation();
                    // Remove any pending tasks from the handler
                    handler.removeCallbacksAndMessages(null);
                }
            }
        });
    }


    private void showDialog(){

        final Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        LinearLayout editLayout = dialog.findViewById(R.id.layoutEdit);
        LinearLayout shareLayout = dialog.findViewById(R.id.layoutShare);
        LinearLayout uploadLayout = dialog.findViewById(R.id.layoutUpload);
        songTextView = dialog.findViewById(R.id.songTxt);
        artistTextView = dialog.findViewById(R.id.artistTxt);

        editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "Edit is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "Share is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "Upload is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}