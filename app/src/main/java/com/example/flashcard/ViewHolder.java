package com.example.flashcard;

import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;


import androidx.recyclerview.widget.RecyclerView;

public class ViewHolder extends RecyclerView.ViewHolder {

    VideoView videoView;
    TextView titleView;

    public ViewHolder(View itemView) {
        super(itemView);
        titleView = itemView.findViewById(R.id.titleView);
        videoView = itemView.findViewById(R.id.videoView);

        // a clic to play/pause the video
        videoView.setOnClickListener(v -> {
            if (videoView.isPlaying()) {
                videoView.pause();
            } else {
                videoView.start();
            }
        });
    }
}
