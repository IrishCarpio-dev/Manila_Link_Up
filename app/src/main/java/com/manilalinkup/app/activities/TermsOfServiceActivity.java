package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.manilalinkup.app.R;

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
                onBackPressed();
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleTermsAcceptance();
            }
        });
    }

    private void handleTermsAcceptance() {

        Toast.makeText(this, "Terms accepted. Welcome to the Manila LinkUp community!", Toast.LENGTH_SHORT).show();
        finish();
    }
}