package com.manilalinkup.app.activities;

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
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.JobPostRepository;

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

        jobListJobCard = JobPostRepository.getSeekerDashboardJobs(this);

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(SeekerDashboardActivity.this, SeekerJobPostActivity.class);
                job.putIntoIntent(intent);
                startActivity(intent);
            }
            @Override
            public void onRemoveClick(JobPostDashboardModel job, int position) {
            }
        }, false);
        recyclerViewJobPost.setAdapter(adapterJobPost);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view_seeker);
        bottomNavigationView.setSelectedItemId(R.id.nav_home_seeker);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home_seeker) {
                    return true;
                } else if (id == R.id.nav_profile_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, SeekerProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (id == R.id.nav_notifications_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, EmployerNotificationsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (id == R.id.nav_activity_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, SaveSeekerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (id == R.id.nav_chat_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, ChatEmployerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });
    }
}
