package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmployerSignUp extends AppCompatActivity {
    private com.google.firebase.auth.FirebaseAuth mAuth;
    MaterialToolbar toolbar;
    MaterialButton sendOTP;
    TextInputLayout employerName;
    TextInputLayout emailAddress;
    TextInputLayout mobileNumber;
    TextInputLayout createPassword;
    TextInputLayout confirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_sign_up);

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        sendOTP = findViewById(R.id.material_button_send_email_link_employer);
        employerName = findViewById(R.id.text_input_layout_employer_name);
        emailAddress = findViewById(R.id.text_input_layout_email_address_employer);
        mobileNumber = findViewById(R.id.text_input_layout_phone_number_employer);
        createPassword = findViewById(R.id.text_input_create_password_employer);
        confirmPassword = findViewById(R.id.text_input_confirm_password_employer);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employerNameInput = employerName.getEditText().getText().toString().trim();
                String mobileNumberInput = mobileNumber.getEditText().getText().toString().trim();
                String emailAddressInput = emailAddress.getEditText().getText().toString().trim();
                String createPasswordInput = createPassword.getEditText().getText().toString().trim();
                String confirmPasswordInput = confirmPassword.getEditText().getText().toString().trim();

                if (emailAddressInput.isEmpty() || createPasswordInput.isEmpty() || confirmPasswordInput.isEmpty()) {
                    Toast.makeText(EmployerSignUp.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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
                                        employerNameInput,
                                        emailAddressInput,
                                        mobileNumberInput
                                );

                                // 3. Inform user and go to Login
                                Toast.makeText(EmployerSignUp.this, "Registration successful! Please verify your email before logging in.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(EmployerSignUp.this, LoginActivity.class);
                                startActivity(intent);
                                finish(); // Close EmployerSignUp so they can't go back
                            } else {
                                Toast.makeText(EmployerSignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });
    }

    private void sendProfileToLaravel(String uid, String employerName,String email, String phoneNumber) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.8/") // Replaced with actual IPv4
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        EmployerRequest request = new EmployerRequest(
                uid,
                employerName,
                phoneNumber,
                email,

                "Manila",
                "May 3, 2004",
                "Paco, Manila,",
                "url profile picture",
                "url",
                1,
                0);

        apiService.registerEmployer(request).enqueue(new Callback<ResponseBody>() {
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