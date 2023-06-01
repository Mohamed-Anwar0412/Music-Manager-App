package com.example.musicappv2;

import android.content.Intent;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity implements MyAdapter.OnItemClickListener {

    private StorageReference storageReference;
    private MyAdapter adapter;
    private MediaPlayer mediaPlayer;
    private boolean isKeyboardVisible = false;
    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_search);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                Intent intent = new Intent(getApplicationContext(), DisplaySongs.class);
                startActivityWithTransition(intent, true);
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
        SearchView searchView = findViewById(R.id.searchView);

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



    private void playSong(String itemName) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            String audioUrl = "gs://music-manager-app-a58a3.appspot.com/AllSongs/" + itemName; // Replace with your Firebase Storage URL
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference songRef = firebaseStorage.getReferenceFromUrl(audioUrl);

            songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadUrl = uri.toString();
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(downloadUrl);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void stopSong() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer = null;
        }
    }

    @Override
    public void onItemClick(String itemName) {
        // Play or pause the selected song based on the current playback state
        playSong(itemName);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release the MediaPlayer resources when the activity is destroyed
        stopSong();
    }
}