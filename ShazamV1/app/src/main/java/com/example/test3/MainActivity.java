package com.example.test3;

import android.Manifest;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private String audioFilePath;
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false; // Flag to track the recording status

    private TextView statusTextView;
    private TextView artistTextView;
    private TextView songTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = findViewById(R.id.statusTextView);
        artistTextView = findViewById(R.id.artistTextView);
        songTextView = findViewById(R.id.songTextView);

        Button recordButton = findViewById(R.id.recordButton);
        recordButton.setOnClickListener(v ->  {
             {
                 setup();
                if (checkPermissions()) {
                    if (!isRecording) { // Start recording
                        startRecording();
                        recordButton.setText("Stop");
                        statusTextView.setText("Recording...");
                        isRecording = true;
                    } else { // Stop recording
                        stopRecording();
                        recordButton.setText("Record");
                        statusTextView.setText("Identifying audio...");
                        isRecording = false;
                        identifyAudio(audioFilePath);
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

                }
            }
        });

        audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
    }

    // Rest of the code...

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
                    JsonObject result = responseJson.getAsJsonObject("result");
                    String artist = result.get("artist").getAsString();
                    String song = result.get("title").getAsString();
                    //statusTextView.setText("Done!!");
                    // Show music details
                    showMusicDetails(artist, song);
                } else {
                    // Handle the API error
                    statusTextView.setText("HaHaHa!!");
                }

            }
        });
    }


    private void showMusicDetails(String artist, String song) {
        runOnUiThread(() ->  {

                artistTextView.setText("Artist: " + artist);
                songTextView.setText("Song: " + song);
                statusTextView.setVisibility(View.GONE);
                artistTextView.setVisibility(View.VISIBLE);
                songTextView.setVisibility(View.VISIBLE);

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
            } else {
                // Permission denied, handle the case where the user denied the permissions
            }
        }
    }
    private void setup()
    {
        artistTextView.setText("");
        songTextView.setText("");
        statusTextView.setText("not recording");
        statusTextView.setVisibility(View.VISIBLE);
        artistTextView.setVisibility(View.GONE);
        songTextView.setVisibility(View.GONE);
    }
}

