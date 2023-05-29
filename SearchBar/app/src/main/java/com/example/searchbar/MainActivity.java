package com.example.searchbar;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.SearchView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        SearchView searchView = findViewById(R.id.searchView);

        // Initialize Firebase Storage
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Songs");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MyAdapter();
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
    }

    private void performSearch(String searchText) {
        if (TextUtils.isEmpty(searchText)) {
            adapter.clear();
            return;
        }

        storageReference.listAll().addOnCompleteListener(new OnCompleteListener<ListResult>() {
            @Override
            public void onComplete(@NonNull Task<ListResult> task) {
                if (task.isSuccessful()) {
                    List<String> searchResults = new ArrayList<>();
                    for (StorageReference fileReference : task.getResult().getItems()) {
                        String itemName = fileReference.getName();
                        if (itemName.contains(searchText)) {
                            searchResults.add(itemName);
                        }
                    }
                    adapter.setItems(searchResults);

                    // Add click listener to the adapter
                    adapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(String itemName) {
                            // Play the selected song
                            playSong(itemName);
                        }
                    });
                } else {
                    // Handle any errors
                }
            }
        });
    }

    private void playSong(String itemName) {
        String audioUrl = "gs://searchbar-30138.appspot.com/Songs/" + itemName; // Replace with your Firebase Storage URL
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference songRef = firebaseStorage.getReferenceFromUrl(audioUrl);

        songRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = uri.toString();
                MediaPlayer mediaPlayer = new MediaPlayer();
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
