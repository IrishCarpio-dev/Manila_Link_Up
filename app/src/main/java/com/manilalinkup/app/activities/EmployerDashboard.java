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

        recyclerViewJobPost = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        jobListJobCard = new ArrayList<>();
        mockData();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(EmployerDashboard.this, EmployerViewJobPost.class);
                intent.putExtra("JOB_TITLE", job.getJobTitle());
                intent.putExtra("EMPLOYER_NAME", job.getEmployerName());
                startActivity(intent);
            }
            @Override
            public void onRemoveClick(JobPostDashboardModel job, int position) {
            }
        });

        recyclerViewJobPost.setAdapter(adapterJobPost);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
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
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_notifications) {
                    Intent intent = new Intent(EmployerDashboard.this, EmployerNotificationsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_add_job) {
                    Intent intent = new Intent(EmployerDashboard.this, EmployerAddJobActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_chat) {
                    Intent intent = new Intent(EmployerDashboard.this, ChatEmployerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
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
                "https://en.wikipedia.org/wiki/Eng_Bee_Tin",
                "3 days ago"
        ));
        jobListJobCard.add(new JobPostDashboardModel(
                "Cafe Barista",
                "Don Kopi",
                "Malate, Manila",
                "Full Time",
                "https://www.freepik.com/vectors/coffee-shop-logo-design",
                "7 days ago"
        ));

        jobListJobCard.add(new JobPostDashboardModel(
                "Store Assistant",
                "Quick Smart Express",
                "Quiapo, Manila",
                "M | W | F",
                "https://venngage.com/templates/logos/market-store-creative-logo-fc8535df-be09-4c80-8ea5-a69a34b2318e",
                "10 days ago"
        ));

        jobListJobCard.add(new JobPostDashboardModel(
                "Artist Assistant",
                "BINI Mika's Company",
                "GMA, Manila",
                "T | Th | F",
                "https://www.thebeautyedit.ph/people/bini-members-and-their-beauty-looks/",
                "1 day ago"
        ));

        if (adapterJobPost != null) {
            adapterJobPost.notifyDataSetChanged();
        }
    }
}
