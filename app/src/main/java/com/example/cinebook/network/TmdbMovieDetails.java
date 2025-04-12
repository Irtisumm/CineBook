package com.example.cinebook.network;

import java.util.List;

public class TmdbMovieDetails {
    private String title;
    private String overview;
    private String poster_path;
    private double vote_average;
    private int vote_count;
    private int runtime;
    private List<Language> spoken_languages;
    private List<ProductionCompany> production_companies;

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return poster_path;
    }

    public double getVoteAverage() {
        return vote_average;
    }

    public int getVoteCount() {
        return vote_count;
    }

    public int getRuntime() {
        return runtime;
    }

    public List<Language> getSpokenLanguages() {
        return spoken_languages;
    }

    public List<ProductionCompany> getProductionCompanies() {
        return production_companies;
    }

    public static class Language {
        private String english_name;

        public String getEnglishName() {
            return english_name;
        }
    }

    public static class ProductionCompany {
        private String name;

        public String getName() {
            return name;
        }
    }
}