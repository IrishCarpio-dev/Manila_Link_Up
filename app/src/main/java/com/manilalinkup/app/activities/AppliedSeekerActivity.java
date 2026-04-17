package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.AppliedJobsAdapter;
import com.manilalinkup.app.models.JobPostDashboardModel;

import java.util.ArrayList;
import java.util.List;

public class AppliedSeekerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private AppliedJobsAdapter appliedAdapter;
    private List<JobPostDashboardModel> appliedList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applied_seeker);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_activity_seeker);
        bottomNavigationView.setOnItemSelectedListener(menuItem ->  {

            if(menuItem.getItemId() == R.id.nav_home_seeker){
                startActivity(new Intent(AppliedSeekerActivity.this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications_seeker) {
                //will change once notification activity has been added
                startActivity(new Intent(AppliedSeekerActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat_seeker) {
                startActivity(new Intent(AppliedSeekerActivity.this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_profile_seeker) {
                startActivity(new Intent(AppliedSeekerActivity.this, SeekerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        recyclerView = findViewById(R.id.recycler_view_employer_own_posts);
        emptyState = findViewById(R.id.empty_state_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appliedList = new ArrayList<>();

        setupMockData();
        initAdapter();

        recyclerView.setAdapter(appliedAdapter);
        checkEmptyState(appliedList);

    }

    private void checkEmptyState(List<?> list) {
        if (list.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void setupMockData() {
        appliedList.add(new JobPostDashboardModel("Messenger", "LBC Express", "Pasay City", "Full-time",
                "https://images.pexels.com/photos/4391470/pexels-photo-4391470.jpeg", "1 day ago"));
        appliedList.add(new JobPostDashboardModel("Dishwasher", "Manila Buffet", "Ermita, Manila", "Part-time",
                "https://images.pexels.com/photos/2290753/pexels-photo-2290753.jpeg", "3 days ago"));
    }

    private void initAdapter() {
        appliedAdapter = new AppliedJobsAdapter(appliedList, new AppliedJobsAdapter.OnAppliedJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(AppliedSeekerActivity.this, AppliedJobPostActivity.class);
                intent.putExtra("JOB_TITLE", job.getJobTitle());
                intent.putExtra("EMPLOYER_NAME", job.getEmployerName());
                intent.putExtra("LOCATION", job.getJobPostLocation());
                intent.putExtra("PFP_URL", job.getEmployerProfilePicture());
                startActivity(intent);
            }
        });
    }
}