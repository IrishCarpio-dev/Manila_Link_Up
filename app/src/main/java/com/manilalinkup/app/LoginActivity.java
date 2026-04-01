package com.manilalinkup.app;

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
import com.manilalinkup.app.R;
import com.manilalinkup.app.UserProfileModel;
import com.manilalinkup.app.ApiService;
import com.manilalinkup.app.AuthInterceptor;

import org.json.JSONObject;

import java.util.Optional;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        progressDialog.setMessage("Verifying account...");
        progressDialog.setCancelable(false); // Prevents user from dismissing it by clicking outside

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Reset errors
                emailLayout.setError(null);
                passwordLayout.setError(null);

                // Validation Checks
                if(email.isEmpty()){
                    emailLayout.setError("Email is required.");
                    emailInput.requestFocus();
                    return; // Stop here
                }

                if(password.isEmpty()){
                    passwordLayout.setError("Password is required.");
                    passwordInput.requestFocus();
                    return; // Stop here
                }

                if(password.length() < 8){
                    passwordLayout.setError("Password must be at least 8 characters.");
                    passwordInput.requestFocus();
                    return; // Stop here
                }

                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.reload().addOnCompleteListener(reloadTask -> {
                                        if (user.isEmailVerified()) {
                                            // NEW: Don't jump to an activity yet. Check the role first!
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
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(token))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8000/") // Replaced with actual IPv4
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        apiService.getUserProfile().enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, Response<UserProfileModel> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().seekers != null) {
                        // User is a seeker
                        Boolean isProfileSet = Optional.ofNullable(response.body().seekers.isProfileSet).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(LoginActivity.this, SeekerDashboardActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(LoginActivity.this, EditSeekerProfileActivity.class));
                            finish();
                        }
                    } else if (response.body().employers != null) {
                        // User is an employer
                        Boolean isProfileSet = Optional.ofNullable(response.body().employers.isProfileSet).orElse(false);

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