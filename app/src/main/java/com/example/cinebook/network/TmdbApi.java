package com.example.cinebook.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApi {
    @GET("movie/{movie_id}")
    Call<TmdbMovieDetails> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Call<TmdbCredits> getMovieCredits(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/release_dates")
    Call<TmdbReleaseDates> getReleaseDates(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );
}