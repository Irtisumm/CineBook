package com.example.cinebook.signup;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;
import com.example.cinebook.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupPageThree extends AppCompatActivity {

    private EditText editTextPassword, editTextConfirmPassword;
    private ImageView imageViewPasswordVisibility, imageViewConfirmPasswordVisibility;
    private LinearLayout linearLayoutContinueButton;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page_three);

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Optional: Connect to Firestore Emulator for debugging
        // if (BuildConfig.DEBUG) {
        //     db.useEmulator("10.0.2.2", 8080);
        // }

        // Initialize views
        editTextPassword = findViewById(R.id.editText_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirmPassword);
        imageViewPasswordVisibility = findViewById(R.id.imageView_passwordVisibility);
        imageViewConfirmPasswordVisibility = findViewById(R.id.imageView_confirmPasswordVisibility);
        linearLayoutContinueButton = findViewById(R.id.linearLayout_continueButton);

        // Retrieve data from previous pages
        Intent intent = getIntent();
        String firstName = intent.getStringExtra("FIRST_NAME");
        String lastName = intent.getStringExtra("LAST_NAME");
        String email = intent.getStringExtra("EMAIL");

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
                Toast.makeText(this, "Password must be at least 8 characters long and include caps, numbers, and symbols.", Toast.LENGTH_SHORT).show();
            } else {
                // Create user with Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // User created, now store data in Firestore
                                String userId = mAuth.getCurrentUser().getUid();
                                Map<String, Object> user = new HashMap<>();
                                user.put("firstName", firstName);
                                user.put("lastName", lastName);
                                user.put("email", email);

                                db.collection("users").document(userId)
                                        .set(user)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("SignupPageThree", "User data saved successfully");
                                            Toast.makeText(SignupPageThree.this, "User data saved!", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("SignupPageThree", "Firestore write failed: " + e.getMessage(), e);
                                            Toast.makeText(SignupPageThree.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            // Optional: Delete user if Firestore fails
                                            // mAuth.getCurrentUser().delete();
                                        });

                                // Navigate to SignInActivity
                                Toast.makeText(SignupPageThree.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                                Intent signInIntent = new Intent(SignupPageThree.this, SignInActivity.class);
                                startActivity(signInIntent);
                                finish();
                            } else {
                                Log.e("SignupPageThree", "Auth failed: " + task.getException().getMessage(), task.getException());
                                Toast.makeText(SignupPageThree.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
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
        editText.setSelection(editText.getText().length());
    }

    private boolean isValidPassword(String password) {
        return password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&
                password.matches(".*[a-z].*") &&
                password.matches(".*\\d.*") &&
                password.matches(".*[@#$%^&+=!].*");
    }
}