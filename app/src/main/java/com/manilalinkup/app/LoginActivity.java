package com.manilalinkup.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    MaterialButton loginNowButton;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextView createPassword;


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
        createPassword = findViewById(R.id.text_view_forget_password);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                boolean isValid = true;

                emailLayout.setError(null);
                passwordLayout.setError(null);

                if(email.isEmpty()){
                    emailLayout.setError("Email is required.");
                    emailInput.requestFocus();
                    isValid = false;
                } else if(password.isEmpty()){
                    passwordLayout.setError("Password is required.");
                    passwordInput.requestFocus();
                    isValid = false;
                } else if(password.length() < 8){
                    passwordLayout.setError("Password must be at least 8 characters.");
                    passwordInput.requestFocus();
                    isValid = false;
                } else{
                    Intent loginNowActivityIntent = new Intent(LoginActivity.this, OTPVerificationActivity.class);
                    startActivity(loginNowActivityIntent);
                    Toast.makeText(LoginActivity.this,"Logging you in. One moment... in time ;)", Toast.LENGTH_LONG).show();
                }

            }
        });

        createPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createPasswordActivityIntent = new Intent(LoginActivity.this, OTPVerificationActivity.class);
                startActivity(createPasswordActivityIntent);
            }
        });

    }
}