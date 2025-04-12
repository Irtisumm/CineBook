package com.example.cinebook.bookingflow;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FiestBookingTicketFlow extends AppCompatActivity {

    private ImageView logoImage;
    private ImageView cinemaIcon;
    private ImageView screenImage;
    private LinearLayout timerButton;
    private LinearLayout continueButton;
    private LinearLayout rowK, rowJ, rowH, rowG, rowF, rowE, rowD, rowC, rowB, rowA;
    private TextView subtotalValue;
    private TextView cinemaName;
    private TextView cinemaDate;
    private TextView timerText;
    private List<TextView> selectedSeats;
    private int ticketPrice = 45000; // Price per ticket in IDR
    private int subtotal = 0;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiest_booking_ticket_flow);

        // Initialize views using findViewById
        logoImage = findViewById(R.id.logo_image);
        cinemaIcon = findViewById(R.id.cinema_icon);
        screenImage = findViewById(R.id.screen_image);
        timerButton = findViewById(R.id.timer_button);
        timerText = findViewById(R.id.timer_text);
        continueButton = findViewById(R.id.continue_button);
        rowK = findViewById(R.id.row_k);
        rowJ = findViewById(R.id.row_j);
        rowH = findViewById(R.id.row_h);
        rowG = findViewById(R.id.row_g);
        rowF = findViewById(R.id.row_f);
        rowE = findViewById(R.id.row_e);
        rowD = findViewById(R.id.row_d);
        rowC = findViewById(R.id.row_c);
        rowB = findViewById(R.id.row_b);
        rowA = findViewById(R.id.row_a);
        subtotalValue = findViewById(R.id.subtotal_value);
        cinemaName = findViewById(R.id.cinema_name);
        cinemaDate = findViewById(R.id.cinema_date);

        selectedSeats = new ArrayList<>();

        // Load images
        Glide.with(this)
                .load("https://example.com/logo.png") // Replace with your URL or drawable
                .into(logoImage);
        Glide.with(this)
                .load("https://example.com/cinema_icon.png") // Replace with your URL or drawable
                .into(cinemaIcon);
        Glide.with(this)
                .load("https://example.com/screen.png") // Replace with your URL or drawable
                .into(screenImage);

        // Retrieve data from Intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String movieTitle = extras.getString("movie_title", "Unknown Movie");
            String selectedTheater = extras.getString("selected_theater", "N/A");
            String selectedDateStr = extras.getString("selected_date", "N/A");
            String selectedShowtime = extras.getString("selected_showtime", "N/A");
            cinemaName.setText(selectedTheater);
            cinemaDate.setText(selectedDateStr + ", " + selectedShowtime);

            // Start countdown timer for showtime
            startCountdownTimer(selectedDateStr, selectedShowtime);
        } else {
            timerText.setText("N/A");
        }

        // Initialize seat grid
        setupSeatGrid();

        // Timer button
        timerButton.setOnClickListener(v -> {
            Toast.makeText(this, "Showtime: " + cinemaDate.getText(), Toast.LENGTH_SHORT).show();
        });

        // Continue button
        continueButton.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder selectedSeatsText = new StringBuilder("Selected Seats: ");
                for (TextView seat : selectedSeats) {
                    selectedSeatsText.append(seat.getText()).append(" ");
                }
                selectedSeatsText.append("\nSubtotal: IDR ").append(subtotal);
                Toast.makeText(this, selectedSeatsText.toString(), Toast.LENGTH_LONG).show();
                // Proceed to next screen or payment
            }
        });
    }

    private void startCountdownTimer(String selectedDate, String selectedShowtime) {
        try {
            // Parse date and showtime (e.g., "12 Sep" and "14:45")
            SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM, HH:mm", Locale.getDefault());
            String dateTimeStr = selectedDate + ", " + selectedShowtime;
            Date showtimeDate = dateFormat.parse(dateTimeStr);

            // Get current time
            long currentTimeMillis = System.currentTimeMillis();
            long showtimeMillis = showtimeDate.getTime();
            long timeRemainingMillis = showtimeMillis - currentTimeMillis;

            if (timeRemainingMillis > 0) {
                // Start countdown timer
                countDownTimer = new CountDownTimer(timeRemainingMillis, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        long minutes = (millisUntilFinished / 1000) / 60;
                        long seconds = (millisUntilFinished / 1000) % 60;
                        timerText.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
                    }

                    @Override
                    public void onFinish() {
                        timerText.setText("00:00");
                    }
                }.start();
            } else {
                timerText.setText("Showtime Passed");
            }
        } catch (ParseException e) {
            timerText.setText("N/A");
            e.printStackTrace();
        }
    }

    private void setupSeatGrid() {
        // Define seat rows
        LinearLayout[] seatRows = { rowK, rowJ, rowH, rowG, rowF, rowE, rowD, rowC, rowB, rowA };

        // Define resource arrays for labels and states
        int[] labelArrays = {
                R.array.row_k_labels, R.array.row_j_labels, R.array.row_h_labels,
                R.array.row_g_labels, R.array.row_f_labels, R.array.row_e_labels,
                R.array.row_d_labels, R.array.row_c_labels, R.array.row_b_labels,
                R.array.row_a_labels
        };
        int[] stateArrays = {
                R.array.row_k_states, R.array.row_j_states, R.array.row_h_states,
                R.array.row_g_states, R.array.row_f_states, R.array.row_e_states,
                R.array.row_d_states, R.array.row_c_states, R.array.row_b_states,
                R.array.row_a_states
        };

        for (int i = 0; i < seatRows.length; i++) {
            LinearLayout row = seatRows[i];
            String[] labels = getResources().getStringArray(labelArrays[i]);
            String[] states = getResources().getStringArray(stateArrays[i]);

            // Clear existing views
            row.removeAllViews();

            // Populate row dynamically
            for (int j = 0; j < labels.length; j++) {
                String label = labels[j];
                String state = states[j];

                LinearLayout seatLayout = new LinearLayout(this);
                seatLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                seatLayout.setOrientation(LinearLayout.VERTICAL);
                seatLayout.setPadding(8, 8, 8, 8); // Increased padding for larger appearance
                if (j < labels.length - 1) {
                    seatLayout.setPadding(8, 8, 16, 8); // Larger marginEnd
                }

                if (!label.isEmpty()) {
                    TextView seatText = new TextView(this);
                    seatText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    seatText.setText(label);
                    seatText.setTextColor(getResources().getColor(android.R.color.white));
                    seatText.setTextSize(16); // Increased text size
                    seatText.setPadding(12, 8, 12, 8); // Larger padding for text
                    seatLayout.addView(seatText);

                    switch (state) {
                        case "unavailable":
                            seatLayout.setBackgroundResource(R.drawable.cr4bdadada);
                            seatLayout.setEnabled(false);
                            break;
                        case "selected":
                            seatLayout.setBackgroundResource(R.drawable.cr4b2466fd);
                            selectedSeats.add(seatText);
                            subtotal += ticketPrice;
                            seatLayout.setOnClickListener(v -> toggleSeatSelection(seatLayout, seatText));
                            break;
                        case "available":
                            seatLayout.setBackgroundResource(R.drawable.cr4b858f93);
                            seatLayout.setOnClickListener(v -> toggleSeatSelection(seatLayout, seatText));
                            break;
                        case "empty":
                            seatLayout.setBackground(null);
                            seatLayout.setEnabled(false);
                            break;
                    }
                }

                row.addView(seatLayout);
            }
        }

        // Update subtotal
        updateSubtotal();
    }

    private void toggleSeatSelection(LinearLayout seatLayout, TextView seatText) {
        if (selectedSeats.contains(seatText)) {
            // Deselect
            seatLayout.setBackgroundResource(R.drawable.cr4b858f93);
            selectedSeats.remove(seatText);
            subtotal -= ticketPrice;
        } else {
            // Select
            seatLayout.setBackgroundResource(R.drawable.cr4b2466fd);
            selectedSeats.add(seatText);
            subtotal += ticketPrice;
        }
        updateSubtotal();
    }

    private void updateSubtotal() {
        subtotalValue.setText("IDR " + subtotal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}