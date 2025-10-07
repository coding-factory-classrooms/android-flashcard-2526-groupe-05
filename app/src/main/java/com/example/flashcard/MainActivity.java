package com.example.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            //Bouton temporaire pour tester ScoreActivity
            //il faut appeler cette fonction à la fin du quiz
            //il faut une valeur String pour "difficulty" et un int pour "totalCorrectAnswers" et "totalQuestions"
            Button testScoreButton = findViewById(R.id.testScoreButton);
            testScoreButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ScoreActivity.class);
                intent.putExtra("difficulty", "Facile");
                intent.putExtra("totalCorrectAnswers", 8);
                intent.putExtra("totalQuestions", 10);
                startActivity(intent);
            });

            return insets;
        });
    }
}