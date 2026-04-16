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

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Creating account...");
        progressDialog.setCancelable(false);


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
                    confirmPassword.setError("Passwords do not match");
                    return;
                } else {
                    confirmPassword.setError(null);
                }

                if (createPasswordInput.length() < 8) {
                    createPassword.setError("Password must be at least 8 characters");
                    return;
                }

                progressDialog.show();

                mAuth.createUserWithEmailAndPassword(emailAddressInput, createPasswordInput)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                mAuth.getCurrentUser().sendEmailVerification();

                                FirebaseUser user = mAuth.getCurrentUser();
                                String userUid = user.getUid();

                                user.getIdToken(true).addOnCompleteListener(tokenTask -> {
                                    if(tokenTask.isSuccessful()){
                                        String idToken = tokenTask.getResult().getToken();

                                        sendProfileToLaravel(
                                                idToken,
                                                userUid,
                                                firstnameInput,
                                                lastnameInput,
                                                emailAddressInput,
                                                mobileNumberInput
                                        );
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(SignUpActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }

        });
    }

    private void sendProfileToLaravel(String idToken, String actualUid, String firstnameInput, String lastnameInput, String emailAddressInput, String mobileNumberInput) {
        // RetrofitClient uses the Token for the "Bearer" header
        ApiService apiService = RetrofitClient.getClient(idToken).create(ApiService.class);

        SeekerRequest request = new SeekerRequest(
                firstnameInput,
                lastnameInput,
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