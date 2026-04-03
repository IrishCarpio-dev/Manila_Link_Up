package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView; // Added import

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class EmployerProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerViewRatings;
    private RecyclerView recyclerViewAllJobsPosted;
    private RatingsProfileAdapter adapterRating;
    private JobPostDashboardAdapter adapterAllJobPost;
    private List<RatingsProfileModel> ratingProfileList;
    private List<JobPostDashboardModel> allJobsPostedList;
    MaterialButton viewArchivedJobs;
    BottomNavigationView bottomNavigationViewEmployer;
    ImageView viewAllRatings;

    // 1. Declare the Settings Icon
    private ImageView settingsIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_profile);

        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        // 2. Initialize the Settings Icon
        settingsIcon = findViewById(R.id.image_view_employer_settings_icon);

        // 3. Set the Click Listener for Settings
        settingsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(EmployerProfileActivity.this, EmployerSettingsActivity.class);
            startActivity(intent);
        });

        recyclerViewRatings = findViewById(R.id.recycler_view_ratings_card);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRatings.setLayoutManager(layoutManager);
        recyclerViewRatings.setLayoutManager(layoutManager);

        //dummy data
        ratingProfileList = new ArrayList<>();
        ratingProfileList.add(new RatingsProfileModel("Mahusay na employer! Mabuhay ka! ", "Juan Dela Cruz", 5.0f));
        ratingProfileList.add(new RatingsProfileModel("Clear instructions and fast payment.", "Maria Clara", 4.5f));
        ratingProfileList.add(new RatingsProfileModel("Watta nice.", "Simoun Ibarra", 4.0f));

        adapterRating = new RatingsProfileAdapter(ratingProfileList);
        recyclerViewRatings.setAdapter(adapterRating);

        //For the recycler all jobs posted
        recyclerViewAllJobsPosted = findViewById(R.id.recycler_view_employer_jobs_posted);
        recyclerViewAllJobsPosted.setLayoutManager(new LinearLayoutManager(this));

        allJobsPostedList = new ArrayList<JobPostDashboardModel>();
        allJobsPostedList.add(new JobPostDashboardModel(
                "Barista",
                "Irish Cafe",
                "Makati City",
                "Full-time",
                R.drawable.frieren,
                "2h ago"
        ));
        allJobsPostedList.add(new JobPostDashboardModel(
                "Cat Sitter",
                "Castillo Family",
                "Sta.Ana, MAnila",
                "M | W | F",
                R.drawable.frieren,
                "8h ago"
        ));
        allJobsPostedList.add(new JobPostDashboardModel(
                "Store Assistant",
                "Nena Castro",
                "San Andres Bukid, MAnila",
                "Every Monday",
                R.drawable.frieren,
                "8h ago"
        ));


        adapterAllJobPost = new JobPostDashboardAdapter(allJobsPostedList, true);
        recyclerViewAllJobsPosted.setAdapter(adapterAllJobPost);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_profile);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem ->  {
            if(menuItem.getItemId() == R.id.nav_home){
                startActivity(new Intent(EmployerProfileActivity.this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications) {
                startActivity(new Intent(EmployerProfileActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_add_job) {
                startActivity(new Intent(EmployerProfileActivity.this, EmployerAddJobActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat) {
                startActivity(new Intent(EmployerProfileActivity.this, ChatEmployerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        viewAllRatings = findViewById(R.id.item_card_see_more_ratings_arrow);
        viewAllRatings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerProfileActivity.this, EmployerViewAllRatings.class);
                startActivity(intent);
            }
        });

        viewArchivedJobs = findViewById(R.id.button_view_archive_jobs);
        viewArchivedJobs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerProfileActivity.this, EmployerViewArchivedJobs.class);
                startActivity(intent);

            }
        });

    }
}