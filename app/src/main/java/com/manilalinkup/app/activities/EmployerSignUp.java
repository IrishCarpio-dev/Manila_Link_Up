package com.manilalinkup.app.activities;

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
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.models.EmployerRequest;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerSignUp extends AppCompatActivity {
    private android.app.ProgressDialog progressDialog;
    private com.google.firebase.auth.FirebaseAuth mAuth;
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

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Signing up...");
        progressDialog.setCancelable(false);

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String employerNameInput = employerName.getEditText().getText().toString().trim();
                String mobileNumberInput = mobileNumber.getEditText().getText().toString().trim();
                String emailAddressInput = emailAddress.getEditText().getText().toString().trim();
                String createPasswordInput = createPassword.getEditText().getText().toString().trim();
                String confirmPasswordInput = confirmPassword.getEditText().getText().toString().trim();

                employerName.setError(null);
                mobileNumber.setError(null);
                emailAddress.setError(null);
                createPassword.setError(null);
                confirmPassword.setError(null);

                // 3. Name Validation
                if (employerNameInput.isEmpty()) {
                    employerName.setError("Full Name or Business Name is required");
                    return;
                }

                // 4. Email Validation (Regex)
                if (emailAddressInput.isEmpty()) {
                    emailAddress.setError("Email address is required");
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressInput).matches()) {
                    emailAddress.setError("Please enter a valid email address");
                    return;
                }

                // 5. Mobile Number Validation (Checks for exactly 10 digits since you have prefix +63)
                if (mobileNumberInput.isEmpty()) {
                    mobileNumber.setError("Mobile number is required");
                    return;
                } else if (mobileNumberInput.length() != 10 || !mobileNumberInput.startsWith("9")) {
                    mobileNumber.setError("Must be 10 digits starting with 9 (e.g., 9123456789)");
                    return;
                }

                // 6. Strong Password Validation
                // Regex: 8+ chars, 1 Upper, 1 Special
                String passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

                if (createPasswordInput.isEmpty()) {
                    createPassword.setError("Password is required");
                    return;
                } else if (!createPasswordInput.matches(passwordPattern)) {
                    createPassword.setError("Must be 8 more characters with 1 capital letter and 1 special character!");
                    return;
                }
                if (!createPasswordInput.equals(confirmPasswordInput)) {
                    confirmPassword.setError("Passwords do not match");
                    return;
                }

                progressDialog.show();
                mAuth.createUserWithEmailAndPassword(emailAddressInput, createPasswordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification();

                                FirebaseUser user = mAuth.getCurrentUser();
                                user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                    if (tokenTask.isSuccessful()) {
                                        String idToken = tokenTask.getResult().getToken();

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
                    finish();
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