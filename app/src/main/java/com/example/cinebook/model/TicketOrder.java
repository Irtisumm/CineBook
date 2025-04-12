package com.example.cinebook.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

public class TicketOrder implements Parcelable {
    private String movieTitle;
    private String moviePosterUrl;
    private String movieRating;
    private String movieDuration;
    private double movieScore;
    private int movieRatingsCount;
    private List<String> selectedSeats;
    private int ticketCount;
    private double ticketPrice;
    private String bundleName;
    private String bundleDetails;
    private double bundlePrice;
    private double tax;
    private String paymentMethod;
    private String cinemaLocation;
    private String showTime;
    private String studio;
    private String row;

    public TicketOrder() {
        selectedSeats = new ArrayList<>();
    }

    // Getters and Setters
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getMoviePosterUrl() { return moviePosterUrl; } // Fixed: was returning movieTitle
    public void setMoviePosterUrl(String moviePosterUrl) { this.moviePosterUrl = moviePosterUrl; }

    public String getMovieRating() { return movieRating; }
    public void setMovieRating(String movieRating) { this.movieRating = movieRating; }

    public String getMovieDuration() { return movieDuration; }
    public void setMovieDuration(String movieDuration) { this.movieDuration = movieDuration; }

    public double getMovieScore() { return movieScore; }
    public void setMovieScore(double movieScore) { this.movieScore = movieScore; }

    public int getMovieRatingsCount() { return movieRatingsCount; }
    public void setMovieRatingsCount(int movieRatingsCount) { this.movieRatingsCount = movieRatingsCount; }

    public List<String> getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(List<String> selectedSeats) { this.selectedSeats = selectedSeats; }

    public int getTicketCount() { return ticketCount; }
    public void setTicketCount(int ticketCount) { this.ticketCount = ticketCount; }

    public double getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(double ticketPrice) { this.ticketPrice = ticketPrice; }

    public String getBundleName() { return bundleName; }
    public void setBundleName(String bundleName) { this.bundleName = bundleName; }

    public String getBundleDetails() { return bundleDetails; }
    public void setBundleDetails(String bundleDetails) { this.bundleDetails = bundleDetails; }

    public double getBundlePrice() { return bundlePrice; }
    public void setBundlePrice(double bundlePrice) { this.bundlePrice = bundlePrice; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCinemaLocation() { return cinemaLocation; }
    public void setCinemaLocation(String cinemaLocation) { this.cinemaLocation = cinemaLocation; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public String getStudio() { return studio; }
    public void setStudio(String studio) { this.studio = studio; }

    public String getRow() { return row; }
    public void setRow(String row) { this.row = row; }

    // Parcelable implementation
    protected TicketOrder(Parcel in) {
        movieTitle = in.readString();
        moviePosterUrl = in.readString();
        movieRating = in.readString();
        movieDuration = in.readString();
        movieScore = in.readDouble();
        movieRatingsCount = in.readInt();
        selectedSeats = new ArrayList<>();
        in.readStringList(selectedSeats);
        ticketCount = in.readInt();
        ticketPrice = in.readDouble();
        bundleName = in.readString();
        bundleDetails = in.readString();
        bundlePrice = in.readDouble();
        tax = in.readDouble();
        paymentMethod = in.readString();
        cinemaLocation = in.readString();
        showTime = in.readString();
        studio = in.readString();
        row = in.readString();
    }

    public static final Creator<TicketOrder> CREATOR = new Creator<TicketOrder>() {
        @Override
        public TicketOrder createFromParcel(Parcel in) {
            return new TicketOrder(in);
        }

        @Override
        public TicketOrder[] newArray(int size) {
            return new TicketOrder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(movieTitle);
        dest.writeString(moviePosterUrl);
        dest.writeString(movieRating);
        dest.writeString(movieDuration);
        dest.writeDouble(movieScore);
        dest.writeInt(movieRatingsCount);
        dest.writeStringList(selectedSeats);
        dest.writeInt(ticketCount);
        dest.writeDouble(ticketPrice);
        dest.writeString(bundleName);
        dest.writeString(bundleDetails);
        dest.writeDouble(bundlePrice);
        dest.writeDouble(tax);
        dest.writeString(paymentMethod);
        dest.writeString(cinemaLocation);
        dest.writeString(showTime);
        dest.writeString(studio);
        dest.writeString(row);
    }
}