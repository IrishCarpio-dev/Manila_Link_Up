package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class OTPVerificationActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    EditText b1;
    EditText b2;
    EditText b3;
    EditText b4;
    MaterialButton verify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);
        b1 = findViewById(R.id.edit_text_otp_box1);
        b2= findViewById(R.id.edit_text_otp_box2);
        b3 = findViewById(R.id.edit_text_otp_box3);
        b4 = findViewById(R.id.edit_text_otp_box4);
        verify = findViewById(R.id.material_button_verify);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupOtpFocus(b1, b2);
        setupOtpFocus(b2, b3);
        setupOtpFocus(b3, b4);

    }

    private void setupOtpFocus(final EditText current, final EditText next) {
        current.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    next.requestFocus(); // Move to next box when 1 number is typed
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
}