package com.manilalinkup.app.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.manilalinkup.app.R;

public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_manila_link_up);

        ImageButton btnBack = findViewById(R.id.btn_back_about);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}