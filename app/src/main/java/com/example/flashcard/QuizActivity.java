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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class QuizActivity extends AppCompatActivity {

    private VideoView videoView;
    private RadioGroup radioGroup;
    private Button validateButton;
    private TextView questionTextView;
    private TextView answerFeedback;
    private Button nextQuestionButton;

    private ArrayList<Question> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int totalCorrectAnswers = 0;
    private String difficulty;

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


        radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);

        loadQuestionsFromJson();
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
        if (index >= questions.size()) {
            goToScorePage();
            return;
        }

        Question q = questions.get(index);

        // Configure la question
        questionTextView.setText(q.getQuestionText());
        radioGroup.removeAllViews();
        answerFeedback.setText("");
        answerFeedback.setVisibility(View.GONE);
        nextQuestionButton.setVisibility(View.GONE);
        for (String option : q.getOptions()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            radioGroup.addView(rb);
        }

        radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);

        // Lecture vidéo
        videoView.setVideoURI(Uri.parse(q.getVideoPath()));
        videoView.setOnPreparedListener(mp -> {
            if ("Hardcore".equalsIgnoreCase(difficulty)) {
                mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(3f));
            }
        });
        videoView.start();

        // ⏸ Pause automatique au moment défini
        long adjustedPauseTime = q.getPauseTimeMs();
        if ("Hardcore".equalsIgnoreCase(difficulty)) {
            adjustedPauseTime /= 3;
        }
        handler.postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                radioGroup.setVisibility(View.VISIBLE);
                validateButton.setVisibility(View.VISIBLE);
            }
        }, adjustedPauseTime);

        validateButton.setOnClickListener(v -> {
            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) return;

            RadioButton selected = findViewById(checkedId);
            if (selected.getText().toString().equals(q.getCorrectAnswer())) {
                totalCorrectAnswers++;
                answerFeedback.setText("Bonne réponse !");
                answerFeedback.setTextColor(Color.GREEN);
            } else {
                answerFeedback.setText("Mauvaise réponse ! La bonne était : " + q.getCorrectAnswer());
                answerFeedback.setTextColor(Color.RED);
            }

            radioGroup.setVisibility(View.GONE);
            validateButton.setVisibility(View.GONE);
            answerFeedback.setVisibility(View.VISIBLE);

            videoView.start();

            videoView.setOnCompletionListener(mp -> {
                nextQuestionButton.setVisibility(View.VISIBLE);
                nextQuestionButton.setOnClickListener(view -> {
                    currentQuestionIndex++;
                    showQuestion(currentQuestionIndex);
                });
            });
        });
    }

    private void goToScorePage() {
        Intent intent = new Intent(QuizActivity.this, ScoreActivity.class);
        intent.putExtra("difficulty", difficulty);
        intent.putExtra("totalCorrectAnswers", totalCorrectAnswers);
        intent.putExtra("totalQuestions", questions.size());
        startActivity(intent);
        finish();
    }
}
