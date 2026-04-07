package com.manilalinkup.app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SeekerPrivacyControlsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Switch switchPublicProfile;
    private TextView btnDownloadData, btnClearHistory, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure this filename matches your XML filename exactly!
        setContentView(R.layout.activity_seeker_privacy_and_data_controls);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        // FIXED: Added the initialization for btnBack
        btnBack = findViewById(R.id.btn_back_privacy);

        switchPublicProfile = findViewById(R.id.switch_public_profile);
        btnDownloadData = findViewById(R.id.btn_download_data);
        btnClearHistory = findViewById(R.id.btn_clear_history);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
    }

    private void setupListeners() {
        // FIXED: Added a null check to prevent the crash shown in your screenshot
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (switchPublicProfile != null) {
            switchPublicProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String message = isChecked ? "Profile is now Public" : "Profile is now Private";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            });
        }

        if (btnDownloadData != null) {
            btnDownloadData.setOnClickListener(v -> {
                Toast.makeText(this, "Preparing your data archive. Check your email soon.", Toast.LENGTH_LONG).show();
            });
        }

        if (btnClearHistory != null) {
            btnClearHistory.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Clear History")
                        .setMessage("Are you sure you want to clear your job search history?")
                        .setPositiveButton("Clear", (dialog, which) -> {
                            Toast.makeText(this, "History cleared", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Delete Account")
                        .setMessage("This action is permanent. Proceed?")
                        .setPositiveButton("Delete Forever", (dialog, which) -> {
                            Toast.makeText(this, "Account deletion request submitted.", Toast.LENGTH_LONG).show();
                            finishAffinity();
                        })
                        .setNegativeButton("Keep Account", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            });
        }
    }
}