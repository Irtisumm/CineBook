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
import com.example.cinebook.model.TicketOrder;
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

        // Initialize views
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
                .load(R.drawable.logo)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(logoImage);
        Glide.with(this)
                .load(R.drawable.cinema_icon)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(cinemaIcon);
        Glide.with(this)
                .load(R.drawable.lineok)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(screenImage);

        // Retrieve data from Intent with defaults
        final String movieTitle;
        final String selectedTheater;
        final String selectedDateStr;
        final String selectedShowtime;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            movieTitle = extras.getString("movie_title", "Unknown Movie");
            selectedTheater = extras.getString("selected_theater", "Gandaria City Cinema");
            selectedDateStr = extras.getString("selected_date", "12 Sep");
            selectedShowtime = extras.getString("selected_showtime", "14:45 WIB");
        } else {
            movieTitle = "Unknown Movie";
            selectedTheater = "Gandaria City Cinema";
            selectedDateStr = "12 Sep";
            selectedShowtime = "14:45 WIB";
        }
        cinemaName.setText(selectedTheater);
        cinemaDate.setText(selectedDateStr + ", " + selectedShowtime);

        // Start countdown timer
        startCountdownTimer();

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
                Intent intent = new Intent(FiestBookingTicketFlow.this, OrderSummaryActivity.class);
                TicketOrder order = new TicketOrder();

                // Populate TicketOrder
                order.setMovieTitle(movieTitle);
                String posterUrl = getPosterUrlForMovie(movieTitle);
                order.setMoviePosterUrl(posterUrl);
                order.setMovieRating("PG-13");
                order.setMovieDuration("2h 44m");
                order.setMovieScore(9.8);
                order.setMovieRatingsCount(192);
                order.setCinemaLocation(selectedTheater);
                order.setShowTime(selectedDateStr + ", " + selectedShowtime);
                order.setStudio("4");
                order.setRow(getSelectedRow());

                // Convert selectedSeats to List<String>
                List<String> seatLabels = new ArrayList<>();
                for (TextView seat : selectedSeats) {
                    seatLabels.add(seat.getText().toString());
                }
                order.setSelectedSeats(seatLabels);
                order.setTicketCount(seatLabels.size());
                order.setTicketPrice(subtotal);
                order.setTax(subtotal * 0.1);
                order.setPaymentMethod("");

                // Attach TicketOrder to Intent
                intent.putExtra("ticketOrder", order);
                startActivity(intent);
            }
        });
    }

    private String getPosterUrlForMovie(String movieTitle) {
        if (movieTitle != null) {
            switch (movieTitle.toLowerCase()) {
                case "a haunting in venice":
                    return "https://image.tmdb.org/t/p/w500/1Xgjl22MkAZC2Apj135jEYhS0qO.jpg";
                case "oppenheimer":
                    return "https://image.tmdb.org/t/p/w500/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg";
                default:
                    return "https://image.tmdb.org/t/p/w500/placeholder.jpg";
            }
        }
        return "https://image.tmdb.org/t/p/w500/placeholder.jpg";
    }

    private String getSelectedRow() {
        if (!selectedSeats.isEmpty()) {
            String seatLabel = selectedSeats.get(0).getText().toString();
            return seatLabel.substring(0, 1); // e.g., "A3" -> "A"
        }
        return "A";
    }

    private void startCountdownTimer() {
        // Start a 2-minute timer to match "1:59" initial display
        long timeRemainingMillis = 2 * 60 * 1000; // 2 minutes
        countDownTimer = new CountDownTimer(timeRemainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                timerText.setText(String.format(Locale.getDefault(), "%d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                timerText.setText("0:00");
                Toast.makeText(FiestBookingTicketFlow.this, "Booking time expired!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    private void setupSeatGrid() {
        LinearLayout[] seatRows = { rowK, rowJ, rowH, rowG, rowF, rowE, rowD, rowC, rowB, rowA };
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
            row.removeAllViews();

            for (int j = 0; j < labels.length; j++) {
                String label = labels[j];
                String state = states[j];

                LinearLayout seatLayout = new LinearLayout(this);
                seatLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                seatLayout.setOrientation(LinearLayout.VERTICAL);
                seatLayout.setPadding(8, 8, 8, 8);
                if (j < labels.length - 1) {
                    seatLayout.setPadding(8, 8, 16, 8);
                }

                if (!label.isEmpty()) {
                    TextView seatText = new TextView(this);
                    seatText.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
                    seatText.setText(label);
                    seatText.setTextColor(getResources().getColor(android.R.color.white));
                    seatText.setTextSize(16);
                    seatText.setPadding(12, 8, 12, 8);
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

        updateSubtotal();
    }

    private void toggleSeatSelection(LinearLayout seatLayout, TextView seatText) {
        if (selectedSeats.contains(seatText)) {
            seatLayout.setBackgroundResource(R.drawable.cr4b858f93);
            selectedSeats.remove(seatText);
            subtotal -= ticketPrice;
        } else {
            seatLayout.setBackgroundResource(R.drawable.cr4b2466fd);
            selectedSeats.add(seatText);
            subtotal += ticketPrice;
        }
        updateSubtotal();
    }

    private void updateSubtotal() {
        String formattedSubtotal = String.format(Locale.getDefault(), "IDR %,d", subtotal).replace(",", ".");
        subtotalValue.setText(formattedSubtotal);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}