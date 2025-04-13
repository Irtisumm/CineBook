package com.example.cinebook.network;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TmdbReleaseDates {
    @SerializedName("results")
    private List<ReleaseDateResult> results;

    public List<ReleaseDateResult> getResults() {
        return results;
    }

    public static class ReleaseDateResult {
        @SerializedName("iso_3166_1")
        private String countryCode;

        @SerializedName("release_dates")
        private List<ReleaseDate> releaseDates;

        public String getCountryCode() {
            return countryCode;
        }

        public List<ReleaseDate> getReleaseDates() {
            return releaseDates;
        }
    }

    public static class ReleaseDate {
        @SerializedName("certification")
        private String certification;

        public String getCertification() {
            return certification;
        }
    }
}