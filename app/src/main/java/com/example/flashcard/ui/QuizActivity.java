package com.example.flashcard.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.*;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.data.Question;
import com.example.flashcard.R;
import com.example.flashcard.data.QuestionLoader;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private VideoView videoView;
    private RadioGroup radioGroup;
    private Button validateButton, nextQuestionButton, replayButton;
    private TextView questionTextView, answerFeedback, questionIndexTextView, timerTextView;
    private ImageView fondButton;

    private ImageView fondButton2;
    private FrameLayout videoContainer;

    private ArrayList<Question> questions;
    private int currentIndex = 0, totalCorrect = 0, timeLeft;
    private String difficulty;
    private final Handler handler = new Handler(), timerHandler = new Handler();
    private Runnable timerRunnable;

    // ------------------------------- LIFECYCLE ----------------------------------

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        difficulty = getIntent().getStringExtra("difficulty");
        questions = QuestionLoader.loadFromJson(this, "questions.json");
        Collections.shuffle(questions);

        showQuestion(currentIndex);
    }

    // ------------------------------- SETUP ----------------------------------

    private void initViews() {
        videoView = findViewById(R.id.videoView);
        radioGroup = findViewById(R.id.quizRadioGroup);
        validateButton = findViewById(R.id.validateButton);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        replayButton = findViewById(R.id.replayButton);
        fondButton = findViewById(R.id.fondButton);
        fondButton2 = findViewById(R.id.fondButton2);
        questionTextView = findViewById(R.id.questionText);
        questionIndexTextView = findViewById(R.id.questionIndexTextView);
        timerTextView = findViewById(R.id.timerTextView);
        answerFeedback = findViewById(R.id.answerFeedback);
        videoContainer = findViewById(R.id.videoContainer);

        setVisible(false, radioGroup, fondButton, fondButton2, validateButton, questionTextView);
    }

    // ------------------------------- LOGIQUE DU QUIZ ----------------------------------

    private void showQuestion(int index) {
        if (index >= questions.size()) {
            goToScorePage();
            return;
        }
        Question q = questions.get(index);

        questionIndexTextView.setText("Question " + (index + 1) + "/" + questions.size());
        questionTextView.setText(q.getQuestionText());
        resetUIForNewQuestion();

        radioGroup.removeAllViews();
        ArrayList<String> shuffled = new ArrayList<>(q.getOptions());
        Collections.shuffle(shuffled);
        shuffled.forEach(option -> {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            radioGroup.addView(rb);
        });

        playVideoWithPause(q);
        setupListeners(q);
    }

    private void setupListeners(Question q) {
        replayButton.setOnClickListener(v -> {
            hideAllInteractiveViews();
            animateVideoDownAndReset(() -> playVideoWithPause(q));
        });
        validateButton.setOnClickListener(v -> validateAnswer(q));
    }

    // ------------------------------- VIDÉO ----------------------------------

    private void playVideoWithPause(Question q) {
        videoView.setVideoURI(Uri.parse(q.getVideoPath()));
        videoView.setOnPreparedListener(mp -> {
            if ("💀 Hardcore".equalsIgnoreCase(difficulty))
                mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(5f));
        });
        videoView.start();

        handler.removeCallbacksAndMessages(null);
        long pauseTime = "💀 Hardcore".equalsIgnoreCase(difficulty)
                ? q.getPauseTimeMs() / 5
                : q.getPauseTimeMs();

        handler.postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                animateVideoUpAndShowUI();
                if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) startTimer();
            }
        }, pauseTime);
    }

    private void animateVideoUpAndShowUI() {
        videoContainer.animate()
                .translationY(-700f)
                .setDuration(700)
                .withEndAction(() ->
                        fadeInViews(radioGroup, validateButton, replayButton, fondButton, fondButton2, questionTextView))
                .start();
    }

    private void animateVideoDownAndReset(Runnable onEnd) {
        videoContainer.animate().translationY(0f).setDuration(700).withEndAction(onEnd).start();
    }

    // ------------------------------- VALIDATION ----------------------------------

    private void validateAnswer(Question q) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Veuillez sélectionner une réponse !", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) {
            timerHandler.removeCallbacks(timerRunnable);
            timerTextView.setVisibility(View.GONE);
        }

        RadioButton selected = findViewById(checkedId);
        boolean isCorrect = selected.getText().toString().equals(q.getCorrectAnswer());
        totalCorrect += isCorrect ? 1 : 0;

        String feedback = isCorrect
                ? "Bonne réponse !"
                : "Mauvaise réponse ! La bonne était : " + q.getCorrectAnswer();

        handleEndOfQuestion(feedback, isCorrect ? Color.GREEN : Color.RED);
    }

    private void handleEndOfQuestion(String text, int color) {
        setVisible(false, radioGroup, validateButton, replayButton, fondButton, fondButton2, questionTextView);
        answerFeedback.setText(text);
        answerFeedback.setTextColor(color);
        answerFeedback.setVisibility(View.VISIBLE);

        videoView.start();
        videoView.setOnCompletionListener(mp -> {
            nextQuestionButton.setVisibility(View.VISIBLE);
            fondButton.setVisibility(View.VISIBLE);
            nextQuestionButton.setOnClickListener(v -> {
                nextQuestionButton.setVisibility(View.GONE);
                fondButton.setVisibility(View.GONE);
                answerFeedback.setVisibility(View.GONE);
                animateVideoDownAndReset(() -> showQuestion(++currentIndex));
            });
        });
    }

    // ------------------------------- TIMER ----------------------------------

    private void startTimer() {
        timerTextView.setVisibility(View.VISIBLE);
        timeLeft = 10;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerTextView.setText("⏱ " + timeLeft + "s");
                if (timeLeft-- <= 0) {
                    timerTextView.setVisibility(View.GONE);
                    Toast.makeText(QuizActivity.this, "Temps écoulé !", Toast.LENGTH_SHORT).show();
                    handleEndOfQuestion("Temps écoulé ! Mauvaise réponse !", Color.RED);
                    return;
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    // ------------------------------- OUTILS UI ----------------------------------

    private void resetUIForNewQuestion() {
        setVisible(false, radioGroup, fondButton, fondButton2, validateButton, replayButton, questionTextView, answerFeedback, nextQuestionButton);
    }

    private void hideAllInteractiveViews() {
        setVisible(false, radioGroup, fondButton, fondButton2, validateButton, replayButton, questionTextView, answerFeedback);
    }

    private void fadeInViews(View... views) {
        for (View v : views) {
            v.setAlpha(0f);
            v.setVisibility(View.VISIBLE);
            v.animate().alpha(1f).setDuration(500).start();
        }
    }

    private void setVisible(boolean visible, View... views) {
        for (View v : views) v.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    // ------------------------------- SCORE ----------------------------------

    private void goToScorePage() {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("totalCorrectAnswers", totalCorrect);
        intent.putExtra("totalQuestions", questions.size());
        startActivity(intent);
        finish();
    }
}
