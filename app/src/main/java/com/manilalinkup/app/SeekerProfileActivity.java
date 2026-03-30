package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

public class SeekerProfileActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private ImageView settingsIcon;
    private TextInputEditText summaryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_profile);

        // 1. Initialize UI Elements from XML
        initializeViews();

        // 2. Setup Navigation
        setupBottomNavigation();

        // 3. Setup Click Listeners
        setupClickListeners();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        settingsIcon = findViewById(R.id.image_view_seeker_settings_icon);
        summaryEditText = findViewById(R.id.editText_summary);

        // Ensure the correct tab is highlighted in the bottom nav
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    startActivity(new Intent(SeekerProfileActivity.this, SeekerDashboardActivity.class));
                    overridePendingTransition(0, 0);
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }
                return false;
            }
        });
    }

    private void setupClickListeners() {
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SeekerProfileActivity.this, SeekerSettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Theme-check logic removed as the feature is no longer supported.
    }
}