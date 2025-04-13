package com.example.cinebook.moviedetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.bookingflow.FiestBookingTicketFlow;
import com.example.cinebook.network.Movie;
import com.example.cinebook.network.TmdbApi;
import com.example.cinebook.network.TmdbCredits;
import com.example.cinebook.network.TmdbMovieDetails;
import com.example.cinebook.network.TmdbReleaseDates;
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
    private TextView tmdbScore;
    private TextView tmdbTotalRatings;
    private TextView rottenScore;
    private TextView rottenTotal;
    private TextView imdbScore;
    private TextView imdbTotal;
    private TextView ratedValue;
    private TextView durationValue;
    private TextView languageValue;
    private TextView productionValue;
    private TextView cinemaName;
    private TextView cinemaAddress;
    private TextView cinemaDistance;
    private TextView selectedMonth;
    private TextView selectedDate;
    private TextView selectedShowtime;
    private TextView writersValue;
    private LinearLayout continueButton;
    private RecyclerView castRecyclerView;
    private TmdbApi tmdbApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_2_movie_detail);

        // Initialize views
        backArrow = findViewById(R.id.header_imageView_backArrow);
        moviePoster = findViewById(R.id.header_imageView_moviePoster);
        locationIcon = findViewById(R.id.cinema_imageView_locationIcon);
        movieTitle = findViewById(R.id.header_textView_movieTitle);
        synopsis = findViewById(R.id.header_textView_synopsis);
        rating = findViewById(R.id.header_textView_rating);
        duration = findViewById(R.id.header_textView_duration);
        tmdbScore = findViewById(R.id.ratings_textView_tmdb_score);
        tmdbTotalRatings = findViewById(R.id.ratings_textView_tmdb_total);
        rottenScore = findViewById(R.id.ratings_textView_rotten_score);
        rottenTotal = findViewById(R.id.ratings_textView_rotten_total);
        imdbScore = findViewById(R.id.ratings_textView_imdb_score);
        imdbTotal = findViewById(R.id.ratings_textView_imdb_total);
        ratedValue = findViewById(R.id.rated_textView_value);
        durationValue = findViewById(R.id.duration_textView_value);
        languageValue = findViewById(R.id.language_textView_value);
        productionValue = findViewById(R.id.production_textView_value);
        cinemaName = findViewById(R.id.cinema_textView_name);
        cinemaAddress = findViewById(R.id.cinema_textView_address);
        cinemaDistance = findViewById(R.id.cinema_textView_distance);
        selectedMonth = findViewById(R.id.cinema_textView_selected_month);
        selectedDate = findViewById(R.id.cinema_textView_selected_date);
        selectedShowtime = findViewById(R.id.cinema_textView_selected_showtime);
        writersValue = findViewById(R.id.writers_textView_value);
        continueButton = findViewById(R.id.continue_button);
        castRecyclerView = findViewById(R.id.cast_recyclerView);

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);

        // Setup RecyclerView
        castRecyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Back arrow click listener
        backArrow.setOnClickListener(v -> finish());

        // Continue button click listener
        continueButton.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailActivity.this, FiestBookingTicketFlow.class);
            Bundle extras = getIntent().getExtras();
            intent.putExtra("movie_title", movieTitle.getText().toString());
            intent.putExtra("selected_theater", cinemaName.getText().toString());
            intent.putExtra("selected_month", extras != null ? extras.getString("selected_month", "N/A") : "N/A");
            intent.putExtra("selected_date", extras != null ? extras.getString("selected_date", "N/A") : "N/A");
            intent.putExtra("selected_showtime", extras != null ? extras.getString("selected_showtime", "N/A") : "N/A");
            String posterPath = moviePoster.getTag() != null ? moviePoster.getTag().toString() : "";
            intent.putExtra("poster_path", posterPath);
            startActivity(intent);
        });

        // Get passed data
        Bundle extras = getIntent().getExtras();
        Movie movie = extras != null ? extras.getParcelable("movie") : null;
        String selectedMonthStr = extras != null ? extras.getString("selected_month", "N/A") : "N/A";
        String selectedDateStr = extras != null ? extras.getString("selected_date", "N/A") : "N/A";
        String selectedTheater = extras != null ? extras.getString("selected_theater", "N/A") : "N/A";
        String selectedTheaterAddress = extras != null ? extras.getString("selected_theater_address", "N/A") : "N/A";
        String selectedTheaterDistance = extras != null ? extras.getString("selected_theater_distance", "N/A") : "N/A";
        String selectedShowtimeStr = extras != null ? extras.getString("selected_showtime", "N/A") : "N/A";

        // Set cinema data
        cinemaName.setText(selectedTheater);
        cinemaAddress.setText(selectedTheaterAddress);
        cinemaDistance.setText(selectedTheaterDistance);
        selectedMonth.setText("Month: " + selectedMonthStr);
        selectedDate.setText("Date: " + selectedDateStr);
        selectedShowtime.setText("Showtime: " + selectedShowtimeStr.replace("_", " "));

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

                    // TMDb Score
                    String tmdbScoreStr = details.getVoteAverage() > 0 ?
                            String.format("%.1f", details.getVoteAverage()) : "N/A";
                    tmdbScore.setText(tmdbScoreStr);
                    String tmdbTotalStr = details.getVoteCount() > 0 ?
                            String.format("%d Ratings", details.getVoteCount()) : "N/A";
                    tmdbTotalRatings.setText(tmdbTotalStr);

                    // Simulated Rotten Tomatoes
                    String rottenScoreStr = details.getVoteAverage() > 0 ?
                            String.format("%d%%", (int) (details.getVoteAverage() * 10)) : "N/A";
                    rottenScore.setText(rottenScoreStr);
                    rottenTotal.setText(details.getVoteCount() > 0 ? "Critics Consensus" : "N/A");

                    // Simulated IMDb
                    String imdbScoreStr = details.getVoteAverage() > 0 ?
                            String.format("%.1f/10", details.getVoteAverage()) : "N/A";
                    imdbScore.setText(imdbScoreStr);
                    imdbTotal.setText(tmdbTotalStr);

                    // Duration
                    int runtime = details.getRuntime();
                    String durationStr = runtime > 0 ?
                            String.format("%dh %dm", runtime / 60, runtime % 60) : "N/A";
                    duration.setText(durationStr);
                    durationValue.setText(durationStr);

                    // Language
                    String language = details.getSpokenLanguages().isEmpty() ?
                            "N/A" : details.getSpokenLanguages().get(0).getEnglishName();
                    languageValue.setText(language);

                    // Production
                    String production = details.getProductionCompanies().isEmpty() ? "N/A" :
                            details.getProductionCompanies().stream()
                                    .map(TmdbMovieDetails.ProductionCompany::getName)
                                    .collect(Collectors.joining(", "));
                    productionValue.setText(production);

                    // Fetch certification
                    Call<TmdbReleaseDates> releaseCall = tmdbApi.getReleaseDates(movieId, apiKey);
                    releaseCall.enqueue(new Callback<TmdbReleaseDates>() {
                        @Override
                        public void onResponse(Call<TmdbReleaseDates> call, Response<TmdbReleaseDates> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                TmdbReleaseDates releaseDates = response.body();
                                String ratingStr = "N/A";
                                for (TmdbReleaseDates.ReleaseDateResult result : releaseDates.getResults()) {
                                    if ("US".equals(result.getCountryCode())) {
                                        for (TmdbReleaseDates.ReleaseDate date : result.getReleaseDates()) {
                                            if (date.getCertification() != null && !date.getCertification().isEmpty()) {
                                                ratingStr = date.getCertification();
                                                break;
                                            }
                                        }
                                        break;
                                    }
                                }
                                rating.setText(ratingStr);
                                ratedValue.setText(ratingStr);
                            } else {
                                rating.setText("N/A");
                                ratedValue.setText("N/A");
                            }
                        }

                        @Override
                        public void onFailure(Call<TmdbReleaseDates> call, Throwable t) {
                            Log.e("MovieDetailActivity", "Release dates API failed: " + t.getMessage());
                            rating.setText("N/A");
                            ratedValue.setText("N/A");
                        }
                    });
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
                    List<TmdbCredits.Cast> castList = credits.getCast().stream()
                            .limit(10)
                            .collect(Collectors.toList());
                    CastAdapter castAdapter = new CastAdapter(MovieDetailActivity.this, castList);
                    castRecyclerView.setAdapter(castAdapter);
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