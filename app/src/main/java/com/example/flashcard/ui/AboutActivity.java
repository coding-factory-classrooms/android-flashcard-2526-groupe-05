package com.example.flashcard.ui;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.flashcard.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Gestion des insets (bordures système)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Références UI
        TextView appName = findViewById(R.id.appName);
        TextView groupName = findViewById(R.id.groupName);
        TextView versionName = findViewById(R.id.versionName);
        TextView aboutDescription = findViewById(R.id.aboutDescription);
        Button backButton = findViewById(R.id.backButton);

        // Contenu
        appName.setText("FlashLearn Pro");
        groupName.setText("Killian, Mathéo, Raphaël, Lenny");
        aboutDescription.setText("Apprenez rapidement avec FlashLearn grâce aux flashcards interactives. Testez vos connaissances, suivez vos progrès et progressez chaque jour !");

        // Version dynamique
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName.setText("Version " + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionName.setText("Version inconnue");
        }

        // Bouton retour
        backButton.setOnClickListener(v -> finish()); // ferme l'activité et retourne au menu
    }
}
