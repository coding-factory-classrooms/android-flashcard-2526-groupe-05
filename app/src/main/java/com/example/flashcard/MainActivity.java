package com.example.flashcard;

import java.util.ArrayList;
import java.util.List;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private Button startQuizBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            startQuizBtn = findViewById(R.id.startQuizBtn);

            startQuizBtn.setOnClickListener(view -> showDifficultyDialog());


            //Bouton temporaire pour tester ScoreActivity
            //il faut appeler cette fonction à la fin du quiz
            //il faut une valeur String pour "difficulty" et un int pour "totalCorrectAnswers" et "totalQuestions"
            /*Button testScoreButton = findViewById(R.id.testScoreButton);
            testScoreButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("difficulty", "Facile");
                intent.putExtra("totalCorrectAnswers", 8);
                intent.putExtra("totalQuestions", 10);
                startActivity(intent);
            });*/

            Button testQuizButton = findViewById(R.id.testQuizButton);
            testQuizButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            });

            return insets;
        });
    }

    private void showDifficultyDialog() {
        final String[] levels = {"Facile", "Moyen", "Difficile"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisis ton niveau de difficulté");
        builder.setItems(levels, (dialog, which) -> {
            String selected = levels[which];
            Toast.makeText(this, "Niveau choisi : " + selected, Toast.LENGTH_SHORT).show();
            //  Ici je pourrais ensuite lancer mon QuizActivity selon le niveau choisi
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}