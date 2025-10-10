package com.example.flashcard.ui;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.R;

public class ScoreActivity extends AppCompatActivity {

    // UI elements
    TextView resultTextView;
    TextView difficultyTextView;
    TextView pourcentageTextView;
    ProgressBar progressBar;
    Button backToMenuButton;
    Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // lifecycle
        EdgeToEdge.enable(this); // modern layout
        setContentView(R.layout.activity_score); // connect layout

        // handle system bars and initialize views
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        // link layout components
        resultTextView = findViewById(R.id.resultTextView);
        difficultyTextView = findViewById(R.id.difficultyTextView);
        pourcentageTextView = findViewById(R.id.pourcentageTextView);
        progressBar = findViewById(R.id.progressBar);
        backToMenuButton = findViewById(R.id.backToMenuButton);
        shareButton = findViewById(R.id.shareButton);

        // get quiz data
        Intent intent = getIntent();
        int totalCorrectAnswers = intent.getIntExtra("totalCorrectAnswers", 0);
        int totalQuestions = intent.getIntExtra("totalQuestions", 0);
        String difficulty = intent.getStringExtra("difficulty");

        // calculate percentage safely
        int pourcentage = 0;
        if (totalQuestions > 0) {
            pourcentage = (totalCorrectAnswers * 100) / totalQuestions;
        }

        // display results
        difficultyTextView.setText("Difficulté : " + difficulty);
        resultTextView.setText("Tu as " + totalCorrectAnswers + " bonnes réponses sur " + totalQuestions);
        pourcentageTextView.setText("Taux de réussite : " + pourcentage + "%");

        // animate progress bar smoothly from 0 to the percentage
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, pourcentage);
        animation.setDuration(1500); // 1.5 seconds
        animation.start();

        // go back to main menu
        backToMenuButton.setOnClickListener(v1 -> {
            Intent intent1 = new Intent(ScoreActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        });

        // share result
        shareButton.setOnClickListener(view -> {
            String message = "J'ai eu " + totalCorrectAnswers + "/" + totalQuestions +
                    " au quiz " + difficulty + " sur l'application FlashCardQuiz !" +
                    " 🔥 Télécharge-la ici : https://example.com/";

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Partager via"));
        });

    }
}
