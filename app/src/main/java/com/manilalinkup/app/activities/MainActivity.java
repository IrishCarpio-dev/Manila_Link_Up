package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.manilalinkup.app.R;

public class MainActivity extends AppCompatActivity {

    MaterialButton loginButton;
    MaterialButton signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        loginButton = findViewById(R.id.material_button_login);
        signupButton = findViewById(R.id.material_button_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetStartedActivity.class);
                startActivity(intent);
            }
        });


    }
}