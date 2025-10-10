package com.example.flashcard.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QuestionLoader {

    /**
     * Loads questions from a JSON file located in the assets folder.
     *
     * @param context  The app context used to access assets.
     * @param fileName The name of the JSON file to load.
     * @return A list of Question objects parsed from the file.
     */
    public static ArrayList<Question> loadFromJson(Context context, String fileName) {
        ArrayList<Question> questions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(context.getAssets().open(fileName)))
        ) {
            // Read the entire file line by line into a StringBuilder
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) jsonBuilder.append(line);

            // Convert the built JSON string into an array of JSON objects
            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                // Extract options 
                ArrayList<String> options = new ArrayList<>();
                JSONArray optionsArray = obj.getJSONArray("options");
                for (int j = 0; j < optionsArray.length(); j++) {
                    options.add(optionsArray.getString(j));
                }

                // Create a new Question object using the parsed data
                questions.add(new Question(
                        obj.getString("videoPath"),
                        obj.getString("questionText"),
                        options,
                        obj.getString("correctAnswer"),
                        obj.getInt("pauseTimeMs")
                ));
            }

        } catch (Exception e) {
            Log.e("QuestionLoader", "Error reading JSON: " + e.getMessage());
        }

        return questions;
    }
}
