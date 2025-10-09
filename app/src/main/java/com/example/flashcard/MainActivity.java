package com.example.flashcard; // package declaration

import java.util.ArrayList; // unused import

// Android / Jetpack imports
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge; // enable edge-to-edge layout
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity"; // tag for logging

    private Button startQuizBtn; // reference to the Start Quiz button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // call parent method
        EdgeToEdge.enable(this); // enable edge-to-edge display
        setContentView(R.layout.activity_main); // set XML layout

        // Adjust padding for system bars and initialize buttons
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            // Initialize Start Quiz button and set click listener
            startQuizBtn = findViewById(R.id.startQuizBtn);
            startQuizBtn.setOnClickListener(view -> showDifficultyDialog());

            // Initialize About button and set click listener
            Button aboutButton = findViewById(R.id.aboutButton);
            aboutButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            });

            return insets; // pass insets through
        });
    }

    // Show a dialog to select difficulty, then start QuizActivity
    private void showDifficultyDialog() {
        final String[] levels = {"⚡ Normal", "💀 Hardcore", "⏱ Time Attack"}; // added Time Attack

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.FuturisticDialogTheme);
        builder.setTitle("⚔️ Sélection du mode de difficulté"); // dialog title
        builder.setItems(levels, (dialog, which) -> {
            String selected = levels[which]; // get selected level
            Toast.makeText(this, "Mode choisi : " + selected, Toast.LENGTH_SHORT).show(); // show toast

            // Start QuizActivity with selected difficulty
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("difficulty", selected);
            startActivity(intent);
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss()); // cancel button
        builder.show(); // show the dialog
    }
}
