package com.manilalinkup.app;

import android.content.Context;
import android.content.Intent; // Added
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth; // Added

public class EmployerSettingsActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnVerification, btnPrivacy;
    private Switch switchNotifications;
    private TextView btnHelpCenter, btnTerms, btnAbout;
    private Button btnLogout;
    private FirebaseAuth mAuth; // Added

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_seeker);

        mAuth = FirebaseAuth.getInstance(); // Initialize Firebase Auth

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnVerification = findViewById(R.id.btn_verification);
        btnPrivacy = findViewById(R.id.btn_privacy);
        switchNotifications = findViewById(R.id.switch_notifications);
        btnHelpCenter = findViewById(R.id.btn_help_center);
        btnTerms = findViewById(R.id.btn_terms);
        btnAbout = findViewById(R.id.btn_about);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> showToast("Opening Edit Profile..."));
        btnVerification.setOnClickListener(v -> showToast("Opening ID Verification..."));
        btnPrivacy.setOnClickListener(v -> showToast("Opening Privacy Controls..."));

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("Notifications " + (isChecked ? "Enabled" : "Disabled"));
        });

        btnHelpCenter.setOnClickListener(v -> showToast("Loading Help Center..."));
        btnTerms.setOnClickListener(v -> showToast("Displaying Terms of Service..."));
        btnAbout.setOnClickListener(v -> showToast("Manila LinkUp v1.0.2-beta"));

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Manila LinkUp?")
                .setPositiveButton("Logout", (dialog, which) -> {

                    // 1. Log out from Firebase
                    mAuth.signOut();

                    // 2. Redirect to MainActivity
                    Intent intent = new Intent(EmployerSettingsActivity.this, MainActivity.class);

                    // 3. Clear the Activity Stack
                    // This ensures the user can't press 'Back' to get back to the Profile
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