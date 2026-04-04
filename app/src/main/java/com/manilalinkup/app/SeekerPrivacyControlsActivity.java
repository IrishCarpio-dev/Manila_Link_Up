package com.manilalinkup.app;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SeekerPrivacyControlsActivity extends AppCompatActivity {

    private Switch switchPublicProfile;
    private TextView btnDownloadData, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_privacy_controls);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        switchPublicProfile = findViewById(R.id.switch_public_profile);
        btnDownloadData = findViewById(R.id.btn_download_data);
        btnDeleteAccount = findViewById(R.id.btn_delete_account);
    }

    private void setupListeners() {

        switchPublicProfile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Visible" : "Hidden";
            showToast("Profile is now " + status);
        });

        btnDownloadData.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Data Export")
                    .setMessage("We will prepare a copy of your personal data and send it to your registered email. This may take up to 24 hours.")
                    .setPositiveButton("Request", (dialog, which) -> showToast("Request Sent"))
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account?")
                .setMessage("This action is permanent. All your profile data, gig history, and verifications will be wiped from Manila LinkUp. Are you absolutely sure?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("DELETE PERMANENTLY", (dialog, which) -> {
                    // TODO: Firebase user.delete() logic
                    showToast("Account deletion request initiated.");
                })
                .setNegativeButton("Keep My Account", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}