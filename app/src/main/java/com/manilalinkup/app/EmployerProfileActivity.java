package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView; // Added import

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class EmployerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RatingsProfileAdapter adapterRating;
    private List<RatingsProfileModel> ratingProfileList;
    BottomNavigationView bottomNavigationViewEmployer;

    // 1. Declare the Settings Icon
    private ImageView settingsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_profile);

        // 2. Initialize the Settings Icon
        settingsIcon = findViewById(R.id.image_view_employer_settings_icon);

        // 3. Set the Click Listener for Settings
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerProfileActivity.this, EmployerSettingsActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.recycler_view_ratings_card);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // dummy data
        ratingProfileList = new ArrayList<>();
        ratingProfileList.add(new RatingsProfileModel("Mahusay na employer! Mabuhay ka! ", "Juan Dela Cruz", 5.0f));
        ratingProfileList.add(new RatingsProfileModel("Clear instructions and fast payment.", "Maria Clara", 4.5f));
        ratingProfileList.add(new RatingsProfileModel("Watta nice.", "Simoun Ibarra", 4.0f));

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerView.setAdapter(adapterRating);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_profile);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem ->  {
            if(menuItem.getItemId() == R.id.nav_home){
                startActivity(new Intent(EmployerProfileActivity.this, EmployerDashboard.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return true;
        });
    }
}