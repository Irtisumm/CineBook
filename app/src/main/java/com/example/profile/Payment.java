package com.example.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cinebook.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Payment extends AppCompatActivity {

    private static final String PREFS_NAME = "CinebookPrefs";
    private static final String KEY_PAYMENT_METHODS = "payment_methods";

    private LinearLayout paymentListContainer;
    private LinearLayout addPaymentContainer;
    private SharedPreferences prefs;
    private List<PaymentMethod> paymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialize SharedPreferences
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Initialize views
        ImageView backButton = findViewById(R.id.back_button);
        paymentListContainer = findViewById(R.id.payment_list_container);
        addPaymentContainer = findViewById(R.id.add_payment_container);

        // Load payment methods
        loadPaymentMethods();
        displayPaymentMethods();

        // Set click listeners
        backButton.setOnClickListener(v -> {
            setResult(RESULT_OK, new Intent());
            finish();
        });
        addPaymentContainer.setOnClickListener(v -> showAddPaymentDialog());
    }

    private void loadPaymentMethods() {
        String json = prefs.getString(KEY_PAYMENT_METHODS, null);
        if (json != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<PaymentMethod>>(){}.getType();
            paymentMethods = gson.fromJson(json, type);
        } else {
            // Initialize with default cards
            paymentMethods = new ArrayList<>();
            paymentMethods.add(new PaymentMethod("**** **** **** 2157", "Dean Turner"));
            paymentMethods.add(new PaymentMethod("**** **** **** 2157", "Dean Turner"));
        }
    }

    private void savePaymentMethods() {
        Gson gson = new Gson();
        String json = gson.toJson(paymentMethods);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PAYMENT_METHODS, json);
        editor.apply();
    }

    private void displayPaymentMethods() {
        // Clear existing views
        paymentListContainer.removeAllViews();

        // Add a view for each payment method
        for (int i = 0; i < paymentMethods.size(); i++) {
            PaymentMethod method = paymentMethods.get(i);
            View cardView = LayoutInflater.from(this).inflate(R.layout.item_payment_method, paymentListContainer, false);

            ImageView cardIcon = cardView.findViewById(R.id.card_icon);
            TextView cardNumber = cardView.findViewById(R.id.card_number);
            TextView cardHolder = cardView.findViewById(R.id.card_holder);
            ImageView deleteIcon = cardView.findViewById(R.id.delete_icon);

            cardIcon.setImageResource(method.cardNumber.startsWith("4") ? R.drawable.ic_visa : R.drawable.ic_mastercard);
            cardNumber.setText(method.cardNumber);
            cardHolder.setText(method.cardHolder);

            // Store index in tag for deletion
            final int index = i;
            deleteIcon.setOnClickListener(v -> {
                paymentMethods.remove(index);
                savePaymentMethods();
                displayPaymentMethods();
                Toast.makeText(this, "Payment method removed", Toast.LENGTH_SHORT).show();
            });

            paymentListContainer.addView(cardView);
        }
    }

    private void showAddPaymentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Payment Method");

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_payment, null);
        builder.setView(dialogView);

        EditText cardNumberInput = dialogView.findViewById(R.id.card_number_input);
        EditText cardHolderInput = dialogView.findViewById(R.id.card_holder_input);
        EditText expiryInput = dialogView.findViewById(R.id.expiry_input);
        EditText cvvInput = dialogView.findViewById(R.id.cvv_input);

        // Card number formatting (XXXX XXXX XXXX XXXX)
        cardNumberInput.addTextChangedListener(new TextWatcher() {
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
                for (int i = 0; i < input.length() && i < 16; i++) {
                    if (i > 0 && i % 4 == 0) {
                        formatted.append(" ");
                    }
                    formatted.append(input.charAt(i));
                }

                cardNumberInput.setText(formatted.toString());
                cardNumberInput.setSelection(formatted.length());

                isFormatting = false;
            }
        });

        // Expiry formatting (MM/YY)
        expiryInput.addTextChangedListener(new TextWatcher() {
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
                if (input.length() >= 4) {
                    input = input.substring(0, 4);
                    String formatted = input.substring(0, 2) + "/" + input.substring(2, 4);
                    expiryInput.setText(formatted);
                    expiryInput.setSelection(formatted.length());
                } else if (input.length() > 2) {
                    String formatted = input.substring(0, 2) + "/" + input.substring(2);
                    expiryInput.setText(formatted);
                    expiryInput.setSelection(formatted.length());
                }

                isFormatting = false;
            }
        });

        builder.setPositiveButton("Save", null);
        builder.setNegativeButton("Cancel", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String cardNumber = cardNumberInput.getText().toString().trim();
            String cardHolder = cardHolderInput.getText().toString().trim();
            String expiry = expiryInput.getText().toString().trim();
            String cvv = cvvInput.getText().toString().trim();

            // Validation
            if (cardNumber.replaceAll("[^0-9]", "").length() < 16) {
                cardNumberInput.setError("Enter a valid 16-digit card number");
                return;
            }
            if (cardHolder.isEmpty()) {
                cardHolderInput.setError("Cardholder name required");
                return;
            }
            if (!expiry.matches("\\d{2}/\\d{2}")) {
                expiryInput.setError("Enter valid expiry (MM/YY)");
                return;
            }
            if (cvv.length() < 3 || cvv.length() > 4) {
                cvvInput.setError("Enter valid CVV (3-4 digits)");
                return;
            }

            // Format card number for display
            String displayCardNumber = formatCardNumber(cardNumber);

            // Add payment method
            paymentMethods.add(new PaymentMethod(displayCardNumber, cardHolder));
            savePaymentMethods();
            displayPaymentMethods();

            Toast.makeText(this, "Payment method added", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private String formatCardNumber(String cardNumber) {
        String digits = cardNumber.replaceAll("[^0-9]", "");
        if (digits.length() >= 16) {
            return "**** **** **** " + digits.substring(12, 16);
        }
        return cardNumber;
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