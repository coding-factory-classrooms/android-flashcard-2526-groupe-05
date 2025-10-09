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
        Item item = items.get(position);

        switch (position) {
            case 0:
                holder.titleView.setText("Pub : Sushi");
                break;
            case 1:
                holder.titleView.setText("Pub : Bonbon");
                break;
            case 2:
                holder.titleView.setText("Pub : Boisson");
                break;
            default:
                holder.titleView.setText(item.getTitle());
                break;
        }
        holder.videoView.setVideoPath(item.getVideo());


        holder.videoView.seekTo(1); // show the first frame

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
