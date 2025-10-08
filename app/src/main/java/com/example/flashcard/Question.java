package com.example.flashcard;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String videoPath;
    private String questionText;
    private ArrayList<String> options;
    private String correctAnswer;
    private int pauseTimeMs;

    public Question(String videoPath, String questionText, ArrayList<String> options, String correctAnswer, int pauseTimeMs) {
        this.videoPath = videoPath;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.pauseTimeMs = pauseTimeMs;
    }

    public String getVideoPath() { return videoPath; }
    public String getQuestionText() { return questionText; }
    public ArrayList<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getPauseTimeMs() { return pauseTimeMs; }
}
