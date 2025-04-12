package com.example.cinebook.bookingflow;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.databinding.Activity12BookingTicketFlowBinding;
import com.example.cinebook.model.TicketOrder;
import java.text.NumberFormat;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Activity12BookingTicketFlowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve data from intent
        Intent intent = getIntent();
        TicketOrder order = intent.getParcelableExtra("ticketOrder");

        if (order != null) {
            bindData(order);
            setupListeners();
        } else {
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
        Glide.with(this)
                .load("https://picsum.photos/200/300")
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .into(binding.moviePoster);

        // Order Summary - Tickets
        binding.ticketCount.setText(String.format(Locale.getDefault(), "x%d", order.getTicketCount()));
        binding.ticketType.setText("Regular Ticket");
        binding.ticketSeats.setText(String.join(", ", order.getSelectedSeats()));
        binding.ticketPrice.setText(formatCurrency(order.getTicketPrice()));

        // Payment Summary
        binding.paymentTicketPrice.setText(formatCurrency(order.getTicketPrice()));
        binding.paymentTax.setText(formatCurrency(order.getTax()));
        double total = order.getTicketPrice() + order.getTax();
        binding.paymentTotal.setText(formatCurrency(total));

        // Payment Method
        binding.paymentMethod.setText(selectedPaymentMethod);
        binding.voucherText.setText(selectedVoucher != null ? "Voucher: " + selectedVoucher : "Use a voucher");
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
        List<String> paymentMethods = new ArrayList<>(Arrays.asList(
                "Visa **** 1234",
                "MasterCard **** 5678",
                "Add New Card"
        ));
        new AlertDialog.Builder(this)
                .setTitle("Select Payment Method")
                .setItems(paymentMethods.toArray(new String[0]), (dialog, which) -> {
                    if (which == paymentMethods.size() - 1) {
                        // Add New Card
                        showAddCardDialog();
                    } else {
                        selectedPaymentMethod = paymentMethods.get(which);
                        binding.paymentMethod.setText(selectedPaymentMethod);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddCardDialog() {
        // Create dialog with custom layout
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Card");

        // Inflate custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_card, null);
        builder.setView(dialogView);

        // Initialize input fields
        EditText cardNumberInput = dialogView.findViewById(R.id.card_number_input);
        EditText expiryDateInput = dialogView.findViewById(R.id.expiry_date_input);
        EditText cvvInput = dialogView.findViewById(R.id.cvv_input);
        EditText cardholderNameInput = dialogView.findViewById(R.id.cardholder_name_input);
        Button saveButton = dialogView.findViewById(R.id.save_card_button);
        Button cancelButton = dialogView.findViewById(R.id.cancel_card_button);

        // Format card number (spaces every 4 digits)
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
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(input.charAt(i));
                }

                cardNumberInput.setText(formatted.toString());
                // Adjust cursor position
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

        // Format expiry date (MM/YY)
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

        // Create dialog
        AlertDialog dialog = builder.create();

        // Save button
        saveButton.setOnClickListener(v -> {
            String cardNumber = cardNumberInput.getText().toString().replaceAll("[^0-9]", "");
            String expiryDate = expiryDateInput.getText().toString();
            String cvv = cvvInput.getText().toString();
            String cardholderName = cardholderNameInput.getText().toString().trim();

            // Validate inputs
            if (cardNumber.length() != 16) {
                cardNumberInput.setError("Enter a 16-digit card number");
                return;
            }
            if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
                expiryDateInput.setError("Enter valid expiry date (MM/YY)");
                return;
            }
            // Check if expiry is in the future
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

            // Format card number for display
            String lastFour = cardNumber.substring(cardNumber.length() - 4);
            selectedPaymentMethod = String.format("Card **** **** **** %s", lastFour);
            binding.paymentMethod.setText(selectedPaymentMethod);
            Toast.makeText(this, "Card added successfully!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
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
                        binding.voucherText.setText("Use a voucher");
                    } else {
                        selectedVoucher = vouchers.get(which);
                        binding.voucherText.setText("Voucher: " + selectedVoucher);
                        Toast.makeText(this, "Voucher applied!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount).replace("Rp", "IDR ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}