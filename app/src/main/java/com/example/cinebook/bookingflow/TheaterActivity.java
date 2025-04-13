package com.example.cinebook.bookingflow;

import android.os.Bundle;
import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;

public class TheaterActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theater);
        setupBottomNavigation(R.id.nav_theater);
    }
}