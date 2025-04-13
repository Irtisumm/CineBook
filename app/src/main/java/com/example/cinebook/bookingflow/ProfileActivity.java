package com.example.cinebook.bookingflow;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;
import com.example.cinebook.screens.HomeScreen;
import com.example.cinebook.signin.SignInActivity;
import com.example.profile.Account;
import com.example.profile.Language;
import com.example.profile.Notification;
import com.example.profile.Payment;
import com.example.profile.Security;
import com.example.profile.Help;
import com.example.profile.Terms;
import com.example.profile.Privacy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends BaseActivity {

    private static final String PREFS_NAME = "CinebookPrefs";
    private static final String KEY_PROFILE_NAME = "profile_name";
    private static final String KEY_PROFILE_IMAGE = "profile_image";
    private static final int ACCOUNT_REQUEST_CODE = 100;
    private static final int LANGUAGE_REQUEST_CODE = 101;
    private static final int PAYMENT_REQUEST_CODE = 102;
    private static final int NOTIFICATION_REQUEST_CODE = 103;
    private static final int SECURITY_REQUEST_CODE = 104;
    private static final int HELP_REQUEST_CODE = 105;
    private static final int TERMS_REQUEST_CODE = 106;
    private static final int PRIVACY_REQUEST_CODE = 107;

    private ImageView profileImage;
    private TextView profileNameTextView;
    private SharedPreferences prefs;
    private String currentPhotoPath;

    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Setup bottom navigation using BaseActivity's method
        setupBottomNavigation(R.id.nav_profile);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        profileImage = findViewById(R.id.profile_image);
        profileNameTextView = findViewById(R.id.profile_name);
        LinearLayout accountOption = findViewById(R.id.account_option);
        LinearLayout paymentOption = findViewById(R.id.payment_option);
        ImageView paymentArrow = findViewById(R.id.payment_arrow);
        LinearLayout notificationsOption = findViewById(R.id.notifications_option);
        ImageView notificationsArrow = findViewById(R.id.notifications_arrow);
        LinearLayout languageOption = findViewById(R.id.language_option);
        ImageView languageArrow = findViewById(R.id.language_arrow);
        LinearLayout securityOption = findViewById(R.id.security_option);
        ImageView securityArrow = findViewById(R.id.security_arrow);
        LinearLayout helpOption = findViewById(R.id.help_option);
        ImageView helpArrow = findViewById(R.id.help_arrow);
        LinearLayout termsOption = findViewById(R.id.terms_option);
        ImageView termsArrow = findViewById(R.id.terms_arrow);
        LinearLayout privacyOption = findViewById(R.id.privacy_option);
        ImageView privacyArrow = findViewById(R.id.privacy_arrow);
        LinearLayout logoutOption = findViewById(R.id.logout_section);

        // Load saved profile data
        loadProfileData();

        // Initialize ActivityResultLaunchers
        initImageLaunchers();
        initPermissionLauncher();

        // Set click listeners
        profileImage.setOnClickListener(v -> showImagePickerDialog());
        profileNameTextView.setOnClickListener(v -> showEditNameDialog());
        accountOption.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Account.class);
            startActivityForResult(intent, ACCOUNT_REQUEST_CODE);
        });
        paymentArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Payment.class);
            startActivityForResult(intent, PAYMENT_REQUEST_CODE);
        });
        notificationsArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Notification.class);
            startActivityForResult(intent, NOTIFICATION_REQUEST_CODE);
        });
        languageArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Language.class);
            startActivityForResult(intent, LANGUAGE_REQUEST_CODE);
        });
        securityArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Security.class);
            startActivityForResult(intent, SECURITY_REQUEST_CODE);
        });
        helpArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Help.class);
            startActivityForResult(intent, HELP_REQUEST_CODE);
        });
        termsArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Terms.class);
            startActivityForResult(intent, TERMS_REQUEST_CODE);
        });
        privacyArrow.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, Privacy.class);
            startActivityForResult(intent, PRIVACY_REQUEST_CODE);
        });
        logoutOption.setOnClickListener(v -> logout());
    }

    private void loadProfileData() {
        String savedName = prefs.getString(KEY_PROFILE_NAME, "User Name");
        profileNameTextView.setText(savedName);

        String imagePath = prefs.getString(KEY_PROFILE_IMAGE, null);
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                profileImage.setImageBitmap(bitmap);
            }
        }
    }

    private void initImageLaunchers() {
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    saveImageToStorage(bitmap);
                    profileImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    showToast("Error loading image");
                }
            }
        });

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                if (bitmap != null) {
                    saveImageToStorage(bitmap);
                    profileImage.setImageBitmap(bitmap);
                } else {
                    showToast("Error capturing image");
                }
            }
        });
    }

    private void initPermissionLauncher() {
        requestCameraPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                dispatchTakePictureIntent();
            } else {
                showToast("Camera permission denied");
            }
        });
    }

    private void showImagePickerDialog() {
        String[] options = {"Take Photo", "Choose from Gallery"};
        new AlertDialog.Builder(this)
                .setTitle("Update Profile Photo")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                        } else {
                            dispatchTakePictureIntent();
                        }
                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryLauncher.launch(intent);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                showToast("Error creating image file");
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.cinebook.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void saveImageToStorage(Bitmap bitmap) {
        try {
            File file = createImageFile();
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(KEY_PROFILE_IMAGE, file.getAbsolutePath());
            editor.apply();
        } catch (IOException e) {
            showToast("Error saving image");
        }
    }

    private void showEditNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Profile Name");

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_name, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.edit_name);
        nameEditText.setText(profileNameTextView.getText().toString());

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String newName = nameEditText.getText().toString().trim();
            if (newName.isEmpty()) {
                nameEditText.setError("Name cannot be empty");
            } else {
                profileNameTextView.setText(newName);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(KEY_PROFILE_NAME, newName);
                editor.apply();
                showToast("Name updated");
                dialog.dismiss();
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    showToast("Logged out");
                    Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            loadProfileData();
        } else if (requestCode == LANGUAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            String selectedLanguage = data.getStringExtra("selectedLanguage");
            if (selectedLanguage != null) {
                showToast("Language set to " + selectedLanguage);
            }
        } else if (requestCode == PAYMENT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            showToast("Payment methods updated");
        } else if (requestCode == NOTIFICATION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            showToast("Notification settings updated");
        } else if (requestCode == SECURITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            showToast("Security settings updated");
        } else if (requestCode == HELP_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            showToast("Help Center viewed");
        } else if (requestCode == TERMS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            showToast("Terms and Conditions viewed");
        } else if (requestCode == PRIVACY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            showToast("Privacy Policy viewed");
        }
    }
}