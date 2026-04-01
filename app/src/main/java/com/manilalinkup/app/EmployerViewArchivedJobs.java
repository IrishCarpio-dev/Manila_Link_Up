package com.manilalinkup.app;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class EmployerViewArchivedJobs extends AppCompatActivity {

    MaterialToolbar toolbar;
    RecyclerView recyclerView;
    ArchiveJobAdapter adapter;
    List<ArchiveJobModel> archiveList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_archived_jobs);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        recyclerView = findViewById(R.id.recycler_view_employer_archived_jobs);

        archiveList = new ArrayList<>();
        loadDummyData();

        adapter = new ArchiveJobAdapter(archiveList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadDummyData() {
        archiveList.add(new ArchiveJobModel(
                "101",
                "Barista (Full Time)",
                "EXPIRED (2 days ago)",
                "Insight: 8 applicants were waiting for review."
        ));

        archiveList.add(new ArchiveJobModel(
                "102",
                "Kitchen Helper",
                "ARCHIVED (1 week ago)",
                "Insight: Manually moved to archive."
        ));

        archiveList.add(new ArchiveJobModel(
                "103",
                "Delivery Rider",
                "EXPIRED (5 days ago)",
                "Insight: 15 views, 0 applicants."
        ));
    }
}