package com.example.flashcard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    private VideoView videoView;
    private RadioGroup radioGroup;
    private Button validateButton;
    private TextView questionTextView;
    private TextView answerFeedback;
    private Button nextQuestionButton;
    private Button replayButton;
    private TextView questionIndexTextView;
    private ImageView fondButton;
    private FrameLayout videoContainer;
    private ArrayList<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int totalCorrectAnswers = 0;
    private String difficulty;
    private TextView timerTextView;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private int timeLeft;

    private Handler handler = new Handler();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        difficulty = getIntent().getStringExtra("difficulty");
        videoView = findViewById(R.id.videoView);
        radioGroup = findViewById(R.id.quizRadioGroup);
        validateButton = findViewById(R.id.validateButton);
        questionTextView = findViewById(R.id.questionText);
        answerFeedback = findViewById(R.id.answerFeedback);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        replayButton = findViewById(R.id.replayButton);
        questionIndexTextView = findViewById(R.id.questionIndexTextView);
        timerTextView = findViewById(R.id.timerTextView);
        fondButton = findViewById(R.id.fondButton);
        videoContainer = findViewById(R.id.videoContainer);
        radioGroup.setVisibility(View.GONE);
        fondButton.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);
        questionTextView.setVisibility(View.GONE);

        loadQuestionsFromJson();
        Collections.shuffle(questions);
        showQuestion(0);
    }

    private void loadQuestionsFromJson() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("questions.json"))
            );
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String videoPath = obj.getString("videoPath");
                String questionText = obj.getString("questionText");
                JSONArray optionsArray = obj.getJSONArray("options");
                ArrayList<String> options = new ArrayList<>();
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }
                String correctAnswer = obj.getString("correctAnswer");
                int pauseTimeMs = obj.getInt("pauseTimeMs");

                questions.add(new Question(videoPath, questionText, options, correctAnswer, pauseTimeMs));
            }
        } catch (Exception e) {
            Log.e("QuizActivity", "Erreur lecture JSON: " + e.getMessage());
        }
    }

    private void showQuestion(int index) {
        //Si toutes les questions ont été répondu, aller à la page des scores
        if (index >= questions.size()) {
            goToScorePage();
            return;
        }

        Question q = questions.get(index);

        // Affiche l'index actuel de la question
        questionIndexTextView.setText("Question " + (index + 1) + "/" + questions.size());

        //Affiche les questions actuelles dans le textView
        questionTextView.setText(q.getQuestionText());

        //Masque les textView et les Boutons
        radioGroup.removeAllViews();
        answerFeedback.setText("");
        answerFeedback.setVisibility(View.GONE);
        nextQuestionButton.setVisibility(View.GONE);
        replayButton.setVisibility(View.GONE);
        questionTextView.setVisibility(View.GONE);

        // Mélange les options pour cette question
        ArrayList<String> shuffledOptions = new ArrayList<>(q.getOptions());
        Collections.shuffle(shuffledOptions);

        // Affiche les boutons de réponses pour la question actuelle
        for (String option : shuffledOptions) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            radioGroup.addView(rb);
        }

        radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);
        fondButton.setVisibility(View.GONE);
        questionTextView.setVisibility(View.GONE);

        //Joue la video avec la pause au bon moment
        playVideoWithPause(q);

        //Bouton pour rejouer la vidéo
        replayButton.setOnClickListener(v -> {
            replayButton.setVisibility(View.GONE);
            radioGroup.setVisibility(View.GONE);
            validateButton.setVisibility(View.GONE);
            fondButton.setVisibility(View.GONE);
            answerFeedback.setVisibility(View.GONE);
            questionTextView.setVisibility(View.GONE);

            animateVideoDownAndReset(() -> playVideoWithPause(questions.get(currentQuestionIndex)));
        });

        //vérification de la réponse et passer a la question suivante
        validateButton.setOnClickListener(v -> validateAnswer(q));
    }

    private void playVideoWithPause(Question q) {
        // Configurer et lancer la vidéo
        videoView.setVideoURI(Uri.parse(q.getVideoPath()));
        videoView.setOnPreparedListener(mp -> {
            if ("💀 Hardcore".equalsIgnoreCase(difficulty)) { //Si mode harcore alors video speed X3
                mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(3f));
            }
        });
        videoView.start();

        // Supprimer d'éventuels callbacks précédents
        handler.removeCallbacksAndMessages(null);

        //met la video sur pause au bon moment
        //Si mode hardcore est séléctionné alors temps à attendre avant de paule la video est divisé par 3
        long adjustedPauseTime = q.getPauseTimeMs();
        if ("💀 Hardcore".equalsIgnoreCase(difficulty)) adjustedPauseTime /= 3;

        //met pause sur la video et affiche les questions/Choix de réponses après un certains temps
        handler.postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                animateVideoUpAndShowUI(); // 👈 Animation au lieu d’apparition brutale

                //si mode Time Attack, lancer le timer
                if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) {
                    replayButton.setVisibility(View.GONE);
                    startTimer();
                }
            }
        }, adjustedPauseTime);
    }

    private void animateVideoUpAndShowUI() {
        // Déplace la vidéo vers le haut (position absolue)
        videoContainer.animate()
                .translationY(-700f)  // absolute, pas relative
                .setDuration(700)
                .withEndAction(() -> {
                    // Affiche les éléments du quiz en fondu
                    radioGroup.setAlpha(0f);
                    validateButton.setAlpha(0f);
                    replayButton.setAlpha(0f);
                    fondButton.setAlpha(0f);
                    questionTextView.setAlpha(0f);

                    radioGroup.setVisibility(View.VISIBLE);
                    validateButton.setVisibility(View.VISIBLE);
                    replayButton.setVisibility(View.VISIBLE);
                    fondButton.setVisibility(View.VISIBLE);
                    questionTextView.setVisibility(View.VISIBLE);

                    radioGroup.animate().alpha(1f).setDuration(500).start();
                    validateButton.animate().alpha(1f).setDuration(500).start();
                    replayButton.animate().alpha(1f).setDuration(500).start();
                    fondButton.animate().alpha(1f).setDuration(500).start();
                    questionTextView.animate().alpha(1f).setDuration(500).start();
                })
                .start();
    }

    private void animateVideoDownAndReset(Runnable onAnimationEnd) {
        videoContainer.animate()
                .translationY(0f) // remonte à sa position initiale
                .setDuration(700) // durée identique à la montée pour la cohérence
                .withEndAction(onAnimationEnd)
                .start();
    }

    private void validateAnswer(Question q) {
        int checkedId = radioGroup.getCheckedRadioButtonId();

        //affiche un message si on essaye de cliquer sur valiser alors que aucune réponse n'est séléctionné
        if (checkedId == -1) {
            Toast.makeText(this, "Veuillez sélectionner une réponse !", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedAnswer = findViewById(checkedId);
        if (selectedAnswer == null) {
            Toast.makeText(this, "Erreur, aucune option sélectionnée.", Toast.LENGTH_SHORT).show();
            return;
        }

        //arrête le timer si le mode time attach est activé
        if ("⏱ Time Attack".equalsIgnoreCase(difficulty)) {
            timerHandler.removeCallbacks(timerRunnable);
            timerTextView.setVisibility(View.GONE);
        }

        //Vérifie si on à sélectionné la bonne réponse, si oui ajout 1 dans le total de bonne réponses
        //puis appelle fonction handleEndOfQuestion
        RadioButton selected = findViewById(checkedId);
        boolean isCorrect = selected.getText().toString().equals(q.getCorrectAnswer());
        if (isCorrect) {
            totalCorrectAnswers++;
            handleEndOfQuestion("Bonne réponse !", Color.GREEN);
        } else {
            handleEndOfQuestion("Mauvaise réponse ! La bonne était : " + q.getCorrectAnswer(), Color.RED);
        }
    }

    //affiche le timer de 10 secondes
    private void startTimer() {
        timerTextView.setVisibility(View.VISIBLE);
        timeLeft = 10;

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                timerTextView.setText("⏱ " + timeLeft + "s");

                if (timeLeft <= 0) {
                    timerTextView.setVisibility(View.GONE);
                    timerHandler.removeCallbacks(this);
                    Toast.makeText(QuizActivity.this, "Temps écoulé !", Toast.LENGTH_SHORT).show();
                    handleEndOfQuestion("Temps écoulé ! Mauvaise réponse !", Color.RED);
                    return;
                }

                timeLeft--;
                timerHandler.postDelayed(this, 1000);
            }
        };

        timerHandler.post(timerRunnable);
    }

    private void handleEndOfQuestion(String feedbackText, int feedbackColor) {
        //Affiche le message bonne ou mauvaire réponse au joueur
        answerFeedback.setText(feedbackText);
        answerFeedback.setTextColor(feedbackColor);
        answerFeedback.setVisibility(View.VISIBLE);

        //Cache les boutons et options
        radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);
        replayButton.setVisibility(View.GONE);
        fondButton.setVisibility(View.GONE);
        questionTextView.setVisibility(View.GONE);
        answerFeedback.setVisibility(View.VISIBLE);

        //lance la fin de la video pous passe à la questions suivante quand on clique sur le bouton
        videoView.start();
        videoView.setOnCompletionListener(mp -> {
            nextQuestionButton.setVisibility(View.VISIBLE);
            nextQuestionButton.setOnClickListener(view -> {
                nextQuestionButton.setVisibility(View.GONE);
                answerFeedback.setVisibility(View.GONE);
                animateVideoDownAndReset(() -> {
                currentQuestionIndex++;
                showQuestion(currentQuestionIndex);

                });
            });
        });
    }

    //Renvoie vers la page du score quand on à répondu à toutes les questions
    private void goToScorePage() {
        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("totalCorrectAnswers", totalCorrectAnswers);
        intent.putExtra("totalQuestions", questions.size());
        startActivity(intent);
        finish();
    }
}
