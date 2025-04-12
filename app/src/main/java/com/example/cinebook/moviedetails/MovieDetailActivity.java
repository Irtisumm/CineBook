package com.example.cinebook.moviedetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.bookingflow.FiestBookingTicketFlow;
import com.example.cinebook.network.Movie;
import com.example.cinebook.network.TmdbApi;
import com.example.cinebook.network.TmdbCredits;
import com.example.cinebook.network.TmdbMovieDetails;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.List;
import java.util.stream.Collectors;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView backArrow;
    private ImageView moviePoster;
    private ImageView locationIcon;
    private TextView movieTitle;
    private TextView synopsis;
    private TextView rating;
    private TextView duration;
    private TextView score;
    private TextView totalRatings;
    private TextView ratedValue;
    private TextView durationValue;
    private TextView languageValue;
    private TextView productionValue;
    private TextView cinemaName;
    private TextView cinemaAddress;
    private TextView cinemaDistance;
    private TextView selectedDate;
    private TextView selectedShowtime;
    private TextView actor1;
    private TextView actor2;
    private TextView actor3;
    private TextView actor4;
    private TextView writersValue;
    private LinearLayout continueButton;
    private TmdbApi tmdbApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_2_movie_detail);

        // Initialize views using findViewById
        backArrow = findViewById(R.id.header_imageView_backArrow);
        moviePoster = findViewById(R.id.header_imageView_moviePoster);
        locationIcon = findViewById(R.id.cinema_imageView_locationIcon);
        movieTitle = findViewById(R.id.header_textView_movieTitle);
        synopsis = findViewById(R.id.header_textView_synopsis);
        rating = findViewById(R.id.header_textView_rating);
        duration = findViewById(R.id.header_textView_duration);
        score = findViewById(R.id.ratings_textView_score);
        totalRatings = findViewById(R.id.ratings_textView_totalRatings);
        ratedValue = findViewById(R.id.rated_textView_value);
        durationValue = findViewById(R.id.duration_textView_value);
        languageValue = findViewById(R.id.language_textView_value);
        productionValue = findViewById(R.id.production_textView_value);
        cinemaName = findViewById(R.id.cinema_textView_name);
        cinemaAddress = findViewById(R.id.cinema_textView_address);
        cinemaDistance = findViewById(R.id.cinema_textView_distance);
        selectedDate = findViewById(R.id.cinema_textView_selected_date);
        selectedShowtime = findViewById(R.id.cinema_textView_selected_showtime);
        actor1 = findViewById(R.id.cast_textView_actor1);
        actor2 = findViewById(R.id.cast_textView_actor2);
        actor3 = findViewById(R.id.cast_textView_actor3);
        actor4 = findViewById(R.id.cast_textView_actor4);
        writersValue = findViewById(R.id.writers_textView_value);
        continueButton = findViewById(R.id.continue_button);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);

        // Back arrow click listener
        backArrow.setOnClickListener(v -> finish());

        // Continue button click listener
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailActivity.this, FiestBookingTicketFlow.class);
            Bundle extras = getIntent().getExtras();
            intent.putExtra("movie_title", movieTitle.getText().toString());
            intent.putExtra("selected_theater", cinemaName.getText().toString());
            intent.putExtra("selected_date", extras != null ? extras.getString("selected_date", "N/A") : "N/A");
            intent.putExtra("selected_showtime", extras != null ? extras.getString("selected_showtime", "N/A") : "N/A");
            String posterPath = moviePoster.getTag() != null ? moviePoster.getTag().toString() : "";
            intent.putExtra("poster_path", posterPath);
            startActivity(intent);
        });

        // Get passed data
        Bundle extras = getIntent().getExtras();
        Movie movie = extras != null ? extras.getParcelable("movie") : null;
        String selectedDateStr = extras != null ? extras.getString("selected_date", "N/A") : "N/A";
        String selectedTheater = extras != null ? extras.getString("selected_theater", "N/A") : "N/A";
        String selectedTheaterAddress = extras != null ? extras.getString("selected_theater_address", "N/A") : "N/A";
        String selectedTheaterDistance = extras != null ? extras.getString("selected_theater_distance", "N/A") : "N/A";
        String selectedShowtimeStr = extras != null ? extras.getString("selected_showtime", "N/A") : "N/A";

        // Set cinema data
        cinemaName.setText(selectedTheater);
        cinemaAddress.setText(selectedTheaterAddress);
        cinemaDistance.setText(selectedTheaterDistance);
        selectedDate.setText("Date: " + selectedDateStr);
        selectedShowtime.setText("Showtime: " + selectedShowtimeStr);

        // Load movie data from API
        if (movie != null) {
            movieTitle.setText(movie.getTitle());
            loadMovieDetails(movie.getId());
            loadMovieCredits(movie.getId());
        } else {
            movieTitle.setText("Unknown Movie");
            synopsis.setText("No synopsis available.");
            Glide.with(this)
                    .load(R.drawable.placeholder)
                    .into(moviePoster);
        }

        // Set location icon
        Glide.with(this)
                .load(R.drawable.ic_menu_myplaces)
                .into(locationIcon);
    }

    private void loadMovieDetails(int movieId) {
        String apiKey = getString(R.string.tmdb_api_key);
        Call<TmdbMovieDetails> call = tmdbApi.getMovieDetails(movieId, apiKey);
        call.enqueue(new Callback<TmdbMovieDetails>() {
            @Override
            public void onResponse(Call<TmdbMovieDetails> call, Response<TmdbMovieDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TmdbMovieDetails details = response.body();

                    // Poster
                    String posterUrl = "https://image.tmdb.org/t/p/w500" + details.getPosterPath();
                    Glide.with(MovieDetailActivity.this)
                            .load(posterUrl)
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error)
                            .into(moviePoster);
                    moviePoster.setTag(posterUrl);

                    // Title
                    movieTitle.setText(details.getTitle());

                    // Synopsis
                    synopsis.setText(details.getOverview());

                    // Rating
                    String ratingStr = details.getVoteAverage() > 0 ? String.format("%.1f", details.getVoteAverage()) : "N/A";
                    rating.setText(ratingStr);
                    score.setText(ratingStr);
                    totalRatings.setText(details.getVoteCount() + " Ratings");
                    ratedValue.setText(ratingStr);

                    // Duration
                    int runtime = details.getRuntime();
                    String durationStr = runtime > 0 ? String.format("%dh %dm", runtime / 60, runtime % 60) : "N/A";
                    duration.setText(durationStr);
                    durationValue.setText(durationStr);

                    // Language
                    String language = details.getSpokenLanguages().isEmpty() ? "N/A" :
                            details.getSpokenLanguages().get(0).getEnglishName();
                    languageValue.setText(language);

                    // Production
                    String production = details.getProductionCompanies().isEmpty() ? "N/A" :
                            details.getProductionCompanies().stream()
                                    .map(TmdbMovieDetails.ProductionCompany::getName)
                                    .collect(Collectors.joining(", "));
                    productionValue.setText(production);
                } else {
                    Log.e("MovieDetailActivity", "Failed to load movie details: " + response.message());
                    synopsis.setText("Failed to load details.");
                }
            }

            @Override
            public void onFailure(Call<TmdbMovieDetails> call, Throwable t) {
                Log.e("MovieDetailActivity", "API call failed: " + t.getMessage());
                synopsis.setText("Error loading data.");
            }
        });
    }

    private void loadMovieCredits(int movieId) {
        String apiKey = getString(R.string.tmdb_api_key);
        Call<TmdbCredits> call = tmdbApi.getMovieCredits(movieId, apiKey);
        call.enqueue(new Callback<TmdbCredits>() {
            @Override
            public void onResponse(Call<TmdbCredits> call, Response<TmdbCredits> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TmdbCredits credits = response.body();

                    // Cast
                    List<String> castNames = credits.getCast().stream()
                            .limit(4)
                            .map(TmdbCredits.Cast::getName)
                            .collect(Collectors.toList());
                    actor1.setText(castNames.size() > 0 ? castNames.get(0) : "N/A");
                    actor2.setText(castNames.size() > 1 ? castNames.get(1) : "N/A");
                    actor3.setText(castNames.size() > 2 ? castNames.get(2) : "N/A");
                    actor4.setText(castNames.size() > 3 ? castNames.get(3) : "N/A");

                    // Writers
                    String writers = credits.getCrew().stream()
                            .filter(crew -> "Writer".equals(crew.getJob()) || "Screenplay".equals(crew.getJob()))
                            .map(TmdbCredits.Crew::getName)
                            .collect(Collectors.joining(", "));
                    writersValue.setText(writers.isEmpty() ? "N/A" : writers);
                } else {
                    Log.e("MovieDetailActivity", "Failed to load credits: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<TmdbCredits> call, Throwable t) {
                Log.e("MovieDetailActivity", "Credits API call failed: " + t.getMessage());
            }
        });
    }
}