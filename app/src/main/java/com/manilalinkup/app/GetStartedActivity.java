package com.manilalinkup.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class GetStartedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        MaterialToolbar toolbar = findViewById(R.id.material_toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }
}