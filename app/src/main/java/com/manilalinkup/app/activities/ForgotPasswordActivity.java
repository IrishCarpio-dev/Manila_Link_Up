package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.ErrorUtils;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private TextInputLayout emailLayout;
    private LinearLayout layoutForm;
    private LinearLayout layoutSuccess;
    private TextView textViewSuccessMessage;
    private android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        emailLayout = findViewById(R.id.text_input_layout_email);
        layoutForm = findViewById(R.id.layout_form);
        layoutSuccess = findViewById(R.id.layout_success);
        textViewSuccessMessage = findViewById(R.id.text_view_success_message);
        MaterialButton btnSend = findViewById(R.id.btn_send_reset_link);
        MaterialButton btnBackToLogin = findViewById(R.id.btn_back_to_login);
        TextView textViewBackToLogin = findViewById(R.id.text_view_back_to_login);
        TextView textViewResend = findViewById(R.id.text_view_resend);

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Sending reset link...");
        progressDialog.setCancelable(false);

        btnSend.setOnClickListener(v -> sendResetLink());

        textViewBackToLogin.setOnClickListener(v -> finish());

        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        textViewResend.setOnClickListener(v -> {
            layoutSuccess.setVisibility(View.GONE);
            layoutForm.setVisibility(View.VISIBLE);
        });
    }

    private void sendResetLink() {
        emailLayout.setError(null);

        String email = (emailLayout.getEditText() != null)
                ? emailLayout.getEditText().getText().toString().trim()
                : "";

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            emailLayout.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email address");
            emailLayout.requestFocus();
            return;
        }

        progressDialog.show();

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        showSuccessState(email);
                    } else {
                        ErrorUtils.showThrowableError(this, task.getException());
                    }
                });
    }

    private void showSuccessState(String email) {
        textViewSuccessMessage.setText("We've sent a password reset link to\n" + email);
        layoutForm.setVisibility(View.GONE);
        layoutSuccess.setVisibility(View.VISIBLE);
    }
}
