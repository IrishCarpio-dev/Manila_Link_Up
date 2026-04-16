package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.manilalinkup.app.R;

public class SeekerJobPreferences extends AppCompatActivity {
    AutoCompleteTextView rateDropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_job_preferences);

        rateDropdown = findViewById(R.id.auto_complete_rate);
        String[] durationUnits = {"hour(s)", "day(s)", "week(s)", "month(s)", "year(s)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, durationUnits);
        rateDropdown.setAdapter(adapter);


    }
}