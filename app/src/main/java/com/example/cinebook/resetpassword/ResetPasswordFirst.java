package com.example.cinebook.resetpassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class ResetPasswordFirst extends AppCompatActivity {

    private EditText editTextEmailInput;
    private LinearLayout linearLayoutContinueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_1_reset_password);

        // Initialize views
        editTextEmailInput = findViewById(R.id.editText_emailInput);
        linearLayoutContinueButton = findViewById(R.id.linearLayout_continueButton);

        // Add functionality to the continue button
        linearLayoutContinueButton.setOnClickListener(v -> {
            String email = editTextEmailInput.getText().toString();

            if (TextUtils.isEmpty(email)) {
                // Show error if email is empty
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                // Show error if email format is invalid
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else {
                // Perform action to send reset link to email
                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();

                // Navigate to the next screen (e.g., ResetPasswordTwo)
                Intent intent = new Intent(ResetPasswordFirst.this, ResetPasswordTwo.class);
                startActivity(intent);
            }
        });
    }

    // Utility method to validate email format
    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}