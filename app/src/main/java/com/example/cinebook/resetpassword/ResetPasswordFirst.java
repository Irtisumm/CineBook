package com.example.cinebook.resetpassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ResetPasswordFirst extends AppCompatActivity {

    private EditText editTextEmailInput;
    private LinearLayout linearLayoutContinueButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_1_reset_password);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        editTextEmailInput = findViewById(R.id.editText_emailInput);
        linearLayoutContinueButton = findViewById(R.id.linearLayout_continueButton);

        // Add functionality to the continue button
        linearLayoutContinueButton.setOnClickListener(v -> {
            String email = editTextEmailInput.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                // Show error if email is empty
                Toast.makeText(this, "Please enter your email address", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                // Show error if email format is invalid
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            } else {
                // Send password reset email using Firebase
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Email sent successfully
                                Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_SHORT).show();
                                // Navigate to ResetPasswordTwo
                                Intent intent = new Intent(ResetPasswordFirst.this, ResetPasswordTwo.class);
                                intent.putExtra("EMAIL", email); // Optional: Pass email for confirmation
                                startActivity(intent);
                                finish(); // Optional: Close this activity
                            } else {
                                // Handle specific errors
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(this, "No account found with this email", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Log.e("ResetPasswordFirst", "Failed to send reset email: " + e.getMessage(), e);
                                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    // Utility method to validate email format
    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}