package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.manilalinkup.app.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SeekerSettingsActivity extends AppCompatActivity {

    private TextView btnEditProfile, btnVerification, btnPrivacy;
    private TextView btnChangePassword, tvUserEmail;
    private Switch switchNotifications;
    private TextView btnHelpCenter, btnTerms, btnAbout;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_seeker);

        try {
            mAuth = FirebaseAuth.getInstance();
        } catch (Exception e) {
            // In case Firebase is not initialized yet in this local workspace
            mAuth = null;
        }

        initializeViews();
        displayCurrentUserEmail();
        setupClickListeners();
    }

    private void initializeViews() {
        // Account & Security
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnVerification = findViewById(R.id.btn_verification);
        btnPrivacy = findViewById(R.id.btn_privacy);

        // Preferences
        switchNotifications = findViewById(R.id.switch_notifications);

        // Support & Legal
        btnHelpCenter = findViewById(R.id.btn_help_center);
        btnTerms = findViewById(R.id.btn_terms);
        btnAbout = findViewById(R.id.btn_about);

        btnLogout = findViewById(R.id.btn_logout);
    }

    private void displayCurrentUserEmail() {
        if (mAuth != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                tvUserEmail.setText(user.getEmail());
            }
        }
    }

    private void setupClickListeners() {

        // 1. Edit Profile with Warning -> jumping to the newly created EditInformationActivity
        btnEditProfile.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Edit Profile",
                    "Changing your profile details may require a new identity verification. Do you want to proceed?",
                    () -> {
                        Intent intent = new Intent(this, EditInformationActivity.class);
                        startActivity(intent);
                    }
            );
        });

        btnChangePassword.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Change Password",
                    "We will send a password reset link to your registered email address. Proceed?",
                    this::sendPasswordResetEmail
            );
        });

        btnVerification.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Verify Identity",
                    "Uploading a new ID will put your account under review. You may be temporarily unable to apply for gigs. Proceed?",
                    () -> {
                        try {
                            Intent intent = new Intent(this, Class.forName("com.manilalinkup.app.SeekerVerifyIdentityActivity"));
                            startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            showToast("Activity not yet available.");
                        }
                    }
            );
        });

        btnPrivacy.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, Class.forName("com.manilalinkup.app.SeekerPrivacyControlsActivity"));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                showToast("Opening Privacy Controls...");
            }
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("Notifications " + (isChecked ? "Enabled" : "Disabled"));
        });

        btnHelpCenter.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, Class.forName("com.manilalinkup.app.HelpCenterActivity"));
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                showToast("Loading Help Center...");
            }
        });
        
        btnTerms.setOnClickListener(v -> showToast("Displaying Terms of Service..."));
        btnAbout.setOnClickListener(v -> showAboutDialog());

        btnLogout.setOnClickListener(v -> showLogoutConfirmation());

    }

    private void showSensitiveActionWarning(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Proceed", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void sendPasswordResetEmail() {
        if (mAuth != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null && user.getEmail() != null) {
                String email = user.getEmail();
                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                new AlertDialog.Builder(this)
                                        .setTitle("Link Sent")
                                        .setMessage("A reset link has been sent to: " + email)
                                        .setPositiveButton("OK", null)
                                        .show();
                            } else {
                                showToast("Error: " + task.getException().getMessage());
                            }
                        });
            }
        } else {
            showToast("Feature not initialized locally.");
        }
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About Manila LinkUp")
                .setMessage("Manila LinkUp v1.0.2-beta\nConnecting employers and gig workers in Metro Manila.")
                .setPositiveButton("Close", null)
                .show();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Manila LinkUp?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    if (mAuth != null) mAuth.signOut();
                    Intent intent = new Intent(SeekerSettingsActivity.this, MainActivity.class);
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
