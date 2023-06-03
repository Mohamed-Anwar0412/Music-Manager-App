package com.example.musicappv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> items;
    private OnItemClickListener onItemClickListener;

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
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String itemName = items.get(position);
        holder.bind(itemName);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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
}