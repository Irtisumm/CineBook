package com.example.cinebook.moviedetails;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.network.Movie;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_1_movie_detail);

        // Get the movie from Intent
        Movie movie = getIntent().getParcelableExtra("movie");

        if (movie != null) {
            // Populate UI with movie data
            TextView titleText = findViewById(R.id.text_details_title);
            TextView descriptionText = findViewById(R.id.text_details_description);
            ImageView posterImage = findViewById(R.id.image_movie_poster);

            titleText.setText(movie.getTitle());
            descriptionText.setText(movie.getOverview());
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                    .into(posterImage);

            // Load static icons (unchanged)
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/23btlndi_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_back));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/j25eddo6_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_share));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/3bc558bs_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_favorite));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/5voi2tbq_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.divider_details));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/5ngiab93_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_theaters_dropdown));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/3l1cmjjw_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_theater_1_arrow));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/eqtimg39_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.divider_theater_1));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/46ugn84n_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_theater_2_arrow));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/ez6mxp5r_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_theater_2_separator));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/6w6w2x8n_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.divider_theater_2));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/yfv3wtsy_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_theater_3_arrow));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/6bfpet3x_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.divider_theater_3));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/unn6x2iq_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.icon_theater_4_arrow));
            Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/75a63500_expires_30_days.png")
                    .into((ImageView) findViewById(R.id.divider_showtimes));
        }

        // Back button click listener
        View button1 = findViewById(R.id.icon_back);
        button1.setOnClickListener(v -> finish());
    }
}