package com.example.flashcard;

import java.util.ArrayList;

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

            Button aboutButton = findViewById(R.id.aboutButton);
            aboutButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            });

            return insets;
        });
    }

    private void showDifficultyDialog() {
        final String[] levels = {"⚡ Normal", "💀 Hardcore"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.FuturisticDialogTheme);
        builder.setTitle("⚔️ Sélection du mode de difficulté");
        builder.setItems(levels, (dialog, which) -> {
            String selected = levels[which];
            Toast.makeText(this, "Mode choisi : " + selected, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("difficulty", selected);
            startActivity(intent);
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}


