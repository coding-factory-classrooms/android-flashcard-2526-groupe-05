package com.example.flashcard;


import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Question {
    private String videoPath;
    private String questionText;
    private List<String> options;
    private String correctAnswer;
    private int pauseTimeMs;

    public Question(String videoPath, String questionText, List<String> options, String correctAnswer, int pauseTimeMs) {
        this.videoPath = videoPath;
        this.questionText = questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.pauseTimeMs = pauseTimeMs;
    }

    public String getVideoPath() { return videoPath; }
    public String getQuestionText() { return questionText; }
    public List<String> getOptions() { return options; }
    public String getCorrectAnswer() { return correctAnswer; }
    public int getPauseTimeMs() { return pauseTimeMs; }
}

/*public class QuestionRepository {

    public static List<Question> getQuestions(Context context) {
        List<Question> questionList = new ArrayList<>();

        questionList.add(new Question(
                "android.resource://" + context.getPackageName() + "/" + R.raw.pub1,
                "De quelle marque s’agit-il ?",
                Arrays.asList("Old Spice", "Axe", "Dior"),
                "Old Spice",
                4000
        ));

        questionList.add(new Question(
                "android.resource://" + context.getPackageName() + "/" + R.raw.pub2,
                "Quel produit est vendu à la fin ?",
                Arrays.asList("Chocolat", "Voiture", "Parfum"),
                "Voiture",
                6000
        ));

        return questionList;
    }
}*/

