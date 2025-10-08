package com.example.flashcard;
import android.app.Dialog;
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
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_difficulty);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnEasy = dialog.findViewById(R.id.btnEasy);
        Button btnMedium = dialog.findViewById(R.id.btnMedium);
        Button btnHard = dialog.findViewById(R.id.btnHard);
        Button btnHardcore = dialog.findViewById(R.id.btnHardcore);

        btnEasy.setOnClickListener(v -> {
            Toast.makeText(this, "Beginner mode", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            // Lancer QuizActivity avec niveau Facile
        });

        btnMedium.setOnClickListener(v -> {
            Toast.makeText(this, "Intermediate mode", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            // Lancer QuizActivity avec niveau Moyen
        });

        btnHard.setOnClickListener(v -> {
            Toast.makeText(this, "Master mode", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            // Lancer QuizActivity avec niveau Difficile
        });

        btnHardcore.setOnClickListener(v -> {
            Toast.makeText(this, " GOD mode  ! ", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            // Lancer QuizActivity avec niveau Hardcore
        });

        dialog.show();
    }

}
