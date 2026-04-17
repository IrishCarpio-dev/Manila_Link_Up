package com.manilalinkup.app.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.AppliedJobsAdapter;
import com.manilalinkup.app.adapters.SavedJobsAdapter;
import com.manilalinkup.app.models.JobPostDashboardModel;

import java.util.ArrayList;
import java.util.List;

public class AppliedSeekerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private TextView savedTab, appliedTab;
    private View indicatorSaved, indicatorApplied;
    private SavedJobsAdapter savedAdapter;
    private AppliedJobsAdapter appliedAdapter;
    private List<JobPostDashboardModel> savedList;
    private List<JobPostDashboardModel> appliedList;
    BottomNavigationView bottomNavigationView;

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
        savedTab = findViewById(R.id.saved_tab);
        appliedTab = findViewById(R.id.applied_tab);
        indicatorSaved = findViewById(R.id.indicator_saved);
        indicatorApplied = findViewById(R.id.indicator_applied);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        savedList = new ArrayList<>();
        appliedList = new ArrayList<>();
        setupMockData();

        initAdapters();

        recyclerView.setAdapter(savedAdapter);

        savedTab.setOnClickListener(v -> switchToSaved());
        appliedTab.setOnClickListener(v -> switchToApplied());
        switchToSaved();
    }

    private void switchToSaved() {
        savedTab.setTextColor(Color.parseColor("#1B3E9C"));
        appliedTab.setTextColor(Color.parseColor("#888888"));

        indicatorSaved.setVisibility(View.VISIBLE);
        indicatorSaved.setBackgroundColor(Color.parseColor("#1B3E9C"));

        indicatorApplied.setVisibility(View.INVISIBLE);

        recyclerView.setAdapter(savedAdapter);
        checkEmptyState(savedList);
    }

    private void switchToApplied() {
        appliedTab.setTextColor(Color.parseColor("#1B3E9C"));
        savedTab.setTextColor(Color.parseColor("#888888"));

        indicatorApplied.setVisibility(View.VISIBLE);
        indicatorApplied.setBackgroundColor(Color.parseColor("#1B3E9C"));

        indicatorSaved.setVisibility(View.INVISIBLE);

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
        String resPath = "android.resource://" + getPackageName() + "/";

        // Mock Data for SAVED Tab
        savedList.add(new JobPostDashboardModel("Barista", "Don Kopi", "Malate, Manila", "Full-time",
                "https://images.pexels.com/photos/312418/pexels-photo-312418.jpeg", "2h ago"));
        savedList.add(new JobPostDashboardModel("Store Assistant", "Eng Bee Tin", "Binondo", "Full-time",
                resPath + R.drawable.mockdata_engbeeten, "8h ago"));

        // Mock Data for APPLIED Tab
        appliedList.add(new JobPostDashboardModel("Messenger", "LBC Express", "Pasay City", "Full-time",
                "https://images.pexels.com/photos/4391470/pexels-photo-4391470.jpeg", "1 day ago"));
        appliedList.add(new JobPostDashboardModel("Dishwasher", "Manila Buffet", "Ermita, Manila", "Part-time",
                "https://images.pexels.com/photos/2290753/pexels-photo-2290753.jpeg", "3 days ago"));
    }

    private void initAdapters() {
        savedAdapter = new SavedJobsAdapter(savedList, new SavedJobsAdapter.OnSavedJobClickListener() {
            @Override
            public void onRemoveClick(int position) {
                if (position >= 0 && position < savedList.size()) {
                    savedList.remove(position);
                    savedAdapter.notifyItemRemoved(position);
                    savedAdapter.notifyItemRangeChanged(position, savedList.size());

                    if (savedList.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onJobClick(JobPostDashboardModel job) {
                // View Details Intent would go here
            }
        });

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