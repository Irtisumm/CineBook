package com.example.cinebook.screens;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;
import com.example.cinebook.adapters.MovieAdapter;
import com.example.cinebook.adapters.NewsAdapter;
import com.example.cinebook.databinding.ActivityHomeScreenBinding;
import com.example.cinebook.network.Movie;
import com.example.cinebook.viewmodel.HomeViewModel;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends BaseActivity {
    private ActivityHomeScreenBinding binding;
    private HomeViewModel viewModel;
    private ArrayAdapter<String> autocompleteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize Bottom Navigation
        setupBottomNavigation(R.id.nav_home);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        // Load header icons
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/search_icon.png")
                .into(binding.iconSearch);

        // Set up RecyclerViews
        binding.recyclerNowShowing.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerTopChart.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerComingSoon.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerMovieNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Observe movie data and load into RecyclerViews
        viewModel.getNowShowingMovies().observe(this, movies -> {
            android.util.Log.d("HomeScreen", "Now Showing movies loaded: " + movies.size());
            binding.progressNowShowing.setVisibility(View.GONE);
            binding.recyclerNowShowing.setVisibility(View.VISIBLE);
            binding.recyclerNowShowing.setAdapter(new MovieAdapter(this, movies));
            updateAutocompleteSuggestions();
        });

        viewModel.getTopRatedMovies().observe(this, movies -> {
            android.util.Log.d("HomeScreen", "Top Rated movies loaded: " + movies.size());
            binding.progressTopChart.setVisibility(View.GONE);
            binding.recyclerTopChart.setVisibility(View.VISIBLE);
            binding.recyclerTopChart.setAdapter(new MovieAdapter(this, movies));
            updateAutocompleteSuggestions();
        });

        viewModel.getUpcomingMovies().observe(this, movies -> {
            android.util.Log.d("HomeScreen", "Upcoming movies loaded: " + movies.size());
            binding.progressComingSoon.setVisibility(View.GONE);
            binding.recyclerComingSoon.setVisibility(View.VISIBLE);
            binding.recyclerComingSoon.setAdapter(new MovieAdapter(this, movies, true)); // Pass true for Coming Soon
            updateAutocompleteSuggestions();
        });

        viewModel.getMovieNews().observe(this, news -> {
            binding.progressMovieNews.setVisibility(View.GONE);
            binding.recyclerMovieNews.setVisibility(View.VISIBLE);
            binding.recyclerMovieNews.setAdapter(new NewsAdapter(news));
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        // Set up search with autocomplete
        autocompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        binding.editSearch.setAdapter(autocompleteAdapter);
        updateAutocompleteSuggestions();

        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Observe filtered movies for search results
        viewModel.getFilteredMovies().observe(this, filteredMovies -> {
            binding.recyclerNowShowing.setAdapter(new MovieAdapter(this, filteredMovies));
            if (filteredMovies.isEmpty() && !binding.editSearch.getText().toString().isEmpty()) {
                Toast.makeText(HomeScreen.this, "No movies found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateAutocompleteSuggestions() {
        List<Movie> allMovies = viewModel.getAllMovies();
        List<String> movieTitles = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getTitle() != null && !movieTitles.contains(movie.getTitle())) {
                movieTitles.add(movie.getTitle());
            }
        }
        autocompleteAdapter.clear();
        autocompleteAdapter.addAll(movieTitles);
        autocompleteAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}