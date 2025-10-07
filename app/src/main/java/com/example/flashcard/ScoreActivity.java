package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ScoreActivity extends AppCompatActivity {

    TextView resultTextView;
    TextView difficultyTextView;
    TextView pourcentageTextView;
    Button backToMenuButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_score);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //relie les TextView et le bouton à ceux du xml
            resultTextView = findViewById(R.id.resultTextView);
            difficultyTextView = findViewById(R.id.difficultyTextView);
            pourcentageTextView = findViewById(R.id.pourcentageTextView);
            backToMenuButton = findViewById(R.id.backToMenuButton);

            //récupère les valeurs envoyées par la page du quiz
            Intent intent = getIntent();
            int totalCorrectAnswers = intent.getIntExtra("totalCorrectAnswers", 0);
            int totalQuestions = intent.getIntExtra("totalQuestions", 0);
            String difficulty = intent.getStringExtra("difficulty");

            //calcul du pourcentage avec une protection contre la division par 0
            int pourcentage = 0;
            if (totalQuestions > 0) {
                pourcentage = (totalCorrectAnswers * 100) / totalQuestions;
            }

            //affiche tout dans les TextView
            difficultyTextView.setText("Difficulté : " + difficulty);
            resultTextView.setText("Tu as " + totalCorrectAnswers + " bonnes réponses sur " + totalQuestions);
            pourcentageTextView.setText("Taux de réussite : " + pourcentage + "%");

            //retourne sur le main menu quand on clique sur le bouton
            backToMenuButton.setOnClickListener(v1 -> {
                Intent intent1 = new Intent(ScoreActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
            });

            return insets;
        });
    }
}