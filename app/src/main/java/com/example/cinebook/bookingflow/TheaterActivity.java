package com.example.cinebook.bookingflow;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;

public class TheaterActivity extends BaseActivity {

    private AutoCompleteTextView searchInput;
    private ImageButton searchButton;
    private TextView cityName;
    private ImageButton cityDropdownButton;
    private LinearLayout[] cinemaItems;
    private String[] theaterNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);
        setupBottomNavigation(R.id.nav_theater);

        // Initialize views
        searchInput = findViewById(R.id.search_input);
        searchButton = findViewById(R.id.search_button);
        cityName = findViewById(R.id.city_name);
        cityDropdownButton = findViewById(R.id.city_dropdown_button);

        // Initialize cinema items
        cinemaItems = new LinearLayout[] {
                findViewById(R.id.cinema_item_1),
                findViewById(R.id.cinema_item_2),
                findViewById(R.id.cinema_item_3),
                findViewById(R.id.cinema_item_4),
                findViewById(R.id.cinema_item_5),
                findViewById(R.id.cinema_item_6),
                findViewById(R.id.cinema_item_7),
                findViewById(R.id.cinema_item_8),
                findViewById(R.id.cinema_item_9),
                findViewById(R.id.cinema_item_10)
        };

        // Theater names for autocomplete and filtering
        theaterNames = new String[] {
                "TGV Suria KLCC",
                "MBO The Starling Mall",
                "GSC Pavilion Kuala Lumpur",
                "TGV 1 Utama",
                "GSC Mid Valley Megamall",
                "TGV Sunway Pyramid",
                "GSC IOI City Mall",
                "TGV AEON Bukit Tinggi",
                "GSC The Gardens Mall",
                "TGV Gurney Paragon"
        };

        // Setup autocomplete for search input
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                theaterNames
        );
        searchInput.setAdapter(adapter);

        // Search button click listener
        searchButton.setOnClickListener(v -> filterTheaters(searchInput.getText().toString()));

        // Real-time filtering as user types
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filterTheaters(s.toString());
            }
        });

        // Setup city dropdown menu
        cityDropdownButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(TheaterActivity.this, cityDropdownButton);
            popup.getMenu().add("Kuala Lumpur");
            popup.getMenu().add("Selangor");
            popup.getMenu().add("Penang");
            popup.setOnMenuItemClickListener(item -> {
                cityName.setText(item.getTitle());
                filterTheatersByCity(item.getTitle().toString());
                return true;
            });
            popup.show();
        });
    }

    private void filterTheaters(String query) {
        query = query.toLowerCase().trim();
        for (int i = 0; i < cinemaItems.length; i++) {
            LinearLayout cinemaItem = cinemaItems[i];
            String theaterName = theaterNames[i].toLowerCase();
            if (query.isEmpty() || theaterName.contains(query)) {
                cinemaItem.setVisibility(View.VISIBLE);
            } else {
                cinemaItem.setVisibility(View.GONE);
            }
        }
    }

    private void filterTheatersByCity(String city) {
        for (int i = 0; i < cinemaItems.length; i++) {
            LinearLayout cinemaItem = cinemaItems[i];
            TextView addressView = cinemaItem.findViewById(getResources().getIdentifier("cinema_address_" + (i + 1), "id", getPackageName()));
            String address = addressView.getText().toString().toLowerCase();
            boolean isVisible = false;

            switch (city.toLowerCase()) {
                case "kuala lumpur":
                    isVisible = address.contains("kuala lumpur");
                    break;
                case "selangor":
                    isVisible = address.contains("selangor") || address.contains("petaling jaya") || address.contains("putrajaya") || address.contains("klang");
                    break;
                case "penang":
                    isVisible = address.contains("penang") || address.contains("george town");
                    break;
            }

            cinemaItem.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        }

        // If search query exists, apply it after city filter
        String query = searchInput.getText().toString();
        if (!query.isEmpty()) {
            filterTheaters(query);
        }
    }
}