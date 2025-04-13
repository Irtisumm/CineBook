package com.example.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class Security extends AppCompatActivity {

    private static final String PREFS_NAME = "CinebookPrefs";
    private static final String KEY_PASSWORD = "user_password";

    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        ImageView backButton = findViewById(R.id.back_button);
        LinearLayout passwordOptionContainer = findViewById(R.id.password_option_container);

        // Set click listeners
        backButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent());
            finish();
        });

        passwordOptionContainer.setOnClickListener(v -> showUpdatePasswordDialog());
    }

    private void showUpdatePasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Password");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_update_password, null);
        builder.setView(dialogView);

        EditText currentPasswordInput = dialogView.findViewById(R.id.current_password_input);
        EditText newPasswordInput = dialogView.findViewById(R.id.new_password_input);
        EditText confirmPasswordInput = dialogView.findViewById(R.id.confirm_password_input);

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String currentPassword = currentPasswordInput.getText().toString().trim();
            String newPassword = newPasswordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            // Validation
            String storedPassword = prefs.getString(KEY_PASSWORD, "default123"); // Default for testing
            if (!currentPassword.equals(storedPassword)) {
                currentPasswordInput.setError("Incorrect current password");
                return;
            }
            if (newPassword.length() < 6) {
                newPasswordInput.setError("Password must be at least 6 characters");
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                confirmPasswordInput.setError("Passwords do not match");
                return;
            }

            // Save new password
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_PASSWORD, newPassword);
            editor.apply();

            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }
}