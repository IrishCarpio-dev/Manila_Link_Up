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
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EmployerSignUp extends AppCompatActivity {
    private android.app.ProgressDialog progressDialog;
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

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false); // Prevents user from dismissing it by clicking outside

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

                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(emailAddressInput, createPasswordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // 1. Send verification link
                                mAuth.getCurrentUser().sendEmailVerification();

                                FirebaseUser user = mAuth.getCurrentUser();
                                user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        String idToken = tokenTask.getResult().getToken();
                                        // 2. Send profile to Laravel immediately so the DB record exists
                                        sendProfileToLaravel(
                                                idToken,
                                                employerNameInput,
                                                emailAddressInput,
                                                mobileNumberInput
                                        );
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(EmployerSignUp.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });


            }
        });
    }

    private void sendProfileToLaravel(String token, String employerName, String email, String phoneNumber) {
        ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

        EmployerRequest request = new EmployerRequest(
                employerName,
                email,
                phoneNumber
        );

        apiService.registerEmployer(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();

                if (response.isSuccessful()) {
                    Toast.makeText(EmployerSignUp.this, "Registration successful! Please verify your email before logging in.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(EmployerSignUp.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // Close EmployerSignUp so they can't go back
                } else {
                    ErrorUtils.showErrorMessage(EmployerSignUp.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showThrowableError(EmployerSignUp.this, t);
            }
        });
    }

}