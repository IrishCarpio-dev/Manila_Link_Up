package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class EmployerSignUp extends AppCompatActivity {

    MaterialToolbar toolbar;
    MaterialButton sendOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_sign_up);
        sendOTP = findViewById(R.id.material_button_send_otp);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendOTPEmployerActivityIntent = new Intent(EmployerSignUp.this, EmployerDashboard.class);
                startActivity(sendOTPEmployerActivityIntent);
                Toast.makeText(EmployerSignUp.this,"Verification Code sent.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}