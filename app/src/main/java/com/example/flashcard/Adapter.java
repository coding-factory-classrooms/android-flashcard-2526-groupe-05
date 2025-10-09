package com.example.flashcard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<ViewHolder>{

    Context context;
    List<Item> items;

    public Adapter(List<Item> items, Context context) {
        this.items = items;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_item_view, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.titleView.setText(items.get(position).getTitle());
        holder.videoView.setVideoPath(items.get(position).getVideo());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
