package com.example.cinebook.moviedetails;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.databinding.Activity12MovieDetailBinding;
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

    private Activity12MovieDetailBinding binding;
    private TmdbApi tmdbApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Activity12MovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        tmdbApi = retrofit.create(TmdbApi.class);

        // Back arrow click listener
        binding.headerImageViewBackArrow.setOnClickListener(v -> finish());

        // Get passed data
        Bundle extras = getIntent().getExtras();
        Movie movie = extras != null ? extras.getParcelable("movie") : null;
        String selectedDate = extras != null ? extras.getString("selected_date", "N/A") : "N/A";
        String selectedTheater = extras != null ? extras.getString("selected_theater", "N/A") : "N/A";
        String selectedTheaterAddress = extras != null ? extras.getString("selected_theater_address", "N/A") : "N/A";
        String selectedTheaterDistance = extras != null ? extras.getString("selected_theater_distance", "N/A") : "N/A";
        String selectedShowtime = extras != null ? extras.getString("selected_showtime", "N/A") : "N/A";

        // Set cinema data
        binding.cinemaTextViewName.setText(selectedTheater);
        binding.cinemaTextViewAddress.setText(selectedTheaterAddress);
        binding.cinemaTextViewDistance.setText(selectedTheaterDistance);
        binding.cinemaTextViewSelectedDate.setText("Date: " + selectedDate);
        binding.cinemaTextViewSelectedShowtime.setText("Showtime: " + selectedShowtime);

        // Load movie data from API
        if (movie != null) {
            binding.headerTextViewMovieTitle.setText(movie.getTitle()); // Immediate feedback
            loadMovieDetails(movie.getId());
            loadMovieCredits(movie.getId());
        } else {
            binding.headerTextViewMovieTitle.setText("Unknown Movie");
            binding.headerTextViewSynopsis.setText("No synopsis available.");
            Glide.with(this)
                    .load(R.drawable.placeholder)
                    .into(binding.headerImageViewMoviePoster);
        }

        // Set location icon
        Glide.with(this)
                .load(R.drawable.ic_menu_myplaces)
                .into(binding.cinemaImageViewLocationIcon);
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
                    Glide.with(MovieDetailActivity.this)
                            .load("https://image.tmdb.org/t/p/w500" + details.getPosterPath())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.error)
                            .into(binding.headerImageViewMoviePoster);

                    // Title
                    binding.headerTextViewMovieTitle.setText(details.getTitle());

                    // Synopsis
                    binding.headerTextViewSynopsis.setText(details.getOverview());

                    // Rating (TMDB vote average, no MPAA available)
                    String rating = details.getVoteAverage() > 0 ? String.format("%.1f", details.getVoteAverage()) : "N/A";
                    binding.headerTextViewRating.setText(rating);
                    binding.ratingsTextViewScore.setText(rating);
                    binding.ratingsTextViewTotalRatings.setText(details.getVoteCount() + " Ratings");
                    binding.ratedTextViewValue.setText(rating); // Use vote average for "Rated"

                    // Duration
                    int runtime = details.getRuntime();
                    String duration = runtime > 0 ? String.format("%dh %dm", runtime / 60, runtime % 60) : "N/A";
                    binding.headerTextViewDuration.setText(duration);
                    binding.durationTextViewValue.setText(duration);

                    // Language
                    String language = details.getSpokenLanguages().isEmpty() ? "N/A" :
                            details.getSpokenLanguages().get(0).getEnglishName();
                    binding.languageTextViewValue.setText(language);

                    // Production
                    String production = details.getProductionCompanies().isEmpty() ? "N/A" :
                            details.getProductionCompanies().stream()
                                    .map(TmdbMovieDetails.ProductionCompany::getName)
                                    .collect(Collectors.joining(", "));
                    binding.productionTextViewValue.setText(production);
                } else {
                    Log.e("MovieDetailActivity", "Failed to load movie details: " + response.message());
                    binding.headerTextViewSynopsis.setText("Failed to load details.");
                }
            }

            @Override
            public void onFailure(Call<TmdbMovieDetails> call, Throwable t) {
                Log.e("MovieDetailActivity", "API call failed: " + t.getMessage());
                binding.headerTextViewSynopsis.setText("Error loading data.");
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
                    binding.castTextViewActor1.setText(castNames.size() > 0 ? castNames.get(0) : "N/A");
                    binding.castTextViewActor2.setText(castNames.size() > 1 ? castNames.get(1) : "N/A");
                    binding.castTextViewActor3.setText(castNames.size() > 2 ? castNames.get(2) : "N/A");
                    binding.castTextViewActor4.setText(castNames.size() > 3 ? castNames.get(3) : "N/A");

                    // Writers
                    String writers = credits.getCrew().stream()
                            .filter(crew -> "Writer".equals(crew.getJob()) || "Screenplay".equals(crew.getJob()))
                            .map(TmdbCredits.Crew::getName)
                            .collect(Collectors.joining(", "));
                    binding.writersTextViewValue.setText(writers.isEmpty() ? "N/A" : writers);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}