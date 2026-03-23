package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class SeekerProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ScrollView mainScrollView;
    private TabLayout tabLayout;
    private View careerView, availView, feedbackView;
    MaterialToolbar toolbar;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_profile);

        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Intent intent = new Intent(SeekerProfileActivity.this, SeekerDashboardActivity.class);
                    startActivity(intent);
                    return true;
                } else if (id == R.id.nav_profile) {
                    return true;
                }

                return false;
            }
        });

        // 1. Initialize Views
        mainScrollView = findViewById(R.id.main_scroll_view);
        tabLayout = findViewById(R.id.profile_tabs);
        careerView = findViewById(R.id.section_career);
        availView = findViewById(R.id.section_availability);
        feedbackView = findViewById(R.id.section_feedback);

        populateExampleData();

        setupTabScrolling();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


    }

    private void populateExampleData() {
        // --- Career History Example ---
        TextView careerText = (TextView) ((androidx.cardview.widget.CardView) careerView).getChildAt(0);
        careerText.setText("• Service Crew at Jollibee (6 months)\n" +
                "• Delivery Rider for Lalamove (1 year)\n" +
                "• Part-time Janitorial Staff - SM Manila");

        // --- Availability Example ---
        TextView availText = (TextView) ((androidx.cardview.widget.CardView) availView).getChildAt(0);
        availText.setText("• Monday - Friday: 5:00 PM onwards\n" +
                "• Saturday & Sunday: All day\n" +
                "• Preferred Location: Manila / Makati area");

        LinearLayout feedbackLayout = (LinearLayout) feedbackView.findViewById(android.R.id.content);
        View feedbackPlaceholder = feedbackView.findViewById(R.id.section_feedback).findViewById(android.R.id.content);
    }

    private void setupTabScrolling() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View target;
                switch (tab.getPosition()) {
                    case 0: target = careerView; break;
                    case 1: target = availView; break;
                    case 2: target = feedbackView; break;
                    default: target = careerView; break;
                }
                if (target != null) {
                    scrollToView(target);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) { onTabSelected(tab); }
        });
    }

    private void scrollToView(View target) {
        mainScrollView.post(() -> {
            // Added 10px offset so the tab bar doesn't cover the card title
            int scrollToY = target.getTop();
            mainScrollView.smoothScrollTo(0, scrollToY);
        });
    }
}