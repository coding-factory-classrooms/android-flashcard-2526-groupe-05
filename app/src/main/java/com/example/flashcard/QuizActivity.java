package com.example.flashcard;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class QuizActivity extends AppCompatActivity {

    private static final int pauseTimeMs = 7000; // 7 secondes
    private VideoView videoView;
    private RadioGroup radioGroup;
    private Button validateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);

        // Edge-to-edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        videoView = findViewById(R.id.videoView);
        radioGroup = findViewById(R.id.quizRadioGroup);
        validateButton = findViewById(R.id.validateButton);

        // cacher les éléments au départ
        radioGroup.setVisibility(View.GONE);
        validateButton.setVisibility(View.GONE);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sushi);
        videoView.setVideoURI(videoUri);
        videoView.start();

        // ⏸ Pause automatique à pauseTimeMs
        new Handler().postDelayed(() -> {
            if (videoView.isPlaying()) {
                videoView.pause();
                radioGroup.setVisibility(View.VISIBLE);
                validateButton.setVisibility(View.VISIBLE);
            }
        }, pauseTimeMs);


        validateButton.setOnClickListener(v -> {

            int checkedId = radioGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {

                return;
            }
            RadioButton selected = findViewById(checkedId);
            String answer = selected.getText().toString();


            radioGroup.setVisibility(View.GONE);
            validateButton.setVisibility(View.GONE);


            videoView.start();
        });
    }
}
