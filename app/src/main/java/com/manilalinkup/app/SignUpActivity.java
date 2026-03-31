package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity {
    private com.google.firebase.auth.FirebaseAuth mAuth;
    MaterialToolbar toolbar;
    MaterialButton sendOTP;
    TextInputLayout firstName;
    TextInputLayout lastname;
    TextInputLayout emailAddress;
    TextInputLayout mobileNumber;
    TextInputLayout createPassword;
    TextInputLayout confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        sendOTP = findViewById(R.id.material_button_send_otp);
        firstName = findViewById(R.id.text_input_layout_first_name);
        lastname = findViewById(R.id.text_input_layout_last_name);
        emailAddress = findViewById(R.id.text_input_layout_email_address);
        mobileNumber = findViewById(R.id.text_input_layout_phone_number);
        createPassword = findViewById(R.id.text_input_create_password);
        confirmPassword = findViewById(R.id.text_input_confirm_password);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstnameInput = firstName.getEditText().getText().toString().trim();
                String lastnameInput = lastname.getEditText().getText().toString().trim();
                String mobileNumberInput = mobileNumber.getEditText().getText().toString().trim();
                String emailAddressInput = emailAddress.getEditText().getText().toString().trim();
                String createPasswordInput = createPassword.getEditText().getText().toString().trim();
                String confirmPasswordInput = confirmPassword.getEditText().getText().toString().trim();

                if (emailAddressInput.isEmpty() || createPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!createPasswordInput.equals(confirmPasswordInput)) {
                    // Show error on the layout so the user sees it clearly
                    confirmPassword.setError("Passwords do not match");
                    return;
                } else {
                    confirmPassword.setError(null); // Clear error if they match
                }

                if (createPasswordInput.length() < 8) {
                    createPassword.setError("Password must be at least 8 characters");
                    return;
                }
                // If validation passes, start Firebase
                mAuth.createUserWithEmailAndPassword(emailAddressInput, createPasswordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 1. Send verification link
                                mAuth.getCurrentUser().sendEmailVerification();

                                // 2. Send profile to Laravel immediately so the DB record exists
                                sendProfileToLaravel(
                                        mAuth.getCurrentUser().getUid(),
                                        firstnameInput,
                                        lastnameInput,
                                        emailAddressInput,
                                        mobileNumberInput
                                );

                                // 3. Inform user and go to Login
                                Toast.makeText(SignUpActivity.this, "Registration successful! Please verify your email before logging in.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Close SignUpActivity so they can't go back
                            } else {
                                Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }

        });
    }

    private void sendProfileToLaravel(String uid, String firstnameInput, String lastnameInput, String emailAddressInput, String mobileNumberInput) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://172.31.243.113/") // Replaced with actual IPv4
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        SeekerRequest request = new SeekerRequest(
                uid,
                firstnameInput,
                lastnameInput,
                emailAddressInput,
                "Not set",        // Placeholder for Address
                "Not set",        // Placeholder for Birthdate
                "Manila",         // Placeholder for Location
                mobileNumberInput,
                0,                // Placeholder for Salary
                "default_url",    // Placeholder for Profile Picture
                "pending",        // Placeholder for Clearance
                1,                // Status: 1 (Active)
                0                 // Verified: 0 (No)
        );

        apiService.registerSeeker(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("API_SUCCESS", "Data sent to Laravel successfully");
                } else {
                    android.util.Log.e("API_ERROR", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                android.util.Log.e("API_FAILURE", "Check Connection: " + t.getMessage());
            }
        });
    }
}