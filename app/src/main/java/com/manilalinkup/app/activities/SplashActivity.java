package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;
import com.manilalinkup.app.models.UserProfileModel;

import java.util.List;
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

        apiService.getUserProfile().enqueue(new Callback<ApiResponse<UserProfileModel>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserProfileModel>> call, Response<ApiResponse<UserProfileModel>> response) {
                if (isDestroyed()) return;
                if (response.isSuccessful()) {
                    SessionCache.getInstance().setUserProfile(response.body().getData());
                    SessionCache.getInstance().refreshServiceTags(token, new SessionCache.ServiceTagsCallback() {
                        @Override public void onAvailable(List<ServiceTagModel> tags) {}
                        @Override public void onError() {}
                    });
                    if (response.body().getData().getSeekers() != null) {
                        // User is a seeker
                        Boolean isProfileSet = Optional.ofNullable(response.body().getData().getSeekers().getProfileSet()).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(SplashActivity.this, SeekerDashboardActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, EditSeekerProfileActivity.class));
                            finish();
                        }
                    } else if (response.body().getData().getEmployers() != null) {
                        // User is an employer
                        Boolean isProfileSet = Optional.ofNullable(response.body().getData().getEmployers().getProfileSet()).orElse(false);

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
            public void onFailure(Call<ApiResponse<UserProfileModel>> call, Throwable t) {
                if (isDestroyed()) return;
                mAuth.signOut();
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
                ErrorUtils.showThrowableError(SplashActivity.this, t);
            }
        });
    }
}