package com.example.flashcard;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recyclerView), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView appName = findViewById(R.id.appName);
        TextView groupName = findViewById(R.id.groupName);
        TextView versionName = findViewById(R.id.versionName);

        appName.setText("Caca Pipi Fesses, la meilleur app de quiz du monde");
        groupName.setText("Killian, Mathéo, Raphaël, Lenny");

        // Récupération dynamique du numéro de version
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            versionName.setText("Version : inconnue");
        }
    }
}