package com.example.cinebook.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cinebook.R;

public class LoadingActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView loadingMessage;
    private TextView loadingSubtitle;
    private ImageView loadingLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // Initialize views
        loadingLogo = findViewById(R.id.loading_logo);
        progressBar = findViewById(R.id.loading_progress_bar);
        loadingMessage = findViewById(R.id.loading_message);
        loadingSubtitle = findViewById(R.id.loading_subtitle);

        // Simulate app initialization (replace with actual task)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Navigate to WelcomeScreen
            Intent intent = new Intent(LoadingActivity.this, WelcomeScreen.class);
            startActivity(intent);
            finish(); // Close LoadingActivity to prevent back navigation
        }, 3000); // 3-second delay for demo
    }
}