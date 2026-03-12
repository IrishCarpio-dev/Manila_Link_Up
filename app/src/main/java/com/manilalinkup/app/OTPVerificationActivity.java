package com.manilalinkup.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class OTPVerificationActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    EditText b1, b2, b3, b4;
    MaterialButton verifyButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otpverification);

        b1 = findViewById(R.id.edit_text_otp_box1);
        b2 = findViewById(R.id.edit_text_otp_box2);
        b3 = findViewById(R.id.edit_text_otp_box3);
        b4 = findViewById(R.id.edit_text_otp_box4);

        verifyButton = findViewById(R.id.verifyButton);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setupOtpBox(b1, null, b2);
        setupOtpBox(b2, b1, b3);
        setupOtpBox(b3, b2, b4);
        setupOtpBox(b4, b3, null);

        /*
        VERIFY BUTTON
         */

        verifyButton.setOnClickListener(v -> {

            String otp =
                    b1.getText().toString() +
                            b2.getText().toString() +
                            b3.getText().toString() +
                            b4.getText().toString();

            if(otp.length() == 4){

                // OTP verified → go to Professional Identity page
                Intent intent = new Intent(OTPVerificationActivity.this, ProfessionalIdentity.class);
                startActivity(intent);

                finish(); // prevent going back to OTP screen

            }

        });
    }

    private void setupOtpBox(EditText current, EditText previous, EditText next) {

        current.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 1 && next != null) {
                    next.requestFocus();
                }

            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        current.setOnKeyListener((v, keyCode, event) -> {

            if (event.getAction() == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_DEL &&
                    current.getText().toString().isEmpty() &&
                    previous != null) {

                previous.requestFocus();
                previous.setSelection(previous.getText().length());
                return true;
            }

            return false;
        });
    }
}