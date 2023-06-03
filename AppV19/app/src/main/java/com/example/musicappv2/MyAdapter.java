package com.example.musicappv2;

<<<<<<< HEAD
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
=======
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

<<<<<<< HEAD
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> items;
<<<<<<< HEAD
    Context context;


    private OnItemClickListener onItemClickListener;
    private StorageReference storageReference;
=======
    private OnItemClickListener onItemClickListener;

>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
    public void setItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void clear() {
        items = null;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview2, parent, false);
<<<<<<< HEAD
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("AllSongs");
=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemName = items.get(position);
        holder.bind(itemName);
<<<<<<< HEAD
        holder.favoriteee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferToFavorite(itemName);
            }
        });
=======
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
<<<<<<< HEAD
        ImageView imageView;
        TextView textView;
        CardView cardView;
        ImageButton favoriteee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.cardViewUpdate);
            favoriteee = itemView.findViewById(R.id.favorite);
=======
        //TextView itemNameTextView;
        ImageView imageView;
        TextView textView;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //itemNameTextView = itemView.findViewById(R.id.textView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            cardView = itemView.findViewById(R.id.cardViewUpdate);
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
            itemView.setOnClickListener(this);
        }

        public void bind(String itemName) {
            textView.setText(itemName);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String itemName = items.get(position);
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(itemName);
                }
            }
        }
    }
<<<<<<< HEAD

    private void transferToFavorite(String itemName) {
        // Assuming you have a reference to the FirebaseStorage instance for the user's favorite songs
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        StorageReference userFavoriteReference = FirebaseStorage.getInstance().getReference().child("Songs/" + userEmail + "/");

        // Copy the song file from "AllSongs" to "UserFavorites"
        StorageReference sourceReference = storageReference.child(itemName);
        File localFile = null; // Temporary local file to store the downloaded song

        try {
            localFile = File.createTempFile("temp_song", "mp3"); // Create a temporary file
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (localFile != null) {
            File finalLocalFile = localFile;
            sourceReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Song downloaded successfully to local file
                            Uri localFileUri = Uri.fromFile(finalLocalFile);

                            // Upload the song to the user's favorite directory
                            StorageReference newFileReference = userFavoriteReference.child(itemName);
                            newFileReference.putFile(localFileUri)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            // The song has been successfully transferred to the user's favorite list
                                            // You can perform any additional actions here, such as updating a database entry
                                            // Delete the temporary local file
                                            finalLocalFile.delete();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Failed to transfer the song to the user's favorite list
                                            // Handle the error appropriately
                                            // Delete the temporary local file
                                            finalLocalFile.delete();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to download the song from the source reference
                            // Handle the error appropriately
                        }
                    });
        }
    }
}
=======
}
>>>>>>> b70b683d156399a1cbe7345c2a180afc7dca0e39
