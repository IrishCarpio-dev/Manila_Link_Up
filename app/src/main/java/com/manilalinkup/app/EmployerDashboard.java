package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class EmployerDashboard extends AppCompatActivity {

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    BottomNavigationView bottomNavigationViewEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_dashboard);
        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);

        recyclerViewJobPost = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        jobListJobCard = new ArrayList<>();
        mockData();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard);
        recyclerViewJobPost.setAdapter(adapterJobPost);


        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_home);
        bottomNavigationViewEmployer.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(EmployerDashboard.this, EmployerProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }

                return false;
            }
        });

    }

    private void mockData() {
        jobListJobCard.add(new JobPostDashboardModel(
                "Events/Catering Helper",
                "Eng Bee Tin",
                "Binondo, Manila",
                "March 30, 2026",
                R.drawable.chipsstarters,
                "3 days ago"
        ));
        jobListJobCard.add(new JobPostDashboardModel(
                "Cafe Barista",
                "Don Kopi",
                "Malate, Manila",
                "Full Time",
                R.drawable.mockdata_engbeeten,
                "7 days ago"
        ));

        jobListJobCard.add(new JobPostDashboardModel(
                "Store Assistant",
                "Quick Smart Express",
                "Quiapo, Manila",
                "M | W | F",
                R.drawable.sarisaristore,
                "10 days ago"
        ));

        jobListJobCard.add(new JobPostDashboardModel(
                "Artist Assistant",
                "BINI Mika's Company",
                "GMA, Manila",
                "T | Th | F",
                R.drawable.mikaemployer,
                "1 day ago"
        ));

        if (adapterJobPost != null) {
            adapterJobPost.notifyDataSetChanged();
        }
    }
}