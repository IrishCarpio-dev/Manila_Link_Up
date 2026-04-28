package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
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

public class EmployerSettingsActivity extends BaseActivity {

    private static final String TAG = "EmployerSettingsActivity";

    private TextView btnEditProfile, btnVerification, btnPrivacy;
    private TextView btnChangePassword, tvUserEmail;
    private Switch switchNotifications;
    private TextView btnHelpCenter, btnTerms, btnAbout;
    private Button logoutButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_settings);

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
            showToast("Error: Some UI elements were not found.");
        }
    }

    private void initializeViews() {
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnChangePassword = findViewById(R.id.btn_change_password);
        btnVerification = findViewById(R.id.btn_verification);
        btnPrivacy = findViewById(R.id.btn_privacy);

        switchNotifications = findViewById(R.id.switch_notifications);

        btnHelpCenter = findViewById(R.id.btn_help_center);
        btnTerms = findViewById(R.id.btn_terms);
        btnAbout = findViewById(R.id.btn_about);

        logoutButton = findViewById(R.id.btn_logout);

        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private boolean checkViewsExist() {
        return btnPrivacy != null && btnTerms != null && btnAbout != null;
    }

    private void displayCurrentUserEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            tvUserEmail.setText(user.getEmail());
        }
    }

    private void setupClickListeners() {
        btnEditProfile.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Edit Profile",
                    "Update your business details and contact information. Proceed?",
                    () -> startActivity(new Intent(this, EditEmployerProfileActivity.class))
            );
        });

        btnChangePassword.setOnClickListener(v -> {
            showSensitiveActionWarning(
                    "Change Password",
                    "We will send a password reset link to your business email. Proceed?",
                    this::sendPasswordResetEmail
            );
        });

        btnVerification.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployerBusinessVerificationActivity.class));
        });

        btnPrivacy.setOnClickListener(v -> {
            startActivity(new Intent(this, EmployerPrivacyActivity.class));
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showToast("Notifications " + (isChecked ? "Enabled" : "Disabled"));
        });

        btnHelpCenter.setOnClickListener(v -> startActivity(new Intent(this, HelpCenterActivity.class)));
        btnTerms.setOnClickListener(v -> startActivity(new Intent(this, TermsOfServiceActivity.class)));
        btnAbout.setOnClickListener(v -> startActivity(new Intent(this, AboutActivity.class)));

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out from Manila LinkUp?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Using your improved LogoutHelper for the sequential flow
                    LogoutHelper.logout(EmployerSettingsActivity.this, mAuth);
                })
                .setNegativeButton("Cancel", null)
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

    private void showSensitiveActionWarning(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Proceed", (dialog, which) -> onConfirm.run())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}