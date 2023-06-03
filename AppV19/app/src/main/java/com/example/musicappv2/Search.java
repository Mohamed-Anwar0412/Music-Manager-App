package com.example.musicappv2;

import android.content.Intent;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Button;
import android.widget.ImageButton;
=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
<<<<<<< HEAD
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
=======
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    private StorageReference storageReference;
    private MyAdapter adapter;
    private MediaPlayer mediaPlayer;
    private boolean isKeyboardVisible = false;
    SearchView searchView;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);
<<<<<<< HEAD
=======
//        searchView.setTextColor(getResources().getColor(android.R.color.white));
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
<<<<<<< HEAD
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
                finish();
                return true;
            } else if (id == R.id.navigation_search) {
                return true;
            } else if (id == R.id.navigation_shazam) {
                Intent intent = new Intent(getApplicationContext(), detetct_Activity.class);
                startActivityWithTransition(intent, false);
                finish();
                return true;
            }

            return false;
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
<<<<<<< HEAD
        searchView = findViewById(R.id.searchView);
=======
        SearchView searchView = findViewById(R.id.searchView);
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39

        // Initialize Firebase Storage
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("AllSongs");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter();
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return false;
            }
        });

        // A global layout listener to detect changes in the layout, including the keyboard
        // Show / Hide keyboard based on the navigation view
        ViewGroup viewGroup = findViewById(android.R.id.content);
        viewGroup.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                Rect r = new Rect();
                view.getWindowVisibleDisplayFrame(r);
                int screenHeight = view.getRootView().getHeight();

                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    // Keyboard is visible
                    if (!isKeyboardVisible) {
                        isKeyboardVisible = true;
                        hideBottomNavigationView();
                    }
                } else {
                    // Keyboard is not visible
                    if (isKeyboardVisible) {
                        isKeyboardVisible = false;
                        showBottomNavigationView();
                    }
                }
            }
        });

        bottomNavigationView.setTransitionName("bottom_navigation");
    }

    private void hideBottomNavigationView() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    private void showBottomNavigationView() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void startActivityWithTransition(Intent intent, boolean isToHome) {
        startActivity(intent);
        if (!isToHome) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void performSearch(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            adapter.clear();
            return;
        }

        String[] searchWords = searchText.toLowerCase().split(" ");

        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<String> searchResults = new ArrayList<>();
                    for (StorageReference fileReference : task.getResult().getItems()) {
                        String itemName = fileReference.getName().toLowerCase();

                        // Check if all search words are present in the item name
                        boolean isMatch = true;
                        for (String word : searchWords) {
                            if (!itemName.contains(word)) {
                                isMatch = false;
                                break;
                            }
                        }

                        if (isMatch) {
                            searchResults.add(fileReference.getName());
                        }
                    }
                    adapter.setItems(searchResults);
                } else {
                    // Handle any errors
                }
            }
        });
    }
<<<<<<< HEAD

=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void playSong(String itemName) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // Already playing a song, no need to start another one
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );

        storageReference.child(itemName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    mediaPlayer.setDataSource(uri.toString());
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopSong();
            }
        });
    }

<<<<<<< HEAD
    @Override
    public void onItemClick(String itemName) {

=======


    @Override
    public void onItemClick(String itemName) {
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            // Song is already playing, stop it
            stopSong();
        } else {
            // Song is not playing, start it
            playSong(itemName);
        }
    }

<<<<<<< HEAD

=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
}
