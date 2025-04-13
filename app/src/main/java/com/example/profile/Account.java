package com.example.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Account extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "CinebookPrefs"; // Same as ProfileActivity
    private static final String KEY_PROFILE_NAME = "profile_name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";

    private ImageView backButton;
    private ImageView profileImage;
    private ImageView editImageButton;
    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText emailInput;
    private Button saveButton;
    private SharedPreferences prefs;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        backButton = findViewById(R.id.back_button);
        profileImage = findViewById(R.id.profile_image);
        editImageButton = findViewById(R.id.edit_image_button);
        firstNameInput = findViewById(R.id.first_name_input);
        lastNameInput = findViewById(R.id.last_name_input);
        emailInput = findViewById(R.id.email_input);
        saveButton = findViewById(R.id.save_button);

        // Load existing data
        loadProfileData();

        // Set click listeners
        backButton.setOnClickListener(v -> finish());
        editImageButton.setOnClickListener(v -> openImagePicker());
        saveButton.setOnClickListener(v -> saveProfileChanges());
    }

    private void loadProfileData() {
        String savedName = prefs.getString(KEY_PROFILE_NAME, "");
        if (!savedName.isEmpty()) {
            String[] names = savedName.split(" ");
            firstNameInput.setText(names.length > 0 ? names[0] : "");
            lastNameInput.setText(names.length > 1 ? names[1] : "");
        }
        String imagePath = prefs.getString(KEY_PROFILE_IMAGE, null);
        if (imagePath != null) {
            profileImage.setImageURI(Uri.parse(imagePath));
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            currentPhotoPath = saveImageToStorage(imageUri);
        }
    }

    private String saveImageToStorage(Uri imageUri) {
        try {
            File storageDir = getExternalFilesDir(null);
            File imageFile = File.createTempFile("profile_", ".jpg", storageDir);
            FileOutputStream fos = new FileOutputStream(imageFile);
            MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri)
                    .compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void saveProfileChanges() {
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();

        // Basic validation
        if (firstName.isEmpty()) {
            firstNameInput.setError("First name is required");
            return;
        }
        if (lastName.isEmpty()) {
            lastNameInput.setError("Last name is required");
            return;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid email is required");
            return;
        }

        // Combine first and last name
        String fullName = firstName + " " + lastName;

        // Save to SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PROFILE_NAME, fullName);
        if (currentPhotoPath != null) {
            editor.putString(KEY_PROFILE_IMAGE, currentPhotoPath);
        }
        editor.apply();

        // Return data to ProfileActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("fullName", fullName);
        resultIntent.putExtra("imagePath", currentPhotoPath);
        resultIntent.putExtra("email", email);
        setResult(RESULT_OK, resultIntent);

        // Show success message and navigate back
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
}