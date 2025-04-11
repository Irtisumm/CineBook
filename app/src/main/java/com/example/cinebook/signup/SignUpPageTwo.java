package com.example.cinebook.signup;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class SignUpPageTwo extends AppCompatActivity {

    private EditText editTextEmail;
    private LinearLayout linearLayoutContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page_two);

        // Initialize views
        editTextEmail = findViewById(R.id.editText_email);
        linearLayoutContinueButton = findViewById(R.id.linearLayout_continueButton);

        // Add functionality to the continue button
        linearLayoutContinueButton.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();

            // Validate email address
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter an email address", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to SignupPageThree
                Intent intent = new Intent(SignUpPageTwo.this, SignupPageThree.class);
                intent.putExtra("EMAIL", email); // Pass email to the next page if needed
                startActivity(intent);
            }
        });
    }
}