package com.example.musicappv2;

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
import android.os.Looper;
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

public class detetct_Activity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private String audioFilePath;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false; // Flag to track the recording status
    private TextView artistTextView;
    private TextView songTextView;

    private String artist;
    private String song;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detetct);

        final RippleBackground rippleBackground = (RippleBackground)findViewById(R.id.content);
        final ImageView imageView = (ImageView)findViewById(R.id.centerImage);
        final Handler handler = new Handler();
        dialog = new Dialog(this);
        setUpDialog();

        audioFilePath = getExternalFilesDir(null).getAbsolutePath() + "/recording.3gp";

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermissions()) {
                    // Set the duration of the animation in milliseconds
                    int animationDuration = 9000; // Example: 9 seconds
                    if (!isRecording) { // Start recording
                        dialog.dismiss();
                        startRecording();
                        isRecording = true;
                        imageView.setColorFilter(Color.argb(255, 255, 255, 255));
                        rippleBackground.startRippleAnimation();
                        // Schedule a task to stop the animation after the specified duration
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Sequential? Ignore this note
                                stopRecording();
                                isRecording = false;
                                imageView.setColorFilter(null);
                                rippleBackground.stopRippleAnimation();
                                identifyAudio(audioFilePath);
                            }
                        }, animationDuration);
                    } else { // Stop recording
                        stopRecording();
                        isRecording = false;
                        imageView.setColorFilter(null);
                        rippleBackground.stopRippleAnimation();
                        // Remove any pending tasks from the handler
                        handler.removeCallbacksAndMessages(null);
                        identifyAudio(audioFilePath);
                    }
                }   else {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                }
            }
        });
    }

    private void setUpDialog(){

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

                Toast.makeText(detetct_Activity.this, "Edit is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(detetct_Activity.this, "Share is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        uploadLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(detetct_Activity.this, "Upload is Clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void showDialog(){

        songTextView.setText("Song: " + song);
        artistTextView.setText("Artist: " + artist);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Your code to run on the UI thread
                dialog.show();
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
                dialog.getWindow().setGravity(Gravity.BOTTOM);
            }
        });

    }
    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();
    }

    private void stopRecording() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
                mediaRecorder.release();
            } catch (RuntimeException e) {
                // Handle the exception appropriately, such as displaying an error message
                e.printStackTrace();
            } finally {
                mediaRecorder = null;
            }
        }
    }

    private void identifyAudio(String audioFilePath) {
        File audioFile = new File(audioFilePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/3gpp"), audioFile);

        MultipartBody.Part audioPart = MultipartBody.Part.createFormData("file", audioFile.getName(), requestFile);

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(audioPart)
                .addFormDataPart("return", "apple_music,spotify")
                .addFormDataPart("api_token", "fa1cf7bcba65fcae9fd3bb9abab5805c")
                .build();

        Request request = new Request.Builder()
                .url("https://api.audd.io/")
                .get()
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle network error
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    System.out.println(responseBody);
                    // Process the response as needed

                    // Extract artist and song from the response if it is in JSON format
                    JsonParser parser = new JsonParser();
                    JsonObject responseJson = parser.parse(responseBody).getAsJsonObject();
                    if(responseJson != null) {
                        JsonObject result = responseJson.getAsJsonObject("result");
                        if(result != null) {
                            artist = result.get("artist").getAsString();
                            song = result.get("title").getAsString();
                            //statusTextView.setText("Done!!");
                            // Show music details
                            showDialog();
                        }
                    }
                } else {
                    // Handle the API error
                    Toast.makeText(detetct_Activity.this, "Failed to detect Track", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }




    private boolean checkPermissions() {
        int resultAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int resultStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (resultAudio == PackageManager.PERMISSION_GRANTED && resultStorage == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE
            );
            return false;
        }
    }

    // Add the onRequestPermissionsResult method to handle the permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Both permissions are granted, you can proceed with recording or other actions
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied, handle the case where the user denied the permissions
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}