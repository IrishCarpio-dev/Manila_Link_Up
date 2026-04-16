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
        mockData();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(SeekerDashboardActivity.this, SeekerJobPostActivity.class);
                //not yet tested - irish
                startActivity(intent);
            }
            @Override
            public void onRemoveClick(JobPostDashboardModel job, int position) {
            }
        });
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
                    //Will set to SeekerNotifications pa, pending task for loraine
                    Intent intent = new Intent(SeekerDashboardActivity.this, SeekerNotificationActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (id == R.id.nav_activity_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, SaveSeekerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (id == R.id.nav_chat_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, ChatSeekerActivity.class);
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