package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.manilalinkup.app.R;

public class SeekerVerificationRejectedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_verification_rejected);

        findViewById(R.id.btnSetupAgain).setOnClickListener(v -> {
            Intent intent = new Intent(this, EditSeekerProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {}
}
