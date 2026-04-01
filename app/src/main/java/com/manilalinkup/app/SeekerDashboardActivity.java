package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;

public class SeekerDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SeekerJobAdapter homeAdapter;
    private List<SeekerJobModel> homeJobList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_dashboard);

        recyclerView = findViewById(R.id.recycler_view_job_posts_dashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        homeJobList = new ArrayList<>();
        mockData();

        homeAdapter = new SeekerJobAdapter(homeJobList);
        recyclerView.setAdapter(homeAdapter);
        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false);
        recyclerViewJobPost.setAdapter(adapterJobPost);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_my_activity) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, SaveSeekerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, SeekerProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }

    private void mockData() {
        homeJobList.add(new SeekerJobModel(
                "Events/Catering Helper",
                "Eng Bee Tin",
                "Binondo, Manila",
                "March 30, 2026",
                R.drawable.chipsstarters,
                "3 days ago"
        ));

        homeJobList.add(new SeekerJobModel(
                "Cafe Barista",
                "Don Kopi",
                "Malate, Manila",
                "Full Time",
                R.drawable.mockdata_engbeeten,
                "7 days ago"
        ));

        homeJobList.add(new SeekerJobModel(
                "Store Assistant",
                "Quick Smart Express",
                "Quiapo, Manila",
                "M | W | F",
                R.drawable.sarisaristore,
                "10 days ago"
        ));

        homeJobList.add(new SeekerJobModel(
                "Artist Assistant",
                "BINI Mika's Company",
                "GMA, Manila",
                "T | Th | F",
                R.drawable.mikaemployer,
                "1 day ago"
        ));
    }
}
