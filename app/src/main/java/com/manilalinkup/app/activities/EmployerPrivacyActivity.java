package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.manilalinkup.app.R;

public class EmployerPrivacyActivity extends AppCompatActivity {

    private ImageView btnBack;
    private Switch switchVisibility;
    private TextView tvStatus, btnViewDocs, btnDownloadData, btnClearHistory, btnDeleteAccount;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_privacy_and_data);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back_privacy);
        switchVisibility = findViewById(R.id.switch_company_visibility);
        tvStatus = findViewById(R.id.tv_employer_verify_status);
        btnViewDocs = findViewById(R.id.btn_view_business_docs);
        btnDownloadData = findViewById(R.id.btn_download_hiring_data);
        btnClearHistory = findViewById(R.id.btn_clear_job_history);
        btnDeleteAccount = findViewById(R.id.btn_close_business_account);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        switchVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String msg = isChecked ? "Company is now discoverable" : "Company is now hidden from search";
            showToast(msg);
        });

        btnViewDocs.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployerBusinessVerificationActivity.class);
            startActivity(intent);
        });

        // Download Data
        btnDownloadData.setOnClickListener(v ->
                showToast("Preparing recruitment data for export... Check your email."));

        // Clear History
        btnClearHistory.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear History")
                        .setMessage("Delete all search and applicant filtering logs?")
                        .setPositiveButton("Clear", (dialog, which) -> showToast("History cleared"))
                        .setNegativeButton("Cancel", null)
                        .show());

        btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Close Business Account")
                .setMessage("This action is permanent. All active job postings and applicant data will be deleted. Are you sure?")
                .setPositiveButton("Close Account", (dialog, which) -> {
                    // Handle deletion logic here
                    showToast("Account termination request sent.");
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}