package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.LogoutHelper;

public class SeekerSettingsActivity extends BaseActivity {

    private static final String TAG = "SeekerSettingsActivity";

    private TextView btnEditProfile, btnVerification, btnPrivacy;
    private TextView btnChangePassword, tvUserEmail;
    private android.widget.ImageButton btnBack;
    private Switch switchNotifications;
    private TextView btnJobPreferences;
    private TextView btnHelpCenter, btnTerms, btnAbout;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_seeker);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initializeViews();

        if (checkViewsExist()) {
            displayCurrentUserEmail();
            setupClickListeners();
        } else {
            showToast("Error: Some UI elements were not found. App may crash.");
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back);

        // Account & Security
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnVerification = findViewById(R.id.btn_verification);
        btnPrivacy = findViewById(R.id.btn_privacy);

        // Preferences
        switchNotifications = findViewById(R.id.switch_notifications);
        btnJobPreferences = findViewById(R.id.btn_job_preferences);

        // Support & Legal
        btnHelpCenter = findViewById(R.id.btn_help_center);
        btnTerms = findViewById(R.id.btn_terms);
        btnAbout = findViewById(R.id.btn_about);

        logoutButton = findViewById(R.id.btn_logout);
    }

    private boolean checkViewsExist() {
        if (btnPrivacy == null) Log.e(TAG, "Missing View: btn_privacy");
        if (btnTerms == null) Log.e(TAG, "Missing View: btn_terms");
        if (btnAbout == null) Log.e(TAG, "Missing View: btn_about");

        return btnPrivacy != null && btnTerms != null && btnAbout != null;
    }

    private void displayCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            tvUserEmail.setText(user.getEmail());
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Edit Profile",
                    "Changing your profile details may require a new identity verification. Proceed?",
                    () -> {
                        startActivity(new Intent(this, EditSeekerProfileActivity.class));
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
                    "Uploading a new ID will put your account under review. Proceed?",
                    () -> {
                        startActivity(new Intent(this, SeekerVerifyIdentityActivity.class));
                    }
            );
        });

        btnPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeekerPrivacyControlsActivity.class);
            startActivity(intent);
        });
        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("Notifications " + (isChecked ? "Enabled" : "Disabled"));
        });

        btnJobPreferences.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeekerJobPreferences.class);
            intent.putExtra("FROM_SETTINGS", true);
            startActivity(intent);
        });

        btnHelpCenter.setOnClickListener(v -> {
            Intent intent = new Intent(this, HelpCenterActivity.class);
            startActivity(intent);
        });

        btnTerms.setOnClickListener(v -> {
            Intent intent = new Intent(this, TermsOfServiceActivity.class);
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // DELETE performLogout() and use this instead:
                    LogoutHelper.logout(SeekerSettingsActivity.this, mAuth);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performLogout() {
        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
            // After Google signs out, use your helper or manual intent to go back to Login
            Intent intent = new Intent(SeekerSettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

            showToast("Logged out successfully");
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
}