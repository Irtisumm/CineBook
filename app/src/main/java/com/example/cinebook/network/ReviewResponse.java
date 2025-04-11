package com.example.cinebook.network;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ReviewResponse {
    @SerializedName("results")
    private List<Review> results;

    public List<Review> getResults() {
        return results;
    }
}