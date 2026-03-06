package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.MaterialToolbar;

public class GetStarted extends AppCompatActivity {

    CardView jobSeekerCard;
    CardView employerCard;

    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        jobSeekerCard = findViewById(R.id.card_view_job_seeker);
        employerCard = findViewById(R.id.card_view_employer);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        jobSeekerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jobSeekerActivityIntent = new Intent(GetStarted.this, SignUpActivity.class);
                startActivity(jobSeekerActivityIntent);
            }
        });

    }
}