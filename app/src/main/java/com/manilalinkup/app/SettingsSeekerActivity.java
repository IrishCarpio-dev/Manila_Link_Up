package com.manilalinkup.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsSeekerActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnVerification, btnPrivacy;
    private Switch switchNotifications, switchDarkMode;
    private TextView btnHelpCenter, btnTerms, btnAbout;
    private Button btnLogout;

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_IS_DARK_MODE = "isDarkMode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_seeker);

        initializeViews();

        // Load the saved theme state to set the Switch position correctly
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkModeActive = prefs.getBoolean(KEY_IS_DARK_MODE, false);
        switchDarkMode.setChecked(isDarkModeActive);

        setupClickListeners();
    }

    private void initializeViews() {
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnVerification = findViewById(R.id.btn_verification);
        btnPrivacy = findViewById(R.id.btn_privacy);
        switchNotifications = findViewById(R.id.switch_notifications);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        btnHelpCenter = findViewById(R.id.btn_help_center);
        btnTerms = findViewById(R.id.btn_terms);
        btnAbout = findViewById(R.id.btn_about);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void setupClickListeners() {
        // Dark Mode Logic
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 1. Save the choice to SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean(KEY_IS_DARK_MODE, isChecked);
            editor.apply();

            // 2. Apply the theme globally
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            showToast("Dark Mode " + (isChecked ? "Enabled" : "Disabled"));
        });

        // Other Navigation (Placeholders)
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
                    showToast("Logged out successfully");
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}