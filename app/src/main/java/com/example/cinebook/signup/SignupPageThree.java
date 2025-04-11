package com.example.cinebook.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;
import com.example.cinebook.signin.SignInActivity;

public class SignupPageThree extends AppCompatActivity {

    private EditText editTextPassword, editTextConfirmPassword;
    private ImageView imageViewPasswordVisibility, imageViewConfirmPasswordVisibility;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page_three);

        // Initialize views
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirmPassword);
        imageViewPasswordVisibility = findViewById(R.id.imageView_passwordVisibility);
        imageViewConfirmPasswordVisibility = findViewById(R.id.imageView_confirmPasswordVisibility);

        LinearLayout linearLayoutContinueButton = findViewById(R.id.linearLayout_continueButton);

        // Continue button click listener
        linearLayoutContinueButton.setOnClickListener(v -> {
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextConfirmPassword.getText().toString().trim();

            // Validate passwords
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 8 characters long and include caps, numbers, or symbols.", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to SignInActivity
                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignupPageThree.this, SignInActivity.class);
                startActivity(intent);
                finish(); // Prevent going back to SignupPageThree
            }
        });

        // Toggle password visibility
        imageViewPasswordVisibility.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            togglePasswordVisibility(editTextPassword, isPasswordVisible);
        });

        imageViewConfirmPasswordVisibility.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            togglePasswordVisibility(editTextConfirmPassword, isConfirmPasswordVisible);
        });
    }

    private void togglePasswordVisibility(EditText editText, boolean isVisible) {
        if (isVisible) {
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        editText.setSelection(editText.getText().length()); // Move cursor to the end
    }

    private boolean isValidPassword(String password) {
        // Check if the password is at least 8 characters long and contains a combination of uppercase, lowercase, numbers, and symbols
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") && // At least one uppercase letter
                password.matches(".*[a-z].*") && // At least one lowercase letter
                password.matches(".*\\d.*") && // At least one number
                password.matches(".*[@#$%^&+=!].*"); // At least one special character
    }
}