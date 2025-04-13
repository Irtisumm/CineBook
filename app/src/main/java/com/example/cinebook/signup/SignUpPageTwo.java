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

        // Retrieve data from SignupPageOne
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("FIRST_NAME");
        String lastName = intent.getStringExtra("LAST_NAME");

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
                Intent nextIntent = new Intent(SignUpPageTwo.this, SignupPageThree.class);
                nextIntent.putExtra("FIRST_NAME", firstName);
                nextIntent.putExtra("LAST_NAME", lastName);
                nextIntent.putExtra("EMAIL", email);
                startActivity(nextIntent);
            }
        });
    }
}