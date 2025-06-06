package com.example.cinebook.bookingflow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.databinding.Activity12BookingTicketFlowBinding;
import com.example.cinebook.model.TicketOrder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderSummaryActivity extends AppCompatActivity {

    private Activity12BookingTicketFlowBinding binding;
    private String selectedPaymentMethod = "Select a payment method";
    private String selectedVoucher = null;
    private double discountMultiplier = 1.0; // 1.0 = no discount
    private TicketOrder order;
    private static final String PREFS_NAME = "CinebookPrefs";
    private static final String KEY_PAYMENT_METHODS = "payment_methods";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Activity12BookingTicketFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Back button
        ImageView backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrderSummaryActivity.this, FiestBookingTicketFlow.class);
            intent.putExtra("ticketOrder", order);
            startActivity(intent);
            finish();
        });

        // Retrieve data from intent
        Intent intent = getIntent();
        order = intent.getParcelableExtra("ticketOrder");

        if (order != null) {
            bindData(order);
            setupListeners();
        } else {
            Log.e("OrderSummaryActivity", "No order data received");
            Toast.makeText(this, "No order data received", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void bindData(TicketOrder order) {
        // Movie Details
        binding.movieTitle.setText(order.getMovieTitle());
        binding.movieRating.setText(order.getMovieRating());
        binding.movieDuration.setText(order.getMovieDuration());
        binding.movieScore.setText(String.format(Locale.getDefault(), "%.1f (%d Ratings)",
                order.getMovieScore(), order.getMovieRatingsCount()));

        // Load movie poster
        String posterUrl = order.getMoviePosterUrl();
        Log.d("OrderSummaryActivity", "Received movie poster URL: " + posterUrl);
        if (posterUrl != null && !posterUrl.isEmpty() && !posterUrl.contains("placeholder")) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.placeholder_poster)
                    .centerCrop()
                    .into(binding.moviePoster);
        } else {
            Log.w("OrderSummaryActivity", "Invalid or missing poster URL: " + posterUrl);
            binding.moviePoster.setImageResource(R.drawable.placeholder_poster);
        }

        // Order Summary - Tickets
        binding.ticketCount.setText(String.format(Locale.getDefault(), "x%d", order.getTicketCount()));
        // Set ticket type based on showtime
        String showTime = order.getShowTime().toUpperCase();
        String ticketType = showTime.contains("IMAX") ? "IMAX Ticket" :
                showTime.contains("PREMIERE") ? "PREMIERE Ticket" : "Regular Ticket";
        binding.ticketType.setText(ticketType);
        binding.ticketSeats.setText(String.join(", ", order.getSelectedSeats()));
        binding.ticketPrice.setText(formatCurrency(order.getTicketPrice() / order.getTicketCount()));

        // Payment Summary
        updatePaymentSummary();
        binding.paymentMethod.setText(selectedPaymentMethod);
        binding.voucherText.setText(selectedVoucher != null ? "Voucher: " + selectedVoucher : "Use a voucher");
    }

    private void updatePaymentSummary() {
        double basePrice = order.getTicketPrice();
        double discountedPrice = basePrice * discountMultiplier;
        double tax = discountedPrice * 0.1;
        double total = discountedPrice + tax;

        binding.paymentTicketPrice.setText(formatCurrency(discountedPrice));
        binding.paymentTax.setText(formatCurrency(tax));
        if (selectedVoucher != null && discountMultiplier < 1.0) {
            binding.discountRow.setVisibility(View.VISIBLE);
            binding.paymentDiscount.setText(formatCurrency(basePrice - discountedPrice));
        } else {
            binding.discountRow.setVisibility(View.GONE);
        }
        binding.paymentTotal.setText(formatCurrency(total));
    }

    private void setupListeners() {
        // Pay Now Button
        binding.payNowButton.setOnClickListener(v -> {
            if (selectedPaymentMethod.equals("Select a payment method")) {
                Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                return;
            }
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Payment")
                    .setMessage("Confirm payment of " + binding.paymentTotal.getText() + "?")
                    .setPositiveButton("Pay", (dialog, which) -> {
                        Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
                        order.setPaymentMethod(selectedPaymentMethod);
                        Intent intent = new Intent(OrderSummaryActivity.this, MovieTicketActivity.class);
                        intent.putExtra("ticketOrder", order);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Select Payment Method
        binding.selectedPaymentRow.setOnClickListener(v -> showPaymentMethodDialog());

        // Select Voucher
        binding.voucherSection.setOnClickListener(v -> showVoucherDialog());
    }

    private void showPaymentMethodDialog() {
        List<String> paymentMethods = getPaymentMethods();
        paymentMethods.add("Add New Card");
        new AlertDialog.Builder(this)
                .setTitle("Select Payment Method")
                .setItems(paymentMethods.toArray(new String[0]), (dialog, which) -> {
                    if (which == paymentMethods.size() - 1) {
                        showAddCardDialog();
                    } else {
                        selectedPaymentMethod = paymentMethods.get(which);
                        binding.paymentMethod.setText(selectedPaymentMethod);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private List<String> getPaymentMethods() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_PAYMENT_METHODS, null);
        List<String> cardNumbers = new ArrayList<>();
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PaymentMethod>>(){}.getType();
            List<PaymentMethod> methods = gson.fromJson(json, type);
            for (PaymentMethod method : methods) {
                cardNumbers.add(method.cardNumber);
            }
        } else {
            // Default cards
            cardNumbers.add("**** **** **** 2157");
            cardNumbers.add("**** **** **** 2157");
        }
        return cardNumbers;
    }

    private void showAddCardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Card");
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_card, null);
        builder.setView(dialogView);

        EditText cardNumberInput = dialogView.findViewById(R.id.card_number_input);
        EditText expiryDateInput = dialogView.findViewById(R.id.expiry_date_input);
        EditText cvvInput = dialogView.findViewById(R.id.cvv_input);
        EditText cardholderNameInput = dialogView.findViewById(R.id.cardholder_name_input);
        Button saveButton = dialogView.findViewById(R.id.save_card_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_card_button);

        cardNumberInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int cursorPosition;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cursorPosition = cardNumberInput.getSelectionStart();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String input = s.toString().replaceAll("[^0-9]", "");
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < input.length(); i++) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ");
                    formatted.append(input.charAt(i));
                }

                cardNumberInput.setText(formatted.toString());
                int newLength = formatted.length();
                if (cursorPosition > newLength) {
                    cursorPosition = newLength;
                } else if (cursorPosition > 0 && formatted.charAt(cursorPosition - 1) == ' ') {
                    cursorPosition++;
                }
                cardNumberInput.setSelection(cursorPosition);

                isFormatting = false;
            }
        });

        expiryDateInput.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                String input = s.toString().replaceAll("[^0-9]", "");
                StringBuilder formatted = new StringBuilder();
                if (input.length() > 2) {
                    formatted.append(input.substring(0, 2)).append("/");
                    formatted.append(input.substring(2, Math.min(input.length(), 4)));
                } else {
                    formatted.append(input);
                }

                expiryDateInput.setText(formatted.toString());
                expiryDateInput.setSelection(formatted.length());

                isFormatting = false;
            }
        });

        AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String cardNumber = cardNumberInput.getText().toString().replaceAll("[^0-9]", "");
            String expiryDate = expiryDateInput.getText().toString();
            String cvv = cvvInput.getText().toString();
            String cardholderName = cardholderNameInput.getText().toString().trim();

            if (cardNumber.length() != 16) {
                cardNumberInput.setError("Enter a 16-digit card number");
                return;
            }
            if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                expiryDateInput.setError("Enter valid expiry date (MM/YY)");
                return;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("MM/yy", Locale.US);
                sdf.setLenient(false);
                Calendar expiryCal = Calendar.getInstance();
                expiryCal.setTime(sdf.parse(expiryDate));
                Calendar now = Calendar.getInstance();
                if (expiryCal.before(now)) {
                    expiryDateInput.setError("Card has expired");
                    return;
                }
            } catch (Exception e) {
                expiryDateInput.setError("Invalid expiry date");
                return;
            }
            if (!cvv.matches("\\d{3,4}")) {
                cvvInput.setError("Enter valid CVV (3-4 digits)");
                return;
            }
            if (cardholderName.isEmpty()) {
                cardholderNameInput.setError("Enter cardholder name");
                return;
            }

            String lastFour = cardNumber.substring(cardNumber.length() - 4);
            String displayCardNumber = String.format("**** **** **** %s", lastFour);
            // Save to SharedPreferences
            savePaymentMethod(displayCardNumber, cardholderName);
            selectedPaymentMethod = displayCardNumber;
            binding.paymentMethod.setText(selectedPaymentMethod);
            Toast.makeText(this, "Card added successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void savePaymentMethod(String cardNumber, String cardHolder) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String json = prefs.getString(KEY_PAYMENT_METHODS, null);
        List<PaymentMethod> methods;
        Gson gson = new Gson();
        if (json != null) {
            Type type = new TypeToken<List<PaymentMethod>>(){}.getType();
            methods = gson.fromJson(json, type);
        } else {
            methods = new ArrayList<>();
        }
        methods.add(new PaymentMethod(cardNumber, cardHolder));
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PAYMENT_METHODS, gson.toJson(methods));
        editor.apply();
    }

    private void showVoucherDialog() {
        List<String> vouchers = new ArrayList<>(Arrays.asList(
                "10% Off (VOUCHER10)",
                "20% Off (VOUCHER20)",
                "No Voucher"
        ));
        new AlertDialog.Builder(this)
                .setTitle("Select Voucher")
                .setItems(vouchers.toArray(new String[0]), (dialog, which) -> {
                    if (which == vouchers.size() - 1) {
                        selectedVoucher = null;
                        discountMultiplier = 1.0;
                        binding.voucherText.setText("Use a voucher");
                    } else {
                        selectedVoucher = vouchers.get(which);
                        discountMultiplier = selectedVoucher.contains("VOUCHER10") ? 0.9 : 0.8;
                        binding.voucherText.setText("Voucher: " + selectedVoucher);
                        Toast.makeText(this, "Voucher applied!", Toast.LENGTH_SHORT).show();
                    }
                    updatePaymentSummary();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.getDefault(), "RM %.0f", amount);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private static class PaymentMethod {
        String cardNumber;
        String cardHolder;

        PaymentMethod(String cardNumber, String cardHolder) {
            this.cardNumber = cardNumber;
            this.cardHolder = cardHolder;
        }
    }
}