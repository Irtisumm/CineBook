package com.example.cinebook.moviedetails;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_2_movie_detail);

        // Load images into ImageViews using Glide
        // Movie poster
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/j0dt0kq6_expires_30_days.png")
                .into((ImageView) findViewById(R.id.header_imageView_moviePoster));

        // Location icon in cinema section
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/8u09zmfb_expires_30_days.png")
                .into((ImageView) findViewById(R.id.header_imageView_moviePoster));

        // Set up click listener for the back arrow to finish the activity
        ImageView backArrow = findViewById(R.id.header_imageView_backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity and go back
            }
        });
    }
}