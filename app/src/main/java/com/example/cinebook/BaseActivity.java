package com.example.cinebook;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cinebook.bookingflow.BookingConfirmationActivity;
import com.example.cinebook.bookingflow.ProfileActivity;
import com.example.cinebook.bookingflow.TheaterActivity;
import com.example.cinebook.screens.HomeScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected BottomNavigationView bottomNavigationView;
    private long lastClickTime = 0;
    private static final long CLICK_DEBOUNCE_TIME = 1000; // 1 second

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomNavigation(int selectedItemId) {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            try {
                bottomNavigationView.setSelectedItemId(selectedItemId);
            } catch (Exception e) {
                Log.e("BaseActivity", "Invalid menu item ID: " + selectedItemId, e);
            }
            bottomNavigationView.setOnItemSelectedListener(item -> {
                // Debounce clicks
                long currentTime = SystemClock.elapsedRealtime();
                if (currentTime - lastClickTime < CLICK_DEBOUNCE_TIME) {
                    Log.d("BaseActivity", "Click debounced for item: " + item.getItemId());
                    return false;
                }
                lastClickTime = currentTime;

                int itemId = item.getItemId();
                Log.d("BaseActivity", "Nav item clicked: " + itemId);
                if (itemId == R.id.nav_home) {
                    if (!(this instanceof HomeScreen)) {
                        startActivity(new Intent(this, HomeScreen.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_theater) {
                    if (!(this instanceof TheaterActivity)) {
                        startActivity(new Intent(this, TheaterActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();
                    }
                    return true;
                } else if (itemId == R.id.nav_ticket) {
                    if (!(this instanceof BookingConfirmationActivity)) {
                        try {
                            startActivity(new Intent(this, BookingConfirmationActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                            finish();
                            Log.d("BaseActivity", "Navigating to BookingConfirmationActivity");
                        } catch (Exception e) {
                            Log.e("BaseActivity", "Failed to start BookingConfirmationActivity", e);
                            return false;
                        }
                    }
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    if (!(this instanceof ProfileActivity)) {
                        startActivity(new Intent(this, ProfileActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
                        finish();
                    }
                    return true;
                }
                Log.w("BaseActivity", "Unknown nav item: " + itemId);
                return false;
            });
        } else {
            Log.w("BaseActivity", "BottomNavigationView not found in layout");
        }
    }
}