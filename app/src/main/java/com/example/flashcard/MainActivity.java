package com.example.flashcard;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button startQuizBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startQuizBtn = findViewById(R.id.startQuizBtn);

        startQuizBtn.setOnClickListener(v -> showDifficultyDialog());
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
