package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.SeekerDashboardAdapter;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.utilities.JobPostRepository;

public class SavedJobsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_dashboard);

        RecyclerView seekerDashboardRecycler = findViewById(R.id.recycler_view_job_posts_dashboard);
        if (seekerDashboardRecycler != null) {
            seekerDashboardRecycler.setLayoutManager(new LinearLayoutManager(this));
            seekerDashboardRecycler.setAdapter(
                    new SeekerDashboardAdapter(
                            JobPostRepository.getEmployerArchiveJobs(this),
                            this::openJobDetails
                    )
            );
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view_seeker);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_activity_seeker);
            bottomNavigationView.setOnItemSelectedListener(item -> true);
        }
    }

    private void openJobDetails(JobPostDashboardModel jobPost) {
        Intent intent = new Intent(this, SeekerJobDetailsActivity.class);
        jobPost.putIntoIntent(intent);
        startActivity(intent);
    }
}
