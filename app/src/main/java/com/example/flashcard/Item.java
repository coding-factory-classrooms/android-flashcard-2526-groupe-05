package com.example.flashcard;

public class Item {
    String title;
    String video;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public Item(String title, String video) {  //each line of RecycleView
        this.title = title;
        this.video = video;

    }

}
