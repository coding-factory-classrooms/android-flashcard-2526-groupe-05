package com.example.flashcard.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QuestionLoader {

    public static ArrayList<Question> loadFromJson(Context context, String fileName) {
        ArrayList<Question> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(fileName)))
        ) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonBuilder.append(line);

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                ArrayList<String> options = new ArrayList<>();
                JSONArray optionsArray = obj.getJSONArray("options");
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }

                questions.add(new Question(
                        obj.getString("videoPath"),
                        obj.getString("questionText"),
                        options,
                        obj.getString("correctAnswer"),
                        obj.getInt("pauseTimeMs")
                ));
            }

        } catch (Exception e) {
            Log.e("QuestionLoader", "Erreur lecture JSON: " + e.getMessage());
        }

        return questions;
    }
}
