package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Optional;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                if (tokenTask.isSuccessful()) {
                    String idToken = tokenTask.getResult().getToken();
                    checkUserRole(idToken);
                }
            });
        }
        finish();
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
                if (response.isSuccessful()) {
                    if (response.body().seekers != null) {
                        // User is a seeker
                        Boolean isProfileSet = Optional.ofNullable(response.body().seekers.isProfileSet).orElse(false);

                        if (isProfileSet) {
                            startActivity(new Intent(SplashActivity.this, SeekerDashboardActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashActivity.this, EditSeekerProfileActivity.class));
                            finish();
                        }
                    } else if (response.body().employers != null) {
                        // User is an employer
                        Boolean isProfileSet = Optional.ofNullable(response.body().employers.isProfileSet).orElse(false);

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