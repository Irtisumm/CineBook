package com.example.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;

public class Notification extends AppCompatActivity {

    private static final String PREFS_NAME = "CinebookPrefs";
    private static final String KEY_ORDER_CONFIRMATION = "notification_order_confirmation";
    private static final String KEY_SCHEDULE_REMINDER = "notification_schedule_reminder";
    private static final String KEY_PROMOTIONS = "notification_promotions";

    private SharedPreferences prefs;
    private boolean isOrderConfirmationEnabled;
    private boolean isScheduleReminderEnabled;
    private boolean isPromotionsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved toggle states
        isOrderConfirmationEnabled = prefs.getBoolean(KEY_ORDER_CONFIRMATION, true);
        isScheduleReminderEnabled = prefs.getBoolean(KEY_SCHEDULE_REMINDER, true);
        isPromotionsEnabled = prefs.getBoolean(KEY_PROMOTIONS, false);

        // Initialize views
        ImageView backButton = findViewById(R.id.back_button);
        ImageView orderConfirmationToggle = findViewById(R.id.order_confirmation_toggle);
        ImageView scheduleReminderToggle = findViewById(R.id.schedule_reminder_toggle);
        ImageView promotionsToggle = findViewById(R.id.promotions_toggle);

        // Set initial toggle states
        updateToggleImage(orderConfirmationToggle, isOrderConfirmationEnabled);
        updateToggleImage(scheduleReminderToggle, isScheduleReminderEnabled);
        updateToggleImage(promotionsToggle, isPromotionsEnabled);

        // Set click listeners
        backButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent());
            finish();
        });

        orderConfirmationToggle.setOnClickListener(v -> {
            isOrderConfirmationEnabled = !isOrderConfirmationEnabled;
            updateToggleImage(orderConfirmationToggle, isOrderConfirmationEnabled);
            saveToggleState(KEY_ORDER_CONFIRMATION, isOrderConfirmationEnabled);
            Toast.makeText(this, "Order confirmation " + (isOrderConfirmationEnabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        scheduleReminderToggle.setOnClickListener(v -> {
            isScheduleReminderEnabled = !isScheduleReminderEnabled;
            updateToggleImage(scheduleReminderToggle, isScheduleReminderEnabled);
            saveToggleState(KEY_SCHEDULE_REMINDER, isScheduleReminderEnabled);
            Toast.makeText(this, "Schedule reminder " + (isScheduleReminderEnabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });

        promotionsToggle.setOnClickListener(v -> {
            isPromotionsEnabled = !isPromotionsEnabled;
            updateToggleImage(promotionsToggle, isPromotionsEnabled);
            saveToggleState(KEY_PROMOTIONS, isPromotionsEnabled);
            Toast.makeText(this, "Promotions " + (isPromotionsEnabled ? "enabled" : "disabled"), Toast.LENGTH_SHORT).show();
        });
    }

    private void updateToggleImage(ImageView toggle, boolean isEnabled) {
        toggle.setImageResource(isEnabled ? R.drawable.ic_toggle_on : R.drawable.ic_toggle_on);
    }

    private void saveToggleState(String key, boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}