package com.example.flashcard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private static final int pauseTimeMs = 7000; // 7 secondes
    private VideoView videoView;
    private RadioGroup radioGroup;
    private Button validateButton;
    private int currentQuestionIndex = 0;
    private int totalCorrectAnswers = 0;
    private ArrayList<Question> questions;
    private String difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
            difficulty = getIntent().getStringExtra("difficulty");

            return insets;
        });


        videoView = findViewById(R.id.videoView);
        radioGroup = findViewById(R.id.quizRadioGroup);
        validateButton = findViewById(R.id.validateButton);

        validateButton.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) return;

            RadioButton selected = findViewById(checkedId);
            String answer = selected.getText().toString();

            Question currentQuestion = questions.get(currentQuestionIndex);
            if (answer.equals(currentQuestion.getCorrectAnswer())) {
                totalCorrectAnswers++;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestion(questions.get(currentQuestionIndex));
            } else {
                // envoyer le score à ScoreActivity
                Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("totalCorrectAnswers", totalCorrectAnswers);
                intent.putExtra("totalQuestions", questions.size());
                startActivity(intent);
                finish();
            }
        });


        // cacher les éléments au départ
        /*radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sushi);
        videoView.setVideoURI(videoUri);
        videoView.start();

        // ⏸ Pause automatique à pauseTimeMs
        new Handler().postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                radioGroup.setVisibility(View.VISIBLE);
                validateButton.setVisibility(View.VISIBLE);
            }
        }, pauseTimeMs);


        validateButton.setOnClickListener(v -> {

            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {

                return;
            }
            RadioButton selected = findViewById(checkedId);
            String answer = selected.getText().toString();


            radioGroup.setVisibility(View.GONE);
            validateButton.setVisibility(View.GONE);


            videoView.start();
        });*/
    }

    private void showQuestion(Question q) {
        //Vidéo de la question
        Uri videoUri = Uri.parse(q.getVideoPath());
        videoView.setVideoURI(videoUri);
        videoView.start();

        //Cacher les boutons au départ
        radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);

        //Remplir les réponses dynamiquement
        radioGroup.removeAllViews(); // supprime les anciens RadioButtons
        for (String option : q.getOptions()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            radioGroup.addView(rb);
        }

        //Pause automatique à pauseTimeMs spécifique à la question
        new Handler().postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                radioGroup.setVisibility(View.VISIBLE);
                validateButton.setVisibility(View.VISIBLE);
            }
        }, q.getPauseTimeMs());
    }
}
