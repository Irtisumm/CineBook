package com.example.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class Terms extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);

        // Initialize views
        ImageView backButton = findViewById(R.id.back_button);

        // Set click listener
        backButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent());
            finish();
        });
    }
}