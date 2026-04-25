package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.SeekerRequest;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;

public class SignUpActivity extends AppCompatActivity {
    private android.app.ProgressDialog progressDialog;
    private com.google.firebase.auth.FirebaseAuth mAuth;
    MaterialButton sendOTP;
    TextInputLayout firstName;
    TextInputLayout middleName;
    TextInputLayout lastname;
    AutoCompleteTextView suffixDropdown;
    TextInputLayout emailAddress;
    TextInputLayout mobileNumber;
    TextInputLayout createPassword;
    TextInputLayout confirmPassword;
    TextView labelCreatePassword;
    TextView labelConfirmPassword;
    TextView otpMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        sendOTP = findViewById(R.id.material_button_send_otp);
        firstName = findViewById(R.id.text_input_layout_first_name);
        middleName = findViewById(R.id.text_input_layout_middle_name);
        lastname = findViewById(R.id.text_input_layout_last_name);
        suffixDropdown = findViewById(R.id.auto_complete_suffix);
        ArrayAdapter<String> suffixAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{"", "Sr.", "Jr.", "III", "IV"});
        suffixDropdown.setAdapter(suffixAdapter);
        emailAddress = findViewById(R.id.text_input_layout_email_address);
        mobileNumber = findViewById(R.id.text_input_layout_phone_number);
        createPassword = findViewById(R.id.text_input_create_password);
        confirmPassword = findViewById(R.id.text_input_confirm_password);
        labelCreatePassword = findViewById(R.id.text_create_password);
        labelConfirmPassword = findViewById(R.id.text_confirm_password);
        otpMessage = findViewById(R.id.text_view_otp_message);

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // 1. Hide the Input Layouts
            createPassword.setVisibility(View.GONE);
            confirmPassword.setVisibility(View.GONE);

            // 2. Hide the TextView Labels and OTP message
            labelCreatePassword.setVisibility(View.GONE);
            labelConfirmPassword.setVisibility(View.GONE);
            otpMessage.setVisibility(View.GONE);

            // 3. Change Button Text to "Continue"
            sendOTP.setText("Continue");

            // Pre-fill email
            if (currentUser.getEmail() != null) {
                emailAddress.getEditText().setText(currentUser.getEmail());
                emailAddress.setEnabled(false);
            }
        }

        sendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName.setError(null);
                lastname.setError(null);
                emailAddress.setError(null);
                mobileNumber.setError(null);
                createPassword.setError(null);
                confirmPassword.setError(null);

                String firstnameInput = firstName.getEditText().getText().toString().trim();
                String middleNameInput = middleName.getEditText().getText().toString().trim();
                String lastnameInput = lastname.getEditText().getText().toString().trim();
                String suffixInput = suffixDropdown.getText().toString().trim();
                String mobileNumberInput = mobileNumber.getEditText().getText().toString().trim();
                String emailAddressInput = emailAddress.getEditText().getText().toString().trim();
                String createPasswordInput = createPassword.getEditText().getText().toString().trim();
                String confirmPasswordInput = confirmPassword.getEditText().getText().toString().trim();


                if (firstnameInput.isEmpty()) {
                    firstName.setError("First name is required");
                    return;
                }
                if (lastnameInput.isEmpty()) {
                    lastname.setError("Last name is required");
                    return;
                }

                if (emailAddressInput.isEmpty()) {
                    emailAddress.setError("Email address is required");
                    return;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddressInput).matches()) {
                    emailAddress.setError("Please enter a valid email address");
                    return;
                }

                if (mobileNumberInput.isEmpty()) {
                    mobileNumber.setError("Mobile number is required");
                    return;
                } else if (mobileNumberInput.length() != 10 || !mobileNumberInput.startsWith("9")) {
                    mobileNumber.setError("Enter 10 digits starting with 9");
                    return;
                }

                String passwordPattern = "^(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

                if (createPasswordInput.isEmpty()) {
                    createPassword.setError("Password is required");
                    return;
                } else if (!createPasswordInput.matches(passwordPattern)) {
                    createPassword.setError("Please use 8+ character, 1 Capital, and 1 Special character");
                    return;
                }

                if (!createPasswordInput.equals(confirmPasswordInput)) {
                    confirmPassword.setError("Passwords do not match");
                    return;
                }

                FirebaseUser sessionUser = mAuth.getCurrentUser();

                if (sessionUser != null) {
                    // --- CASE A: SOCIAL USER (Facebook/Google) ---
                    progressDialog.show();
                    sessionUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                        if (tokenTask.isSuccessful()) {
                            sendProfileToLaravel(tokenTask.getResult().getToken(), sessionUser.getUid(),
                                    firstnameInput, middleNameInput, lastnameInput, suffixInput,
                                    emailAddressInput, mobileNumberInput);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // --- CASE B: NEW EMAIL/PASSWORD USER ---
                    createPasswordInput = createPassword.getEditText().getText().toString().trim();
                    confirmPasswordInput = confirmPassword.getEditText().getText().toString().trim();

                    if (createPasswordInput.isEmpty()) { createPassword.setError("Required"); return; }
                    if (!createPasswordInput.matches(passwordPattern)) { createPassword.setError("Weak password"); return; }
                    if (!createPasswordInput.equals(confirmPasswordInput)) { confirmPassword.setError("Mismatch"); return; }

                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(emailAddressInput, createPasswordInput)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser newUser = mAuth.getCurrentUser();
                                    newUser.sendEmailVerification();
                                    newUser.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                        if (tokenTask.isSuccessful()) {
                                            sendProfileToLaravel(tokenTask.getResult().getToken(), newUser.getUid(),
                                                    firstnameInput, middleNameInput, lastnameInput, suffixInput,
                                                    emailAddressInput, mobileNumberInput);
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }

        });

    }

    private void sendProfileToLaravel(String idToken, String actualUid, String firstnameInput, String middleNameInput, String lastnameInput, String suffixInput, String emailAddressInput, String mobileNumberInput) {
        ApiService apiService = RetrofitClient.getClient(idToken).create(ApiService.class);

        SeekerRequest request = new SeekerRequest(
                firstnameInput,
                middleNameInput,
                lastnameInput,
                suffixInput,
                emailAddressInput,
                mobileNumberInput,
                "2000-01-01"
        );

        apiService.registerSeeker(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "Registration successful! Please verify your email before logging in.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    ErrorUtils.showErrorMessage(SignUpActivity.this, response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressDialog.dismiss();
                ErrorUtils.showThrowableError(SignUpActivity.this, t);
            }
        });
    }
}