package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class EmployerSettingsActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView tvEmail;
    private TextView btnBusinessVerify, btnPassword, btnPrivacy;
    private Switch switchApplicantNotif;
    private TextView btnHelp, btnTerms, btnAbout;
    private Button btnLogout;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_settings);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupClickListeners();
        displayCurrentEmail();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back_settings);
        tvEmail = findViewById(R.id.tv_employer_email);

        btnBusinessVerify = findViewById(R.id.btn_business_verification);
        btnPassword = findViewById(R.id.btn_change_password_employer);
        btnPrivacy = findViewById(R.id.btn_privacy_employer);

        switchApplicantNotif = findViewById(R.id.switch_applicant_notif);

        btnHelp = findViewById(R.id.btn_employer_help);
        btnTerms = findViewById(R.id.btn_terms_employer);
        btnAbout = findViewById(R.id.btn_about);

        btnLogout = findViewById(R.id.btn_logout_employer);
    }

    private void setupClickListeners() {
        // Navigation - Back
        btnBack.setOnClickListener(v -> finish());

        // Business Verification
        btnBusinessVerify.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployerBusinessVerificationActivity.class));
            showToast("Opening BIR/SEC Verification...");
        });

        // Password Change
        btnPassword.setOnClickListener(v -> {
            showToast("Opening Password Settings...");
        });

        // Privacy Controls
        btnPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployerPrivacyActivity.class));
            showToast("Opening Privacy Controls...");
        });

        // Notification Toggle
        switchApplicantNotif.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Enabled" : "Disabled";
            showToast("Applicant Alerts " + status);
        });

        // Support & Legal
        btnHelp.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpCenterActivity.class));
            showToast("Loading Employer Help Center...");
        });

        btnTerms.setOnClickListener(v -> {
            startActivity(new Intent(this, TermsOfServiceActivity.class));
            showToast("Loading Hiring Terms...");
        });

        btnAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
            showToast("Manila LinkUp for Employers v1.0.2");
        });

        // Logout
        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void displayCurrentEmail() {
        if (mAuth.getCurrentUser() != null) {
            tvEmail.setText(mAuth.getCurrentUser().getEmail());
        } else {
            tvEmail.setText("No user logged in");
        }
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Manila LinkUp?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(EmployerSettingsActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    showToast("Logged out successfully");
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}