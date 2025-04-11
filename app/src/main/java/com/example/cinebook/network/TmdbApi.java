package com.example.cinebook.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbApi {
    @GET("movie/now_playing")
    Call<MovieResponse> getNowPlaying(@Query("494b3fe65fb0e929af45cab610c73858") String apiKey);

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRated(@Query("494b3fe65fb0e929af45cab610c73858") String apiKey);

    @GET("movie/upcoming")
    Call<MovieResponse> getUpcoming(@Query("494b3fe65fb0e929af45cab610c73858") String apiKey);
}