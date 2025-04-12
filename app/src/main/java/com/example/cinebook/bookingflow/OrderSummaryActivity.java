package com.example.cinebook.bookingflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.databinding.Activity12BookingTicketFlowBinding;
import com.example.cinebook.model.TicketOrder;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
                .load("https://picsum.photos/200/300") // Use a valid URL
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
        // Simulate adding a new card (in a real app, integrate with a payment gateway)
        new AlertDialog.Builder(this)
                .setTitle("Add New Card")
                .setMessage("Enter card details (mock)")
                .setPositiveButton("Add", (dialog, which) -> {
                    selectedPaymentMethod = "New Card **** 9999"; // Mock new card
                    binding.paymentMethod.setText(selectedPaymentMethod);
                    Toast.makeText(this, "Card added!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
                        // In a real app, apply discount to total
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