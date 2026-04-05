package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.models.UserProfileModel;

import java.util.Optional;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        handleRouting();
    }

    private void handleRouting() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                if (tokenTask.isSuccessful()) {
                    String idToken = tokenTask.getResult().getToken();
                    checkUserRole(idToken);
                }
            });
        }
    }

    private void checkUserRole(String token) {
        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        apiService.getUserProfile().enqueue(new Callback<UserProfileModel>() {
            @Override
            public void onResponse(Call<UserProfileModel> call, Response<UserProfileModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSeekers() != null) {
                        // User is a seeker
                        Boolean isProfileSet = Optional.ofNullable(response.body().getSeekers().getProfileSet()).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(SplashActivity.this, SeekerDashboardActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, EditSeekerProfileActivity.class));
                            finish();
                        }
                    } else if (response.body().getEmployers() != null) {
                        // User is an employer
                        Boolean isProfileSet = Optional.ofNullable(response.body().getEmployers().getProfileSet()).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(SplashActivity.this, EmployerDashboard.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, EditEmployerProfileActivity.class));
                            finish();
                        }
                    } else {
                        mAuth.signOut();
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                        Toast.makeText(SplashActivity.this, "User profile not found in our system.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mAuth.signOut();
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                    ErrorUtils.showErrorMessage(SplashActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UserProfileModel> call, Throwable t) {
                mAuth.signOut();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                ErrorUtils.showThrowableError(SplashActivity.this, t);
            }
        });
    }
}