package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SaveSeekerActivity extends AppCompatActivity {

    // In-memory storage for saved jobs
    public static List<SeekerJobModel> savedList = new ArrayList<>();

    RecyclerView recyclerView;
    View emptyState;
    BottomNavigationView bottomNavigationView;
    SavedJobsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_save_seeker);

        recyclerView = findViewById(R.id.rv_saved_jobs);
        emptyState = findViewById(R.id.empty_state_layout);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use the list defined in this class
        adapter = new SavedJobsAdapter(savedList);

        // Handle removal of saved jobs
        adapter.setOnRemoveClickListener(job -> {
            savedList.remove(job);
            loadSavedJobs();
        });

        recyclerView.setAdapter(adapter);

        loadSavedJobs();

        bottomNavigationView.setSelectedItemId(R.id.nav_my_activity);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, SeekerDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_my_activity) {
                return true;

            } else if (id == R.id.nav_chat) {
                Intent intent = new Intent(this, ChatSeekerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;

            } else if (id == R.id.nav_profile) {
                Intent intent = new Intent(this, SeekerProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }

    private void loadSavedJobs() {
        if (savedList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSavedJobs(); // auto refresh
        bottomNavigationView.setSelectedItemId(R.id.nav_my_activity);
    }
}
