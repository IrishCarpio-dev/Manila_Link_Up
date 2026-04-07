package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;

public class EmployerAddJobActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationViewEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_add_job);


        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_add_job);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem ->  {

            if(menuItem.getItemId() == R.id.nav_home){
                startActivity(new Intent(EmployerAddJobActivity.this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications) {
                startActivity(new Intent(EmployerAddJobActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat) {
                startActivity(new Intent(EmployerAddJobActivity.this, ChatEmployerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(EmployerAddJobActivity.this, EmployerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

    }
}