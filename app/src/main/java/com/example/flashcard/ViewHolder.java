package com.example.flashcard;

import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    VideoView videoView;
    TextView titleView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        videoView = itemView.findViewById(R.id.videoView); //** reference to item_view
        titleView = itemView.findViewById(R.id.titleView); //**
    }
}
