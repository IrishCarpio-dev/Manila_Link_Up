package com.manilalinkup.app.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.manilalinkup.app.adapters.EmployerAllRatingsAdapter;
import com.manilalinkup.app.models.EmployerAllRatingsModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class EmployerViewAllRatings extends AppCompatActivity {

    MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private EmployerAllRatingsAdapter adapter;
    private List<EmployerAllRatingsModel> ratingsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_all_ratings);

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

        recyclerView = findViewById(R.id.recycler_ratings_views);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 2. Create Dummy Data
        ratingsList = new ArrayList<>();

        // Add 3-4 dummy ratings to see how it scrolls
        ratingsList.add(new EmployerAllRatingsModel(
                "user_01",
                "https://images.pexels.com/photos/774909/pexels-photo-774909.jpeg", // Placeholder image URL
                "Maria",
                "Santos",
                5.0f,
                System.currentTimeMillis(),
                "Very professional as an employer. Madali syang kausap and straight to the point."
        ));

        ratingsList.add(new EmployerAllRatingsModel(
                "user_02",
                "https://images.pexels.com/photos/1222271/pexels-photo-1222271.jpeg",
                "Juan",
                "Dela Cruz",
                4.5f,
                System.currentTimeMillis() - 86400000, // 1 day ago
                "Great work ethic. Was very patient to me kahit natututo pa lang ako."
        ));

        ratingsList.add(new EmployerAllRatingsModel(
                "user_03",
                "https://images.pexels.com/photos/220453/pexels-photo-220453.jpeg",
                "Carl",
                "Reyes",
                4.0f,
                System.currentTimeMillis() - (86400000 * 2), // 2 days ago
                "Reliable companion. Mabait po sya and very reasonable."
        ));
        ratingsList.add(new EmployerAllRatingsModel(
                "user_03",
                "https://via.placeholder.com/150",
                "Magdalena",
                "Castillano",
                5.0f,
                System.currentTimeMillis() - (86400000 * 2), // 2 days ago
                "Hire niyo po ako ule sa susunod. Thank you!"
        ));

        // 3. Set the Adapter
        adapter = new EmployerAllRatingsAdapter(ratingsList);
        recyclerView.setAdapter(adapter);
    }
}