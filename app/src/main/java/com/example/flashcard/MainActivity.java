package com.example.flashcard;

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

            InputStream inputStream = getResources().openRawResource(R.raw.questions);
            Reader reader = new InputStreamReader(inputStream);
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Question>>(){}.getType();
            List<Question> questionList = gson.fromJson(reader, listType);

            return insets;
        });
    }

    private void showDifficultyDialog() {
        final String[] levels = {"Normal", "Hardcore"};

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
        builder.show();
    }
}