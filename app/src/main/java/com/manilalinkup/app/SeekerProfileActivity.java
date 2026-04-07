package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SeekerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRatings;
    private RatingsProfileAdapter adapterRating;
    private List<RatingsProfileModel> ratingProfileList;

    // Added variables for Experience RecyclerView
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

        initializeViews();

        // Setup RecyclerViews
        setupRatingsRecyclerView();
        setupExperienceRecyclerView(); // New method call

        setupBottomNavigation();

        setupClickListeners();
    }

    private void initializeViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        settingsIcon = findViewById(R.id.image_view_seeker_settings_icon);
        summaryEditText = findViewById(R.id.editText_summary);

        // Initialize RecyclerViews
        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        recyclerViewExperience = findViewById(R.id.recycler_view_experience); // Added

        // Ensure the correct tab is highlighted in the bottom nav
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
    }

    private void setupRatingsRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRatings.setLayoutManager(layoutManager);

        ratingProfileList = new ArrayList<>();
        ratingProfileList.add(new RatingsProfileModel("Very hardworking and punctual!", "Juan Dela Cruz", 5.0f));
        ratingProfileList.add(new RatingsProfileModel("Great communication skills.", "Maria Clara", 4.5f));
        ratingProfileList.add(new RatingsProfileModel("Did the job perfectly.", "Simoun Ibarra", 4.0f));

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerViewRatings.setAdapter(adapterRating);
    }

    // New method for Experience RecyclerView implementation
    private void setupExperienceRecyclerView() {
        recyclerViewExperience.setLayoutManager(new LinearLayoutManager(this));

        experienceList = new ArrayList<>();
        // Dummy data for Work Experience
        experienceList.add(new ExperienceModel("Barista", "Starbucks - Manila", "Jan 2023 - Present"));
        experienceList.add(new ExperienceModel("Delivery Rider", "GrabFood PH", "June 2022 - Dec 2022"));
        experienceList.add(new ExperienceModel("Service Crew", "Jollibee Padre Faura", "Nov 2021 - May 2022"));

        adapterExperience = new ExperienceAdapter(experienceList);
        recyclerViewExperience.setAdapter(adapterExperience);
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
    }
}