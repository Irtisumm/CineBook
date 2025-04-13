package com.example.cinebook.bookingflow;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;
import com.example.cinebook.model.TicketOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookingConfirmationActivity extends BaseActivity {

    private TextView ticketTitleTextView;
    private TextView filterAllTextView;
    private TextView filterActiveTextView;
    private TextView filterPastTextView;
    private LinearLayout ticketItem1;
    private LinearLayout ticketItem2;

    private List<Ticket> allTickets;
    private List<Ticket> activeTickets;
    private List<Ticket> pastTickets;

    // Static list to receive tickets from MovieTicketActivity
    public static final List<TicketOrder> SHARED_TICKET_ORDERS = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        // Initialize Bottom Navigation
        setupBottomNavigation(R.id.nav_ticket);

        // Initialize views
        ticketTitleTextView = findViewById(R.id.ticket_title);
        filterAllTextView = findViewById(R.id.filter_all);
        filterActiveTextView = findViewById(R.id.filter_active);
        filterPastTextView = findViewById(R.id.filter_past);
        ticketItem1 = findViewById(R.id.ticket_item_1);
        ticketItem2 = findViewById(R.id.ticket_item_2);

        // Initialize ticket data from shared list
        initializeTicketData();

        // Set up filter tabs
        setupFilterTabs();

        // Display initial tickets (All)
        displayTickets("All");
    }

    private void initializeTicketData() {
        allTickets = new ArrayList<>();
        activeTickets = new ArrayList<>();
        pastTickets = new ArrayList<>();

        // Convert TicketOrder objects to Ticket objects
        for (TicketOrder order : SHARED_TICKET_ORDERS) {
            boolean isActive = isTicketActive(order.getShowTime());
            Ticket ticket = new Ticket(
                    order.getMovieTitle(),
                    order.getCinemaLocation() != null ? order.getCinemaLocation() : "Unknown",
                    order.getShowTime() != null ? order.getShowTime() : "Unknown",
                    order.getMoviePosterUrl(),
                    isActive
            );
            allTickets.add(ticket);
            if (isActive) {
                activeTickets.add(ticket);
            } else {
                pastTickets.add(ticket);
            }
        }

        // Add mock data only if no real tickets exist (optional, for testing)
        if (allTickets.isEmpty()) {
            Ticket mockTicket1 = new Ticket(
                    "Mission: Impossible - Dead Reckoning Part One",
                    "Gandaria City Mall",
                    "Saturday, 12 September 14:25 WIB",
                    "https://example.com/mission_impossible_poster.jpg", // Placeholder URL
                    true
            );
            Ticket mockTicket2 = new Ticket(
                    "Doctor Strange In The Multiverse Of Madness",
                    "Gandaria City Mall",
                    "Saturday, 5 September 14:25 WIB",
                    "https://example.com/doctor_strange_poster.jpg", // Placeholder URL
                    false
            );
            allTickets.add(mockTicket1);
            allTickets.add(mockTicket2);
            activeTickets.add(mockTicket1);
            pastTickets.add(mockTicket2);
        }
    }

    private boolean isTicketActive(String showTime) {
        if (showTime == null || showTime.isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM HH:mm z", Locale.getDefault());
            Date showDate = sdf.parse(showTime);
            Date currentDate = new Date();
            return showDate != null && showDate.after(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void setupFilterTabs() {
        setFilterTabState(filterAllTextView, true);

        filterAllTextView.setOnClickListener(v -> {
            setFilterTabState(filterAllTextView, true);
            setFilterTabState(filterActiveTextView, false);
            setFilterTabState(filterPastTextView, false);
            displayTickets("All");
        });

        filterActiveTextView.setOnClickListener(v -> {
            setFilterTabState(filterAllTextView, false);
            setFilterTabState(filterActiveTextView, true);
            setFilterTabState(filterPastTextView, false);
            displayTickets("Active");
        });

        filterPastTextView.setOnClickListener(v -> {
            setFilterTabState(filterAllTextView, false);
            setFilterTabState(filterActiveTextView, false);
            setFilterTabState(filterPastTextView, true);
            displayTickets("Past");
        });
    }

    private void setFilterTabState(TextView textView, boolean isSelected) {
        if (isSelected) {
            textView.setTextColor(ContextCompat.getColor(this, R.color.active_filter_color));
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.inactive_filter_color));
        }
    }

    private void displayTickets(String filter) {
        ticketItem1.setVisibility(View.GONE);
        ticketItem2.setVisibility(View.GONE);

        List<Ticket> ticketsToShow;
        switch (filter) {
            case "Active":
                ticketsToShow = activeTickets;
                break;
            case "Past":
                ticketsToShow = pastTickets;
                break;
            default:
                ticketsToShow = allTickets;
                break;
        }

        if (ticketsToShow.size() > 0) {
            populateTicketItem(ticketItem1, ticketsToShow.get(0));
            ticketItem1.setVisibility(View.VISIBLE);
        }
        if (ticketsToShow.size() > 1) {
            populateTicketItem(ticketItem2, ticketsToShow.get(1));
            ticketItem2.setVisibility(View.VISIBLE);
        }
    }

    private void populateTicketItem(LinearLayout ticketItem, Ticket ticket) {
        ImageView ticketImage = ticketItem.findViewById(ticketItem.getId() == R.id.ticket_item_1 ? R.id.ticket_image_1 : R.id.ticket_image_2);
        TextView ticketTitle = ticketItem.findViewById(ticketItem.getId() == R.id.ticket_item_1 ? R.id.ticket_title_1 : R.id.ticket_title_2);
        TextView locationText = ticketItem.findViewById(ticketItem.getId() == R.id.ticket_item_1 ? R.id.location_text_1 : R.id.location_text_2);
        TextView timeText = ticketItem.findViewById(ticketItem.getId() == R.id.ticket_item_1 ? R.id.time_text_1 : R.id.time_text_2);

        // Load image (URL or resource)
        if (ticket.getImageUrl() != null && !ticket.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(ticket.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ticketImage);
        } else {
            Glide.with(this)
                    .load(R.mipmap.ic_launcher)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .into(ticketImage);
        }

        ticketTitle.setText(ticket.getTitle());
        locationText.setText(ticket.getLocation());
        timeText.setText(ticket.getTime());
    }

    // Updated Ticket model class to handle URL
    private static class Ticket {
        private final String title;
        private final String location;
        private final String time;
        private final String imageUrl;
        private final boolean isActive;

        public Ticket(String title, String location, String time, String imageUrl, boolean isActive) {
            this.title = title;
            this.location = location;
            this.time = time;
            this.imageUrl = imageUrl;
            this.isActive = isActive;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation() {
            return location;
        }

        public String getTime() {
            return time;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public boolean isActive() {
            return isActive;
        }
    }
}