package com.manilalinkup.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.ErrorUtils;

public class ResetPasswordActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout newPasswordLayout;
    private TextInputLayout confirmPasswordLayout;
    private ProgressBar progressBar;
    private LinearLayout layoutForm;
    private LinearLayout layoutError;
    private String oobCode;

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);

        mAuth = FirebaseAuth.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar);
        layoutForm = findViewById(R.id.layout_form);
        layoutError = findViewById(R.id.layout_error);
        newPasswordLayout = findViewById(R.id.text_input_layout_new_password);
        confirmPasswordLayout = findViewById(R.id.text_input_layout_confirm_password);
        MaterialButton btnReset = findViewById(R.id.btn_reset_password);
        MaterialButton btnRequestNew = findViewById(R.id.btn_request_new_link);


        btnReset.setOnClickListener(v -> attemptPasswordReset());
        btnRequestNew.setOnClickListener(v -> {
            Intent intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        });

        handleResetLink(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleResetLink(intent);
    }

    private void handleResetLink(Intent intent) {
        Uri data = intent.getData();
        if (data == null) {
            showError();
            return;
        }

        String mode = data.getQueryParameter("mode");
        oobCode = data.getQueryParameter("oobCode");

        if (!"resetPassword".equals(mode) || oobCode == null || oobCode.isEmpty()) {
            showError();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        layoutForm.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);

        mAuth.verifyPasswordResetCode(oobCode)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        layoutForm.setVisibility(View.VISIBLE);
                    } else {
                        showError();
                    }
                });
    }

    private void attemptPasswordReset() {
        newPasswordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        String newPassword = (newPasswordLayout.getEditText() != null)
                ? newPasswordLayout.getEditText().getText().toString().trim()
                : "";
        String confirmPassword = (confirmPasswordLayout.getEditText() != null)
                ? confirmPasswordLayout.getEditText().getText().toString().trim()
                : "";

        if (newPassword.isEmpty()) {
            newPasswordLayout.setError("Password is required");
            newPasswordLayout.requestFocus();
            return;
        }
        if (!newPassword.matches(PASSWORD_PATTERN)) {
            newPasswordLayout.setError("Must be 8+ characters with 1 uppercase letter and 1 special character");
            newPasswordLayout.requestFocus();
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            confirmPasswordLayout.requestFocus();
            return;
        }

        showProgress("Updating password...");

        mAuth.confirmPasswordReset(oobCode, newPassword)
                .addOnCompleteListener(task -> {
                    hideProgress();
                    if (task.isSuccessful()) {
                        navigateToLogin();
                    } else {
                        ErrorUtils.showThrowableError(this, task.getException());
                    }
                });
    }

    private void showError() {
        progressBar.setVisibility(View.GONE);
        layoutForm.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
    }

    private void navigateToLogin() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Password Updated");
        builder.setMessage("Your password has been reset successfully. Please log in with your new password.");
        builder.setPositiveButton("Log In", (dialog, which) -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);
        builder.show();
    }
}
