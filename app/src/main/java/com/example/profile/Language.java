package com.example.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class Language extends AppCompatActivity {

    private static final String PREFS_NAME = "CinebookPrefs";
    private static final String KEY_LANGUAGE = "selected_language";
    private static final int LANGUAGE_REQUEST_CODE = 101;

    private LinearLayout languageEnglishUs;
    private LinearLayout languageSpanish;
    private LinearLayout languageFrench;
    private ImageView checkEnglishUs;
    private ImageView checkSpanish;
    private ImageView checkFrench;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        ImageView backButton = findViewById(R.id.back_button);
        languageEnglishUs = findViewById(R.id.language_english_us);
        languageSpanish = findViewById(R.id.language_spanish);
        languageFrench = findViewById(R.id.language_french);
        checkEnglishUs = findViewById(R.id.check_english_us);
        checkSpanish = findViewById(R.id.check_spanish);
        checkFrench = findViewById(R.id.check_french);

        // Load saved language
        loadSelectedLanguage();

        // Set click listeners
        backButton.setOnClickListener(v -> finish());

        languageEnglishUs.setOnClickListener(v -> selectLanguage("English (US)", checkEnglishUs));
        languageSpanish.setOnClickListener(v -> selectLanguage("Spanish", checkSpanish));
        languageFrench.setOnClickListener(v -> selectLanguage("French", checkFrench));
    }

    private void loadSelectedLanguage() {
        String selectedLanguage = prefs.getString(KEY_LANGUAGE, "English (US)"); // Default to English
        updateCheckmark(selectedLanguage);
    }

    private void selectLanguage(String language, ImageView selectedCheck) {
        // Update SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LANGUAGE, language);
        editor.apply();

        // Update UI
        updateCheckmark(language);

        // Show confirmation
        Toast.makeText(this, language + " selected", Toast.LENGTH_SHORT).show();

        // Return result to ProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("selectedLanguage", language);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void updateCheckmark(String selectedLanguage) {
        // Reset all checkmarks
        checkEnglishUs.setVisibility(View.GONE);
        checkSpanish.setVisibility(View.GONE);
        checkFrench.setVisibility(View.GONE);

        // Show checkmark for selected language
        switch (selectedLanguage) {
            case "English (US)":
                checkEnglishUs.setVisibility(View.VISIBLE);
                break;
            case "Spanish":
                checkSpanish.setVisibility(View.VISIBLE);
                break;
            case "French":
                checkFrench.setVisibility(View.VISIBLE);
                break;
        }
    }
}