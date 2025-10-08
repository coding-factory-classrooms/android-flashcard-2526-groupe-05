package com.example.flashcard;
import android.app.Dialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
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

            Button testQuizButton = findViewById(R.id.testQuizButton);
            testQuizButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            });
            Button aboutButton = findViewById(R.id.aboutButton);
            aboutButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            });

//            InputStream inputStream = getResources().openRawResource(R.raw.questions);
//            Reader reader = new InputStreamReader(inputStream);
//            Gson gson = new Gson();
//            Type listType = new TypeToken<List<Question>>(){}.getType();
//            List<Question> questionList = gson.fromJson(reader, listType);

            return insets;
        });
    }

    private void showDifficultyDialog() {
        /*final String[] levels = {"Normal", "Hardcore"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisis ton niveau de difficulté");
        builder.setItems(levels, (dialog, which) -> {
            String selected = levels[which];
            Toast.makeText(this, "Niveau choisi : " + selected, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, QuizActivity.class);
            intent.putExtra("difficulty", selected);
            ArrayList<Question> questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
            startActivity(intent);
        });
        builder.setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss());
        builder.show();*/


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