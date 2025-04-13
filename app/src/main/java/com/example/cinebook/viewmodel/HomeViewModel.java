package com.example.cinebook.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cinebook.network.ApiClient;
import com.example.cinebook.network.Movie;
import com.example.cinebook.network.MovieResponse;
import com.example.cinebook.network.Review;
import com.example.cinebook.network.ReviewResponse;
import com.example.cinebook.network.TMDbApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> location = new MutableLiveData<>("Kuala Lumpur");
    private final MutableLiveData<List<Movie>> nowShowingMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> topRatedMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Movie>> upcomingMovies = new MutableLiveData<>();
    private final MutableLiveData<List<Review>> movieNews = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(true);
    private final MutableLiveData<List<Movie>> filteredMovies = new MutableLiveData<>();

    private final TMDbApiService apiService = ApiClient.getApiService();
    private final String apiKey = "..";

    public HomeViewModel() {
        fetchAllData();
    }

    public LiveData<String> getLocation() {
        return location;
    }

    public void setLocation(String newLocation) {
        location.setValue(newLocation);
    }

    public LiveData<List<Movie>> getNowShowingMovies() {
        return nowShowingMovies;
    }

    public LiveData<List<Movie>> getTopRatedMovies() {
        return topRatedMovies;
    }

    public LiveData<List<Movie>> getUpcomingMovies() {
        return upcomingMovies;
    }

    public LiveData<List<Review>> getMovieNews() {
        return movieNews;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<List<Movie>> getFilteredMovies() {
        return filteredMovies;
    }

    // Combine all movies for search
    public List<Movie> getAllMovies() {  // Now public
        List<Movie> allMovies = new ArrayList<>();
        if (nowShowingMovies.getValue() != null) allMovies.addAll(nowShowingMovies.getValue());
        if (topRatedMovies.getValue() != null) allMovies.addAll(topRatedMovies.getValue());
        if (upcomingMovies.getValue() != null) allMovies.addAll(upcomingMovies.getValue());
        return allMovies;
    }

    // Filter movies based on search query
    public void filterMovies(String query) {
        List<Movie> allMovies = getAllMovies();
        if (query == null || query.trim().isEmpty()) {
            filteredMovies.setValue(allMovies);
            return;
        }

        List<Movie> filtered = new ArrayList<>();
        for (Movie movie : allMovies) {
            if (movie.getTitle() != null && movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(movie);
            }
        }
        filteredMovies.setValue(filtered);
    }

    private void fetchAllData() {
        fetchNowPlaying();
        fetchTopRated();
        fetchUpcoming();
        fetchMovieNews();
    }

    private void fetchNowPlaying() {
        isLoading.setValue(true);
        apiService.getNowPlaying(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    nowShowingMovies.setValue(response.body().getResults());
                    filterMovies(""); // Initialize filteredMovies with all movies
                } else {
                    error.setValue("Failed to load now playing movies");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error: " + t.getMessage());
            }
        });
    }

    private void fetchTopRated() {
        isLoading.setValue(true);
        apiService.getTopRated(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    topRatedMovies.setValue(response.body().getResults());
                    filterMovies(""); // Update filteredMovies
                } else {
                    error.setValue("Failed to load top rated movies");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error: " + t.getMessage());
            }
        });
    }

    private void fetchUpcoming() {
        isLoading.setValue(true);
        apiService.getUpcoming(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    upcomingMovies.setValue(response.body().getResults());
                    filterMovies(""); // Update filteredMovies
                } else {
                    error.setValue("Failed to load upcoming movies");
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {
                isLoading.setValue(false);
                error.setValue("Error: " + t.getMessage());
            }
        });
    }

    private void fetchMovieNews() {
        isLoading.setValue(true);
        isLoading.setValue(false);
        movieNews.setValue(getDemoNews());
    }

    private List<Review> getDemoNews() {
        List<Review> demoNews = new ArrayList<>();

        Review review1 = new Review();
        review1.setAuthor("SciFiScribe");
        review1.setContent("Breaking news: 'Quantum Rift,' a sci-fi epic set to release in July 2025, promises to redefine space travel with its mind-bending visuals and a stellar cast led by Mia Torres. Sources say it’s a mix of Interstellar and The Matrix!");
        demoNews.add(review1);

        Review review2 = new Review();
        review2.setAuthor("ActionAce");
        review2.setContent("Get ready for 'Steel Vengeance,' hitting theaters in September 2025! This action-packed thriller stars Liam Drake as a rogue cyborg on a mission to save humanity. Early buzz hints at jaw-dropping stunts and a killer soundtrack.");
        demoNews.add(review2);

        Review review3 = new Review();
        review3.setAuthor("FantasyFanatic");
        review3.setContent("Fantasy lovers rejoice! 'The Crystal Throne,' slated for December 2025, unveils a magical realm ruled by dragons. Starring newcomer Zara Lin, it’s rumored to be the next big franchise after Lord of the Rings.");
        demoNews.add(review3);

        return demoNews;
    }
}