package com.example.flashcard.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
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

import com.example.flashcard.R;
import com.example.flashcard.data.Question;
import com.example.flashcard.data.QuestionLoader;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    // ------------------- UI -------------------
    private VideoView videoView;
    private RadioGroup radioGroup;
    private Button validateButton, nextButton, replayButton;
    private TextView questionText, feedbackText, questionIndexText, timerText;
    private ImageView fondButton, fondButton2;
    private FrameLayout videoContainer;

    // ------------------- Données -------------------
    private ArrayList<Question> questions;
    private int currentIndex = 0, totalCorrect = 0, timeLeft;
    private String difficulty;

    // ------------------- Audio -------------------
    private MediaPlayer mediaPlayer;

    // ------------------- Handlers -------------------
    private final Handler handler = new Handler(), timerHandler = new Handler();
    private Runnable timerRunnable;

    // ------------------- LIFECYCLE -------------------
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        initViews();
        difficulty = getIntent().getStringExtra("difficulty");

        // 💡 Cache le bouton replay en mode Time Attack
        if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) {
            replayButton.setVisibility(View.INVISIBLE);
            fondButton2.setVisibility(View.INVISIBLE);
        }

        questions = QuestionLoader.loadFromJson(this, "questions.json");
        Collections.shuffle(questions);
        showQuestion(currentIndex);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopMusic();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopMusic();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ------------------- INITIALISATION -------------------
    private void initViews() {
        videoView = findViewById(R.id.videoView);
        radioGroup = findViewById(R.id.quizRadioGroup);
        validateButton = findViewById(R.id.validateButton);
        nextButton = findViewById(R.id.nextQuestionButton);
        replayButton = findViewById(R.id.replayButton);
        fondButton = findViewById(R.id.fondButton);
        fondButton2 = findViewById(R.id.fondButton2);
        questionText = findViewById(R.id.questionText);
        feedbackText = findViewById(R.id.answerFeedback);
        questionIndexText = findViewById(R.id.questionIndexTextView);
        timerText = findViewById(R.id.timerTextView);
        videoContainer = findViewById(R.id.videoContainer);

        setVisible(false, radioGroup, fondButton, fondButton2, validateButton, questionText, feedbackText, nextButton);
    }

    // ------------------- AFFICHAGE QUESTION -------------------
    private void showQuestion(int index) {
        if (index >= questions.size()) {
            goToScorePage();
            return;
        }

        Question q = questions.get(index);
        questionIndexText.setText("Question " + (index + 1) + "/" + questions.size());
        questionText.setText(q.getQuestionText());
        resetUI();

        // Ajout des choix aléatoires
        radioGroup.removeAllViews();
        ArrayList<String> shuffled = new ArrayList<>(q.getOptions());
        Collections.shuffle(shuffled);
        for (String option : shuffled) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            radioGroup.addView(rb);
        }

        playVideoWithPause(q);
        setupListeners(q);
    }

    private void setupListeners(Question q) {
        if (!"⏱ Time Attack".equalsIgnoreCase(difficulty)) {
            replayButton.setOnClickListener(v -> {
                hideQuizUI();
                animateVideoDown(() -> playVideoWithPause(q));
            });
        }
        validateButton.setOnClickListener(v -> validateAnswer(q));
    }

    // ------------------- VIDÉO -------------------
    private void playVideoWithPause(Question q) {
        stopMusic(); // 🔇 Coupe la musique à chaque lancement vidéo

        videoView.setVideoURI(Uri.parse(q.getVideoPath()));
        videoView.setOnPreparedListener(mp -> {
            if ("💀 Hardcore".equalsIgnoreCase(difficulty))
                mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(3f));
        });
        videoView.start();

        handler.removeCallbacksAndMessages(null);
        long pauseTime = "💀 Hardcore".equalsIgnoreCase(difficulty)
                ? q.getPauseTimeMs() / 3
                : q.getPauseTimeMs();

        handler.postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                animateVideoUp();
                startMusic(); // 🎵 Lance la musique quand la question est visible
                if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) startTimer();
            }
        }, pauseTime);
    }

    private void animateVideoUp() {
        videoContainer.animate()
                .translationY(-500f)
                .setDuration(700)
                .withEndAction(() -> fadeInViews(
                        radioGroup, validateButton,
                        fondButton, fondButton2,
                        questionText,
                        "⏱ Time Attack".equalsIgnoreCase(difficulty) ? null : replayButton))
                .start();
    }

    private void animateVideoDown(Runnable onEnd) {
        videoContainer.animate()
                .translationY(0f)
                .setDuration(500)
                .withEndAction(onEnd)
                .start();
    }

    // ------------------- VALIDATION -------------------
    private void validateAnswer(Question q) {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Veuillez sélectionner une réponse !", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedAnswer = findViewById(checkedId);
        if (selectedAnswer == null) {
            Toast.makeText(this, "Erreur, aucune option sélectionnée.", Toast.LENGTH_SHORT).show();
            return;
        }

        stopMusic(); // 🔇 Stoppe la musique quand la réponse est validée

        RadioButton selected = findViewById(checkedId);
        boolean isCorrect = selected.getText().toString().equals(q.getCorrectAnswer());

        if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) {
            timerHandler.removeCallbacks(timerRunnable);
            timerText.setVisibility(View.GONE);
        }

        totalCorrect += isCorrect ? 1 : 0;
        String feedback = isCorrect
                ? "Bonne réponse !"
                : "Mauvaise réponse ! La bonne était : " + q.getCorrectAnswer();
        handleEndOfQuestion(feedback, isCorrect ? Color.GREEN : Color.RED);
    }

    private void handleEndOfQuestion(String text, int color) {
        hideQuizUI();
        feedbackText.setText(text);
        feedbackText.setTextColor(color);
        feedbackText.setVisibility(View.VISIBLE);
        videoView.start(); // relance la fin de la vidéo

        videoView.setOnCompletionListener(mp -> {
            nextButton.setVisibility(View.VISIBLE);
            fondButton.setVisibility(View.VISIBLE);
            nextButton.setOnClickListener(v -> {
                hideQuizUI();
                animateVideoDown(() -> showQuestion(++currentIndex));
            });
        });
    }

    // ------------------- TIMER (Time Attack) -------------------
    private void startTimer() {
        timerText.setVisibility(View.VISIBLE);
        timeLeft = 10;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerText.setText("⏱ " + timeLeft + "s");
                if (timeLeft-- <= 0) {
                    timerText.setVisibility(View.GONE);
                    Toast.makeText(QuizActivity.this, "Temps écoulé !", Toast.LENGTH_SHORT).show();
                    handleEndOfQuestion("Temps écoulé ! Mauvaise réponse !", Color.RED);
                    stopMusic();
                    return;
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    // ------------------- MUSIQUE -------------------
    private void startMusic() {
        stopMusic(); // évite le chevauchement
        mediaPlayer = MediaPlayer.create(this, R.raw.question_music);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // ------------------- OUTILS UI -------------------
    private void resetUI() {
        setVisible(false, radioGroup, fondButton, fondButton2, validateButton, replayButton, questionText, feedbackText, nextButton);
    }

    private void hideQuizUI() {
        setVisible(false, radioGroup, fondButton, fondButton2, validateButton, replayButton, questionText, feedbackText);
    }

    private void fadeInViews(View... views) {
        for (View v : views) {
            if (v == null) continue;
            if ("⏱ Time Attack".equalsIgnoreCase(difficulty)
                    && (v == replayButton || v == fondButton2)) {
                v.setVisibility(View.INVISIBLE);
                continue;
            }
            v.setAlpha(0f);
            v.setVisibility(View.VISIBLE);
            v.animate().alpha(1f).setDuration(500).start();
        }
    }

    private void setVisible(boolean visible, View... views) {
        for (View v : views) {
            if (v != null) v.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    // ------------------- SCORE -------------------
    private void goToScorePage() {
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("totalCorrectAnswers", totalCorrect);
        intent.putExtra("totalQuestions", questions.size());
        startActivity(intent);
        finish();
    }
}
