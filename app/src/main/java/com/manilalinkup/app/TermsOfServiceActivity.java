package com.manilalinkup.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class TermsOfServiceActivity extends AppCompatActivity {

    // Declaring UI components
    private ImageButton btnBack;
    private Button btnAccept;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_service);

        btnBack = findViewById(R.id.btn_back_terms);
        btnAccept = findViewById(R.id.btn_accept_terms);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Better practice than finish() for standard navigation
            }
        });

        // 3. Set up the Accept Button Listener
        // Logic for both Seekers and Employers to acknowledge the terms
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTermsAcceptance();
            }
        });
    }

    private void handleTermsAcceptance() {
        // Provide feedback to the user
        Toast.makeText(this, "Terms accepted. Welcome to the Manila LinkUp community!", Toast.LENGTH_SHORT).show();

        // Optional: Finish activity to return to the dashboard/settings
        finish();
    }
}