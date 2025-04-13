package com.example.cinebook.signup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class SignupPageOne extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName;
    private LinearLayout linearLayoutContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page_one);

        // Initialize views
        editTextFirstName = findViewById(R.id.editText_firstName);
        editTextLastName = findViewById(R.id.editText_lastName);
        linearLayoutContinueButton = findViewById(R.id.linearLayout_continueButton);

        // Add functionality to the continue button
        linearLayoutContinueButton.setOnClickListener(v -> {
            String firstName = editTextFirstName.getText().toString().trim();
            String lastName = editTextLastName.getText().toString().trim();

            // Validate input fields
            if (firstName.isEmpty() || lastName.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to SignUpPageTwo
                Intent intent = new Intent(SignupPageOne.this, SignUpPageTwo.class);
                intent.putExtra("FIRST_NAME", firstName);
                intent.putExtra("LAST_NAME", lastName);
                startActivity(intent);
            }
        });
    }
}