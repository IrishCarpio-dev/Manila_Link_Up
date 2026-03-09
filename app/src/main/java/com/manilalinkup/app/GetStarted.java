package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.appbar.MaterialToolbar;

public class GetStarted extends AppCompatActivity {

    CardView jobSeekerCard;
    CardView employerCard;
    TextView backToLogin;


    MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        jobSeekerCard = findViewById(R.id.card_view_job_seeker);
        employerCard = findViewById(R.id.card_view_employer);
        backToLogin = findViewById(R.id.text_view_back_log_in);


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

        employerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jobSeekerActivityIntent = new Intent(GetStarted.this, EmployerSIgnUp.class);
                startActivity(jobSeekerActivityIntent);
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backToLoginActivityIntent = new Intent(GetStarted.this, MainActivity.class);
                startActivity(backToLoginActivityIntent);
                Toast.makeText(GetStarted.this, "Please try logging in.", Toast.LENGTH_LONG).show();;
            }
        });

    }
}