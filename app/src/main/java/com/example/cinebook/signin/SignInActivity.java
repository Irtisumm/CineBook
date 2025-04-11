package com.example.cinebook.signin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;
import com.example.cinebook.resetpassword.ResetPasswordFirst;
import com.example.cinebook.signup.SignupPageOne;
import com.example.cinebook.screens.HomeScreen;

public class SignInActivity extends AppCompatActivity {

    EditText emailEditText, passwordEditText;
    LinearLayout loginButtonLayout;
    TextView signUpPrompt, forgotPasswordText;
    ImageView passwordToggleIcon;

    boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButtonLayout = findViewById(R.id.loginButtonLayout);
        signUpPrompt = findViewById(R.id.signUpPrompt);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        passwordToggleIcon = findViewById(R.id.passwordToggleIcon);

        // Handle "Forgot Password?" click
        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, ResetPasswordFirst.class);
            startActivity(intent);
        });

        // Handle "Login" button click
        loginButtonLayout.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            } else {
                // Navigate to HomeScreen
                Intent intent = new Intent(SignInActivity.this, HomeScreen.class);
                startActivity(intent);
                finish(); // Close the current activity to prevent going back to it
            }
        });

        // Handle "Sign Up" click
        signUpPrompt.setOnClickListener(v -> {
            // Navigate to SignupPageOne
            Intent intent = new Intent(SignInActivity.this, SignupPageOne.class);
            startActivity(intent);
        });

        // Handle password visibility toggle
        passwordToggleIcon.setOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            passwordEditText.setInputType(passwordVisible ?
                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
                    android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setSelection(passwordEditText.getText().length());
        });
    }
}