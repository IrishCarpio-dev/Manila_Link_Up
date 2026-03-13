package com.manilalinkup.app;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

public class EditSeekerProfileActivity extends AppCompatActivity {

    MaterialToolbar toolbar;
    private RecyclerView rvSalaryRange;
    private SalaryRangeAdapter adapter;
    private RadioGroup rgSalaryType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_seeker_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        rvSalaryRange = findViewById(R.id.rvSalaryRange);
        rgSalaryType = findViewById(R.id.rgSalaryType);

    }
}