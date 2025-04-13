package com.example.cinebook.bookingflow;

import android.os.Bundle;
import com.example.cinebook.BaseActivity;
import com.example.cinebook.R;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setupBottomNavigation(R.id.nav_profile);
    }
}