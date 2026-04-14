package com.manilalinkup.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.models.UserProfileModel;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private android.app.ProgressDialog progressDialog;
    private com.google.firebase.auth.FirebaseAuth mAuth;
    MaterialToolbar toolbar;
    MaterialButton loginNowButton;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextView forgetPassword;

    //for testing Dashboards - Irish
    ImageView googleLogin;
    ImageView facebookLogin; // Added for Facebook shortcut


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        loginNowButton = findViewById(R.id.material_button_login_now_2);
        emailInput = findViewById(R.id.text_input_email_input);
        passwordInput = findViewById(R.id.text_input_password_input);
        emailLayout = findViewById(R.id.text_input_layout_email_address);
        passwordLayout = findViewById(R.id.text_input_layout_password);
        forgetPassword = findViewById(R.id.text_view_forget_password);

        // For testing Dashboards - Irish
        googleLogin = findViewById(R.id.image_view_login_google);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testIntents = new Intent(LoginActivity.this, EmployerDashboard.class);
                startActivity(testIntents);
            }
        });

        // For testing Seeker Dashboard via Facebook shortcut
        facebookLogin = findViewById(R.id.image_view_login_facebook);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct jump to Seeker Dashboard
                Intent intent = new Intent(LoginActivity.this, SeekerDashboardActivity.class);
                startActivity(intent);
                finish(); // Optional: closes login screen so back button doesn't return here
            }
        });



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false); // Prevents user from dismissing it by clicking outside

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                emailLayout.setError(null);
                passwordLayout.setError(null);

                if(email.isEmpty()){
                    emailLayout.setError("Email is required.");
                    emailInput.requestFocus();
                    return;
                }

                if(password.isEmpty()){
                    passwordLayout.setError("Password is required.");
                    passwordInput.requestFocus();
                    return;
                }

                if(password.length() < 8){
                    passwordLayout.setError("Password must be at least 8 characters.");
                    passwordInput.requestFocus();
                    return;
                }

                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.reload().addOnCompleteListener(reloadTask -> {
                                        if (user.isEmailVerified()) {
                                            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                                if (tokenTask.isSuccessful()) {
                                                    String idToken = tokenTask.getResult().getToken();
                                                    checkUserRole(idToken);
                                                }
                                            });
                                        } else {
                                            progressDialog.dismiss();
                                            mAuth.signOut();
                                            Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
    }

    private void checkUserRole(String token) {
        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.getUserProfile().enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, Response<UserProfileModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getSeekers() != null) {
                        // User is a seeker
                        Boolean isProfileSet = Optional.ofNullable(response.body().getSeekers().getProfileSet()).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(LoginActivity.this, SeekerDashboardActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, EditSeekerProfileActivity.class));
                            finish();
                        }
                    } else if (response.body().getEmployers() != null) {
                        // User is an employer
                        Boolean isProfileSet = Optional.ofNullable(response.body().getEmployers().getProfileSet()).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(LoginActivity.this, EmployerDashboard.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, EditEmployerProfileActivity.class));
                            finish();
                        }
                    } else {
                        mAuth.signOut();
                        Toast.makeText(LoginActivity.this, "User profile not found in our system.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mAuth.signOut();
                    ErrorUtils.showErrorMessage(LoginActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UserProfileModel> call, Throwable t) {
                progressDialog.dismiss();
                mAuth.signOut();

                ErrorUtils.showThrowableError(LoginActivity.this, t);
            }
        });
    }
}