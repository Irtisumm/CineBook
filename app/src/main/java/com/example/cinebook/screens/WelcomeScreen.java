package com.example.cinebook.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.signin.SignInActivity;

public class WelcomeScreen extends AppCompatActivity {

    private static final String TAG = "WelcomeScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        // Initialize views
        ImageView backgroundImageView = findViewById(R.id.background_image);
        LinearLayout continueWithEmailButton = findViewById(R.id.email_button);
        LinearLayout signInButton = findViewById(R.id.signin_container);

        // Load background image using Glide
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/vavipnbu_expires_30_days.png")
                .placeholder(R.drawable.placeholder_background)
                .error(R.drawable.error_image)
                .centerCrop()
                .into(backgroundImageView);

        // Set click listener for "Continue with email"
        continueWithEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Continue with email pressed");
                Intent intent = new Intent(WelcomeScreen.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        // Set click listener for "Sign in"
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Sign in pressed");
                Intent intent = new Intent(WelcomeScreen.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }
}