package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.ExperienceAdapter;
import com.manilalinkup.app.adapters.RatingsProfileAdapter;
import com.manilalinkup.app.models.ExperienceModel;
import com.manilalinkup.app.models.RatingsProfileModel;

import java.util.ArrayList;
import java.util.List;

public class SeekerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRatings;
    private RatingsProfileAdapter adapterRating;
    private List<RatingsProfileModel> ratingProfileList;

    private RecyclerView recyclerViewExperience;
    private ExperienceAdapter adapterExperience;
    private List<ExperienceModel> experienceList;

    private BottomNavigationView bottomNavigationView;
    private ImageView settingsIcon;
    private TextInputEditText summaryEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_profile);

        // 1. Initialize all views first
        initializeViews();

        // 2. Setup Data and Adapters
        setupFeedbacksRecyclerView();
        setupExperienceRecyclerView();

        // 3. Setup Interactions
        setupBottomNavigation();
        setupClickListeners();
    }

    private void initializeViews() {
        settingsIcon = findViewById(R.id.image_view_seeker_settings_icon);
        summaryEditText = findViewById(R.id.editText_summary);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        recyclerViewExperience = findViewById(R.id.recycler_view_experience);

        // Set the initial state of the navigation bar
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        }
    }

    private void setupFeedbacksRecyclerView() {
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRatings.setLayoutManager(horizontalLayout);

        ratingProfileList = new ArrayList<>();
        ratingProfileList.add(new RatingsProfileModel("Very hardworking and punctual!", "Juan Dela Cruz", 5.0f));
        ratingProfileList.add(new RatingsProfileModel("Great communication skills.", "Maria Clara", 4.5f));
        ratingProfileList.add(new RatingsProfileModel("Did the job perfectly.", "Simoun Ibarra", 4.0f));

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerViewRatings.setAdapter(adapterRating);
    }

    private void setupExperienceRecyclerView() {
        recyclerViewExperience.setLayoutManager(new LinearLayoutManager(this));

        experienceList = new ArrayList<>();
        experienceList.add(new ExperienceModel("Barista", "Starbucks - Manila", "Jan 2023 - Present"));
        experienceList.add(new ExperienceModel("Delivery Rider", "GrabFood PH", "June 2022 - Dec 2022"));
        experienceList.add(new ExperienceModel("Service Crew", "Jollibee Padre Faura", "Nov 2021 - May 2022"));

        adapterExperience = new ExperienceAdapter(experienceList);
        recyclerViewExperience.setAdapter(adapterExperience);
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                // Navigate to Dashboard/Home
                Intent intent = new Intent(this, SeekerDashboardActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                // We don't call finish() so the user can go back to Profile
                return true;
            } else if (id == R.id.nav_profile) {
                // Already here, do nothing
                return true;
            }
            // Add logic for nav_bills, nav_savings, nav_cards as you create them
            return false;
        });
    }

    private void setupClickListeners() {
        if (settingsIcon != null) {
            settingsIcon.setOnClickListener(v -> {
                Intent intent = new Intent(SeekerProfileActivity.this, SeekerSettingsActivity.class);
                startActivity(intent);
            });
        }

        if (summaryEditText != null) {
            summaryEditText.setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    // Logic to save profile summary text
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // FIXED: Added null safety for the BottomNav and the specific menu item
        if (bottomNavigationView != null) {
            MenuItem profileItem = bottomNavigationView.getMenu().findItem(R.id.nav_profile);
            if (profileItem != null) {
                profileItem.setChecked(true);
            }
        }
    }
}