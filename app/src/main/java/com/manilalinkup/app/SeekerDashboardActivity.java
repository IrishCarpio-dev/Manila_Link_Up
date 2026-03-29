package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

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

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_dashboard);

        recyclerViewJobPost = findViewById(R.id.recycler_view_job_posts_dashboard);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));

        jobListJobCard = new ArrayList<>();
        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard);
        
        // Handle "Save" button click from Dashboard
        adapterJobPost.setOnSaveClickListener(job -> {
            if (!SavedJobs.savedList.contains(job)) {
                SavedJobs.savedList.add(job);
                Toast.makeText(this, "Job Saved!", Toast.LENGTH_SHORT).show();
                
                // Remove from local list and update UI immediately
                int position = jobListJobCard.indexOf(job);
                if (position != -1) {
                    jobListJobCard.remove(position);
                    adapterJobPost.notifyItemRemoved(position);
                }
            }
        });
        
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
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshJobList();
    }

    private void refreshJobList() {
        jobListJobCard.clear();
        
        // Get all available jobs (simulating a data source)
        List<JobPostDashboardModel> allJobs = getAllAvailableJobs();
        
        // Only add jobs that are NOT in the saved list
        for (JobPostDashboardModel job : allJobs) {
            boolean isSaved = false;
            for (JobPostDashboardModel savedJob : SavedJobs.savedList) {
                if (savedJob.equals(job)) {
                    isSaved = true;
                    break;
                }
            }
            if (!isSaved) {
                jobListJobCard.add(job);
            }
        }
        
        adapterJobPost.notifyDataSetChanged();
    }

    private List<JobPostDashboardModel> getAllAvailableJobs() {
        List<JobPostDashboardModel> list = new ArrayList<>();
        list.add(new JobPostDashboardModel("Events/Catering Helper", "Eng Bee Tin", "Binondo, Manila", "March 30, 2026", R.drawable.chipsstarters, "3 days ago"));
        list.add(new JobPostDashboardModel("Cafe Barista", "Don Kopi", "Malate, Manila", "Full Time", R.drawable.mockdata_engbeeten, "7 days ago"));
        list.add(new JobPostDashboardModel("Store Assistant", "Quick Smart Express", "Quiapo, Manila", "M | W | F", R.drawable.sarisaristore, "10 days ago"));
        list.add(new JobPostDashboardModel("Artist Assistant", "BINI Mika's Company", "GMA, Manila", "T | Th | F", R.drawable.mikaemployer, "1 day ago"));
        return list;
    }
}
