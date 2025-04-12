package com.example.cinebook.moviedetails;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;
import com.example.cinebook.databinding.Activity11MovieDetailBinding;
import com.example.cinebook.databinding.LayoutTheaterItemBinding;
import com.example.cinebook.network.Movie;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MovieDetail extends AppCompatActivity {

    private Activity11MovieDetailBinding binding;
    private int selectedDateIndex = -1;
    private int selectedTheaterIndex = -1;
    private String selectedShowtime = null;
    private final List<View> dateViews = new ArrayList<>();
    private final List<LayoutTheaterItemBinding> theaterBindings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Activity11MovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get movie from Intent
        Movie movie = getIntent().getParcelableExtra("movie");
        if (movie != null) {
            populateMovieData(movie);
        }

        // Setup back button
        binding.layoutMovieHeader.iconBack.setOnClickListener(v -> finish());

        // Setup continue button
        binding.buttonContinue.setOnClickListener(v -> onContinueClicked());

        // Setup dynamic dates
        setupDateOptions();

        // Setup theater data
        setupTheaterData();
    }

    private void populateMovieData(Movie movie) {
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(binding.layoutMovieHeader.imageMoviePoster);

        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/23btlndi_expires_30_days.png")
                .into(binding.layoutMovieHeader.iconBack);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/j25eddo6_expires_30_days.png")
                .into(binding.layoutMovieHeader.iconShare);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/3bc558bs_expires_30_days.png")
                .into(binding.layoutMovieHeader.iconFavorite);

        binding.layoutMovieDetails.textDetailsTitle.setText(movie.getTitle());
        binding.layoutMovieDetails.textDetailsDescription.setText(movie.getOverview());
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/5voi2tbq_expires_30_days.png")
                .into(binding.layoutMovieDetails.dividerDetails);

        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/5ngiab93_expires_30_days.png")
                .into(binding.layoutShowtimes.iconTheatersDropdown);
        Glide.with(this)
                .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/75a63500_expires_30_days.png")
                .into(binding.layoutShowtimes.dividerShowtimes);
    }

    private void setupDateOptions() {
        LinearLayout dateContainer = binding.layoutShowtimes.layoutDateOptions;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.US);

        dateContainer.removeAllViews();
        dateViews.clear();

        for (int i = 0; i < 6; i++) {
            View dateView = LayoutInflater.from(this)
                    .inflate(R.layout.layout_date_item, dateContainer, false);
            TextView dayText = dateView.findViewById(R.id.text_day);
            TextView dateText = dateView.findViewById(R.id.text_date);

            dayText.setText(dayFormat.format(calendar.getTime()));
            dateText.setText(dateFormat.format(calendar.getTime()));

            final int index = i;
            dateView.setOnClickListener(v -> selectDate(index));

            dateViews.add(dateView);
            dateContainer.addView(dateView);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        selectDate(0);
    }

    private void selectDate(int index) {
        if (selectedDateIndex == index) return;

        if (selectedDateIndex >= 0 && selectedDateIndex < dateViews.size()) {
            View prevDateView = dateViews.get(selectedDateIndex);
            TextView prevDayText = prevDateView.findViewById(R.id.text_day);
            TextView prevDateText = prevDateView.findViewById(R.id.text_date);
            prevDateView.setBackgroundResource(0);
            prevDayText.setTextColor(ContextCompat.getColor(this, R.color.black_191b1c));
            prevDateText.setTextColor(ContextCompat.getColor(this, R.color.black_191b1c));
        }

        View dateView = dateViews.get(index);
        TextView dayText = dateView.findViewById(R.id.text_day);
        TextView dateText = dateView.findViewById(R.id.text_date);
        dateView.setBackgroundResource(R.drawable.cr8b2466fd);
        dayText.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        dateText.setTextColor(ContextCompat.getColor(this, android.R.color.white));

        selectedDateIndex = index;

        selectedShowtime = null;
        binding.buttonContinue.setEnabled(false);
        updateTheaterShowtimes();
    }

    private void setupTheaterData() {
        LinearLayout theatersContainer = binding.layoutShowtimes.layoutTheatersList;
        List<Theater> theaters = generateDummyTheaterData();

        theatersContainer.removeAllViews();
        theaterBindings.clear();

        for (int i = 0; i < theaters.size(); i++) {
            Theater theater = theaters.get(i);
            LayoutTheaterItemBinding theaterBinding = LayoutTheaterItemBinding.inflate(
                    LayoutInflater.from(this), theatersContainer, false);

            theaterBinding.textTheaterName.setText(theater.name);
            theaterBinding.textTheaterDistance.setText(theater.distance);
            theaterBinding.textTheaterAddress.setText(theater.address);

            Glide.with(this)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/ez6mxp5r_expires_30_days.png")
                    .into(theaterBinding.iconTheaterSeparator);
            Glide.with(this)
                    .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/46ugn84n_expires_30_days.png")
                    .into(theaterBinding.iconTheaterArrow);

            LinearLayout showtimesContainer = new LinearLayout(this);
            showtimesContainer.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            showtimesContainer.setOrientation(LinearLayout.VERTICAL);
            showtimesContainer.setTag("showtimes_" + i);

            addShowtimesSection(showtimesContainer, "REGULAR", "IDR 40,000", theater.regularShowtimes, i);
            addShowtimesSection(showtimesContainer, "IMAX", "IDR 55,000", theater.imaxShowtimes, i);
            addShowtimesSection(showtimesContainer, "PREMIERE", "IDR 70,000", theater.premiereShowtimes, i);

            theaterBinding.layoutTheaterDetails.addView(showtimesContainer);

            final int index = i;
            theaterBinding.getRoot().setOnClickListener(v -> selectTheater(index));

            theaterBindings.add(theaterBinding);
            theatersContainer.addView(theaterBinding.getRoot());

            if (i < theaters.size() - 1) {
                ImageView divider = new ImageView(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
                divider.setScaleType(ImageView.ScaleType.FIT_XY);
                Glide.with(this)
                        .load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/6w6w2x8n_expires_30_days.png")
                        .into(divider);
                theatersContainer.addView(divider);
            }
        }

        selectTheater(0);
    }

    private void selectTheater(int index) {
        if (selectedTheaterIndex == index) return;

        if (selectedTheaterIndex >= 0 && selectedTheaterIndex < theaterBindings.size()) {
            LayoutTheaterItemBinding prevBinding = theaterBindings.get(selectedTheaterIndex);
            prevBinding.getRoot().setBackgroundResource(0);
            prevBinding.textTheaterName.setTextColor(ContextCompat.getColor(this, R.color.black_191b1c));
        }

        LayoutTheaterItemBinding theaterBinding = theaterBindings.get(index);
        theaterBinding.getRoot().setBackgroundResource(R.drawable.selected_background);
        theaterBinding.textTheaterName.setTextColor(ContextCompat.getColor(this, R.color.selected_text));

        selectedTheaterIndex = index;

        selectedShowtime = null;
        binding.buttonContinue.setEnabled(false);
        updateTheaterShowtimes();
    }

    private void addShowtimesSection(LinearLayout container, String title, String price, List<String> times, int theaterIndex) {
        LinearLayout section = new LinearLayout(this);
        section.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        section.setOrientation(LinearLayout.VERTICAL);
        section.setPadding(0, 0, 0, 28);

        LinearLayout header = new LinearLayout(this);
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(android.view.Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, 20);

        TextView titleView = new TextView(this);
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        titleView.setText(title);
        titleView.setTextColor(ContextCompat.getColor(this, R.color.gray_656e72));
        titleView.setTextSize(14);
        titleView.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView priceView = new TextView(this);
        priceView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        priceView.setText(price);
        priceView.setTextColor(ContextCompat.getColor(this, R.color.gray_656e72));
        priceView.setTextSize(12);
        priceView.setGravity(android.view.Gravity.END);

        header.addView(titleView);
        header.addView(priceView);

        section.addView(header);

        LinearLayout timesRow = new LinearLayout(this);
        timesRow.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        timesRow.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < times.size(); i++) {
            String time = times.get(i);
            LinearLayout timeLayout = new LinearLayout(this);
            timeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            timeLayout.setBackgroundResource(R.drawable.scbcfd1sw1cr4);
            timeLayout.setPadding(0, 8, 0, 8);
            timeLayout.setGravity(android.view.Gravity.CENTER);
            timeLayout.setTag("time_" + theaterIndex + "_" + title + "_" + time);

            TextView timeView = new TextView(this);
            timeView.setId(R.id.showtime_text);
            timeView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            timeView.setText(time);
            timeView.setTextColor(ContextCompat.getColor(this, R.color.black_191b1c));
            timeView.setTextSize(12);
            timeView.setTypeface(null, android.graphics.Typeface.BOLD);

            timeLayout.addView(timeView);
            timesRow.addView(timeLayout);

            timeLayout.setOnClickListener(v -> selectShowtime(theaterIndex, title, time));

            if (i < times.size() - 1) {
                View spacer = new View(this);
                spacer.setLayoutParams(new LinearLayout.LayoutParams(13, 0));
                timesRow.addView(spacer);
            }
        }

        section.addView(timesRow);
        container.addView(section);
    }

    private void selectShowtime(int theaterIndex, String showType, String time) {
        if (selectedTheaterIndex != theaterIndex) {
            selectTheater(theaterIndex);
        }

        if (selectedShowtime != null) {
            View prevTimeView = binding.layoutShowtimes.layoutTheatersList
                    .findViewWithTag("time_" + selectedTheaterIndex + "_" + selectedShowtime);
            if (prevTimeView != null) {
                prevTimeView.setBackgroundResource(R.drawable.scbcfd1sw1cr4);
                TextView prevTimeText = prevTimeView.findViewById(R.id.showtime_text);
                if (prevTimeText != null) {
                    prevTimeText.setTextColor(ContextCompat.getColor(this, R.color.black_191b1c));
                }
            }
        }

        String tag = "time_" + theaterIndex + "_" + showType + "_" + time;
        View timeView = binding.layoutShowtimes.layoutTheatersList.findViewWithTag(tag);
        if (timeView != null) {
            timeView.setBackgroundResource(R.drawable.selected_background);
            TextView timeText = timeView.findViewById(R.id.showtime_text);
            if (timeText != null) {
                timeText.setTextColor(ContextCompat.getColor(this, R.color.selected_text));
            }
        }

        selectedShowtime = showType + "_" + time;
        binding.buttonContinue.setEnabled(true);
    }

    private void updateTheaterShowtimes() {
        for (int i = 0; i < theaterBindings.size(); i++) {
            LinearLayout showtimesContainer = (LinearLayout) theaterBindings.get(i).layoutTheaterDetails
                    .findViewWithTag("showtimes_" + i);
            if (showtimesContainer != null) {
                showtimesContainer.setVisibility(i == selectedTheaterIndex ? View.VISIBLE : View.GONE);
            }
        }
    }

    private void onContinueClicked() {
        String selectedDate = "";
        if (selectedDateIndex >= 0 && selectedDateIndex < dateViews.size()) {
            TextView dateText = dateViews.get(selectedDateIndex).findViewById(R.id.text_date);
            TextView dayText = dateViews.get(selectedDateIndex).findViewById(R.id.text_day);
            selectedDate = dayText.getText() + " " + dateText.getText();
        }

        String selectedTheater = "";
        String selectedTheaterAddress = "";
        String selectedTheaterDistance = "";
        if (selectedTheaterIndex >= 0 && selectedTheaterIndex < theaterBindings.size()) {
            selectedTheater = theaterBindings.get(selectedTheaterIndex).textTheaterName.getText().toString();
            selectedTheaterAddress = theaterBindings.get(selectedTheaterIndex).textTheaterAddress.getText().toString();
            selectedTheaterDistance = theaterBindings.get(selectedTheaterIndex).textTheaterDistance.getText().toString();
        }

        Movie movie = getIntent().getParcelableExtra("movie");

        String logMessage = String.format(
                "Booking: Date=%s, Theater=%s, Showtime=%s",
                selectedDate, selectedTheater, selectedShowtime
        );
        Log.d("MovieDetail", logMessage);

        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra("movie", movie);
        intent.putExtra("selected_date", selectedDate);
        intent.putExtra("selected_theater", selectedTheater);
        intent.putExtra("selected_theater_address", selectedTheaterAddress);
        intent.putExtra("selected_theater_distance", selectedTheaterDistance);
        intent.putExtra("selected_showtime", selectedShowtime);
        startActivity(intent);
    }

    private List<Theater> generateDummyTheaterData() {
        List<Theater> theaters = new ArrayList<>();
        Random random = new Random();

        List<String> theaterNames = Arrays.asList(
                "GSC Mid Valley Megamall",
                "TGV Suria KLCC",
                "MBO The Starling Mall",
                "GSC Pavilion Kuala Lumpur",
                "TGV 1 Utama"
        );

        List<String> addresses = Arrays.asList(
                "Mid Valley Megamall, Kuala Lumpur",
                "Suria KLCC, Kuala Lumpur",
                "The Starling Mall, Petaling Jaya",
                "Pavilion Kuala Lumpur, Kuala Lumpur",
                "1 Utama Shopping Centre, Petaling Jaya"
        );

        List<String> showtimes = Arrays.asList(
                "11:00", "11:45", "13:00", "16:00", "17:30", "19:00", "19:45", "20:30"
        );

        for (int i = 0; i < theaterNames.size(); i++) {
            Theater theater = new Theater();
            theater.name = theaterNames.get(i);
            theater.address = addresses.get(i);
            theater.distance = String.format("%.1f KM", random.nextFloat() * 10);
            theater.regularShowtimes = showtimes.subList(0, random.nextInt(4) + 4);
            theater.imaxShowtimes = showtimes.subList(0, random.nextInt(3) + 1);
            theater.premiereShowtimes = showtimes.subList(0, random.nextInt(3) + 1);
            theaters.add(theater);
        }

        return theaters;
    }

    private static class Theater {
        String name;
        String address;
        String distance;
        List<String> regularShowtimes;
        List<String> imaxShowtimes;
        List<String> premiereShowtimes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}