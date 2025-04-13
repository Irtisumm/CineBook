package com.example.cinebook.bookingflow;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.bumptech.glide.Glide;
import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;
import com.example.cinebook.model.TicketOrder;
import com.example.cinebook.screens.HomeScreen;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class MovieTicketActivity extends BaseActivity {

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
    private LinearLayout downloadButton;
    private LinearLayout shareButton;
    private LinearLayout backToHomeButton;
    private static final int STORAGE_PERMISSION_CODE = 100;
    private File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_ticket);

        // Initialize views
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
        downloadButton = findViewById(R.id.download_button);
        shareButton = findViewById(R.id.share_button);
        backToHomeButton = findViewById(R.id.back_to_home_button);

        // Retrieve data from intent
        Intent intent = getIntent();
        TicketOrder order = intent.getParcelableExtra("ticketOrder");

        if (order != null) {
            bindData(order);
        } else {
            Log.e("MovieTicketActivity", "TicketOrder is null");
            Toast.makeText(this, "No ticket data available", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Download button click listener
        downloadButton.setOnClickListener(v -> generateAndSavePDF(order));

        // Share button click listener
        shareButton.setOnClickListener(v -> sharePDF(order));

        // Back to Home button click listener
        backToHomeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(MovieTicketActivity.this, HomeScreen.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });
    }

    private void bindData(TicketOrder order) {
        activeTicketTitleTextView.setText("Active ticket");
        Glide.with(this)
                .load(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(appLogoImageView);

        String posterUrl = order.getMoviePosterUrl();
        Log.d("MovieTicketActivity", "Loading movie poster URL: " + posterUrl);
        if (posterUrl != null && !posterUrl.isEmpty()) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.placeholder_poster)
                    .error(R.drawable.placeholder_poster)
                    .into(moviePosterImageView);
        } else {
            moviePosterImageView.setImageResource(R.drawable.placeholder_poster);
        }

        String bookingCode = generateBookingCode();
        bookingCodeTextView.setText("Booking Code: #" + bookingCode);
        movieTitleTextView.setText(order.getMovieTitle());
        cinemaLocationTextView.setText(order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown");
        showTimeTextView.setText(order.getShowTime() != null ? order.getShowTime() : "Unknown");
        ticketClassTextView.setText("Regular");
        studioNumberTextView.setText(order.getStudio() != null ? order.getStudio() : "Unknown");
        rowNumberTextView.setText(order.getRow() != null ? order.getRow() : "Unknown");
        seatNumbersTextView.setText(String.join(", ", order.getSelectedSeats()));

        String qrContent = "Booking Code: #" + bookingCode + "\n" +
                "Movie: " + order.getMovieTitle() + "\n" +
                "Seats: " + String.join(", ", order.getSelectedSeats()) + "\n" +
                "Location: " + (order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown") + "\n" +
                "Time: " + (order.getShowTime() != null ? order.getShowTime() : "Unknown");
        Bitmap qrBitmap = generateQRCode(qrContent);
        if (qrBitmap != null) {
            qrCodeImageView.setImageBitmap(qrBitmap);
        } else {
            qrCodeImageView.setImageResource(R.drawable.placeholder_poster);
        }

        barcodeNumberTextView.setText(generateBarcodeNumber());

        // Add ticket to shared list for BookingConfirmationActivity
        synchronized (BookingConfirmationActivity.SHARED_TICKET_ORDERS) {
            BookingConfirmationActivity.SHARED_TICKET_ORDERS.add(order);
        }
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

    private String generateBookingCode() {
        return String.valueOf((int) (Math.random() * 100000));
    }

    private String generateBarcodeNumber() {
        return String.format("%05d %06d %08d",
                (int) (Math.random() * 100000),
                (int) (Math.random() * 1000000),
                (int) (Math.random() * 100000000));
    }

    private boolean checkStoragePermission() {
        // Only check for legacy storage permissions on Android 12 or lower
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        // No need for WRITE_EXTERNAL_STORAGE on Android 13+ for app-specific storage
        return true;
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // No permission needed for Android 13+, proceed with operation
            Toast.makeText(this, "Storage permission not required", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void generateAndSavePDF(TicketOrder order) {
        try {
            // Use app-specific external storage (no permission needed on Android 10+)
            String fileName = "Ticket_" + order.getMovieTitle().replace(" ", "_") + "_" + generateBookingCode() + ".pdf";
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (directory == null) {
                throw new Exception("Unable to access Downloads directory");
            }
            pdfFile = new File(directory, fileName);

            PdfWriter writer = new PdfWriter(new FileOutputStream(pdfFile));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Movie Ticket").setBold().setFontSize(20));
            document.add(new Paragraph("Booking Code: #" + generateBookingCode()));
            document.add(new Paragraph("Movie: " + order.getMovieTitle()));
            document.add(new Paragraph("Cinema: " + (order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown")));
            document.add(new Paragraph("Showtime: " + (order.getShowTime() != null ? order.getShowTime() : "Unknown")));
            document.add(new Paragraph("Class: Regular"));
            document.add(new Paragraph("Studio: " + (order.getStudio() != null ? order.getStudio() : "Unknown")));
            document.add(new Paragraph("Row: " + (order.getRow() != null ? order.getRow() : "Unknown")));
            document.add(new Paragraph("Seats: " + String.join(", ", order.getSelectedSeats())));

            Bitmap qrBitmap = generateQRCode(
                    "Booking Code: #" + generateBookingCode() + "\n" +
                            "Movie: " + order.getMovieTitle() + "\n" +
                            "Seats: " + String.join(", ", order.getSelectedSeats()) + "\n" +
                            "Location: " + (order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown") + "\n" +
                            "Time: " + (order.getShowTime() != null ? order.getShowTime() : "Unknown")
            );
            if (qrBitmap != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                ImageData imageData = ImageDataFactory.create(stream.toByteArray());
                Image qrImage = new Image(imageData);
                qrImage.setWidth(100);
                qrImage.setHeight(100);
                document.add(qrImage);
            }

            document.close();
            Toast.makeText(this, "PDF saved to app's Downloads folder: " + fileName, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("MovieTicketActivity", "Error generating PDF: " + e.getMessage());
            Toast.makeText(this, "Failed to generate PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sharePDF(TicketOrder order) {
        try {
            if (pdfFile == null || !pdfFile.exists()) {
                generateAndSavePDF(order);
            }

            if (pdfFile == null || !pdfFile.exists()) {
                throw new Exception("PDF file not created");
            }

            Uri pdfUri = FileProvider.getUriForFile(this,
                    "com.example.cinebook.fileprovider",
                    pdfFile);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Ticket: " + order.getMovieTitle());
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Here is your movie ticket PDF for " + order.getMovieTitle());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Ticket"));
        } catch (Exception e) {
            Log.e("MovieTicketActivity", "Error sharing PDF: " + e.getMessage());
            Toast.makeText(this, "Failed to share PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}