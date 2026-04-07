package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

        initializeViews();
        setupFeedbacksRecyclerView();
        setupExperienceRecyclerView();
        setupBottomNavigation();
        setupClickListeners();
    }

    private void initializeViews() {
        settingsIcon = findViewById(R.id.image_view_seeker_settings_icon);
        summaryEditText = findViewById(R.id.editText_summary);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        recyclerViewExperience = findViewById(R.id.recycler_view_experience);

        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
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
                startActivity(new Intent(this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                return true;
            }
            return false;
        });
    }

    private void setupClickListeners() {
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(SeekerProfileActivity.this, SeekerSettingsActivity.class);
            startActivity(intent);
        });

        summaryEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // Potential place to save data to Firebase
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure the correct menu item is checked when returning to this activity
        bottomNavigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
    }
}