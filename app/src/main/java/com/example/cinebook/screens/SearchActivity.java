package com.example.cinebook.screens;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.cinebook.R;

public class SearchActivity extends AppCompatActivity {

    private EditText editTextSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_search);

        // Explicitly cast views to avoid Glide ambiguity
        ImageView iconSearch = findViewById(R.id.icon_search);
        ImageView iconRecentMore = findViewById(R.id.icon_recent_more);
        ImageView iconRecentItem1 = findViewById(R.id.icon_recent_item_1);
        ImageView iconClearRecentItem1 = findViewById(R.id.icon_clear_recent_item_1);
        ImageView iconRecentItem2 = findViewById(R.id.icon_recent_item_2);
        ImageView iconClearRecentItem2 = findViewById(R.id.icon_clear_recent_item_2);
        ImageView dividerRecent = findViewById(R.id.divider_recent);
        ImageView iconTrendingMore = findViewById(R.id.icon_trending_more);
        ImageView dividerTrending = findViewById(R.id.divider_trending);

        // Load images with Glide
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/1cqi8tqq_expires_30_days.png").into(iconSearch);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/hvfi5h82_expires_30_days.png").into(iconRecentMore);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/0z22wvmw_expires_30_days.png").into(iconRecentItem1);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/808klikd_expires_30_days.png").into(iconClearRecentItem1);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/oliiixuo_expires_30_days.png").into(iconRecentItem2);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/a5ckhxmz_expires_30_days.png").into(iconClearRecentItem2);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/x6ywgvnn_expires_30_days.png").into(dividerRecent);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/xjo509ur_expires_30_days.png").into(iconTrendingMore);
        Glide.with(this).load("https://storage.googleapis.com/tagjs-prod.appspot.com/v1/d0sulmDxlH/l5pu1wri_expires_30_days.png").into(dividerTrending);

        // Initialize EditText
        editTextSearch = findViewById(R.id.edit_text_search);

        // Cancel button listener
        TextView cancelButton = findViewById(R.id.text_cancel);
        cancelButton.setOnClickListener(v -> {
            editTextSearch.setText("");
            finish();
        });
    }
}