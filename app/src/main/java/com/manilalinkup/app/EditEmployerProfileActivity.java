package com.manilalinkup.app;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class EditEmployerProfileActivity extends AppCompatActivity {
    MaterialToolbar toolbar;
    private EditText locationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_employer_profile);
        locationInput = findViewById(R.id.etLocation);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Use .attachAddressAutocomplete() for complete address autocomplete
        AddressAutocompleteHelper.attachDistrictAutocomplete(locationInput);
    }
}