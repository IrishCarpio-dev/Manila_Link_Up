package com.manilalinkup.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EmployerProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);
            bottomNavigationView.setOnItemSelectedListener(item -> true);
        }
    }
}
