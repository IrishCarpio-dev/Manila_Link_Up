package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SeekerSettingsActivity extends AppCompatActivity {

    private static final String TAG = "SeekerSettingsActivity";

    private TextView btnEditProfile, btnVerification, btnPrivacy;
    private TextView btnChangePassword, tvUserEmail;
    private Switch switchNotifications;
    private TextView btnHelpCenter, btnTerms, btnAbout;
    private Button btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // CRITICAL: Ensure this layout file contains ALL the IDs used below
        setContentView(R.layout.activity_settings_seeker);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();

        // This check will tell you in Logcat if any view is missing
        if (checkViewsExist()) {
            displayCurrentUserEmail();
            setupClickListeners();
        } else {
            showToast("Error: Some UI elements were not found. App may crash.");
        }
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

    private boolean checkViewsExist() {
        if (btnPrivacy == null) Log.e(TAG, "Missing View: btn_privacy");
        if (btnTerms == null) Log.e(TAG, "Missing View: btn_terms");
        if (btnAbout == null) Log.e(TAG, "Missing View: btn_about");

        // Return true only if the most critical views are found
        return btnPrivacy != null && btnTerms != null && btnAbout != null;
    }

    private void displayCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            tvUserEmail.setText(user.getEmail());
        }
    }

    private void setupClickListeners() {

        // 1. Edit Profile
        btnEditProfile.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Edit Profile",
                    "Changing your profile details may require a new identity verification. Proceed?",
                    () -> {
                        startActivity(new Intent(this, EditSeekerProfileActivity.class));
                    }
            );
        });

        // 2. Change Password (Email Reset Logic)
        btnChangePassword.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Change Password",
                    "We will send a password reset link to your registered email address. Proceed?",
                    this::sendPasswordResetEmail
            );
        });

        // 3. Verification
        btnVerification.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Verify Identity",
                    "Uploading a new ID will put your account under review. Proceed?",
                    () -> {
                        startActivity(new Intent(this, SeekerVerifyIdentityActivity.class));
                    }
            );
        });

        // 4. Privacy Controls
        btnPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeekerPrivacyControlsActivity.class);
            startActivity(intent);
        });

        // 5. Notifications Switch
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("Notifications " + (isChecked ? "Enabled" : "Disabled"));
        });

        // 6. Help Center
        btnHelpCenter.setOnClickListener(v -> {
            startActivity(new Intent(this, HelpCenterActivity.class));
        });

        // 7. Terms of Service
        btnTerms.setOnClickListener(v -> {
            startActivity(new Intent(this, TermsOfServiceActivity.class));
        });

        // 8. About Manila LinkUp
        btnAbout.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutActivity.class));
        });

        // 9. Logout
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
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Manila LinkUp?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    mAuth.signOut();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}