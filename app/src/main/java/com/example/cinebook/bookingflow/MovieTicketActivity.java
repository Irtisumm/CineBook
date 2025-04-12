package com.example.cinebook.bookingflow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.model.TicketOrder;
import com.google.android.material.imageview.ShapeableImageView;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.BarcodeFormat;

public class MovieTicketActivity extends AppCompatActivity {

    private ImageView appLogoImageView;
    private TextView activeTicketTitleTextView;
    private ShapeableImageView moviePosterImageView;
    private TextView bookingCodeTextView;
    private TextView movieTitleTextView;
    private TextView cinemaLocationTextView;
    private TextView showTimeTextView;
    private TextView ticketClassTextView;
    private TextView studioNumberTextView;
    private TextView rowNumberTextView;
    private TextView seatNumbersTextView;
    private ShapeableImageView qrCodeImageView;
    private TextView barcodeNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_ticket);

        // Initialize views with updated IDs
        appLogoImageView = findViewById(R.id.app_logo);
        activeTicketTitleTextView = findViewById(R.id.active_ticket_title);
        moviePosterImageView = findViewById(R.id.movie_poster);
        bookingCodeTextView = findViewById(R.id.booking_code);
        movieTitleTextView = findViewById(R.id.movie_title);
        cinemaLocationTextView = findViewById(R.id.cinema_location);
        showTimeTextView = findViewById(R.id.show_time);
        ticketClassTextView = findViewById(R.id.ticket_class);
        studioNumberTextView = findViewById(R.id.studio_number);
        rowNumberTextView = findViewById(R.id.row_number);
        seatNumbersTextView = findViewById(R.id.seat_numbers);
        qrCodeImageView = findViewById(R.id.qr_code_image);
        barcodeNumberTextView = findViewById(R.id.barcode_number);

        // Retrieve data from intent
        Intent intent = getIntent();
        TicketOrder order = intent.getParcelableExtra("ticketOrder");

        if (order != null) {
            bindData(order);
        } else {
            Log.e("MovieTicketActivity", "TicketOrder is null");
            finish();
        }
    }

    private void bindData(TicketOrder order) {
        // Set static content
        activeTicketTitleTextView.setText("Active ticket");

        // Load app logo
        Glide.with(this)
                .load(R.mipmap.ic_launcher) // Replace with actual logo resource or URL
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(appLogoImageView);

        // Load movie poster with debugging
        String posterUrl = order.getMoviePosterUrl();
        Log.d("MovieTicketActivity", "Movie poster URL: " + posterUrl);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.drawable.default_poster) // Ensure default_poster exists in res/drawable
                    .into(moviePosterImageView);
        } else {
            Log.w("MovieTicketActivity", "Movie poster URL is null or empty, using default");
            moviePosterImageView.setImageResource(R.drawable.default_poster);
        }

        // Generate booking code once for consistency
        String bookingCode = generateBookingCode();

        // Set ticket details from TicketOrder
        bookingCodeTextView.setText("Booking Code: #" + bookingCode);
        movieTitleTextView.setText(order.getMovieTitle());
        cinemaLocationTextView.setText(order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown");
        showTimeTextView.setText(order.getShowTime() != null ? order.getShowTime() : "Unknown");
        ticketClassTextView.setText("Regular"); // Hardcoded as per XML
        studioNumberTextView.setText(order.getStudio() != null ? order.getStudio() : "Unknown");
        rowNumberTextView.setText(order.getRow() != null ? order.getRow() : "Unknown");
        seatNumbersTextView.setText(String.join(", ", order.getSelectedSeats()));

        // Generate QR code
        String qrContent = "Booking Code: #" + bookingCode + "\n" +
                "Movie: " + order.getMovieTitle() + "\n" +
                "Seats: " + String.join(", ", order.getSelectedSeats()) + "\n" +
                "Location: " + (order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown") + "\n" +
                "Time: " + (order.getShowTime() != null ? order.getShowTime() : "Unknown");
        Bitmap qrBitmap = generateQRCode(qrContent);
        if (qrBitmap != null) {
            qrCodeImageView.setImageBitmap(qrBitmap);
        } else {
            Log.e("MovieTicketActivity", "Failed to generate QR code");
            qrCodeImageView.setImageResource(R.mipmap.ic_launcher); // Fallback
        }

        // Set barcode number
        barcodeNumberTextView.setText(generateBarcodeNumber());
    }

    private Bitmap generateQRCode(String text) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate a dummy booking code (replace with actual logic)
    private String generateBookingCode() {
        return String.valueOf((int) (Math.random() * 100000));
    }

    // Generate a dummy barcode number (replace with actual logic)
    private String generateBarcodeNumber() {
        return String.format("%05d %06d %08d",
                (int) (Math.random() * 100000),
                (int) (Math.random() * 1000000),
                (int) (Math.random() * 100000000));
    }
}