package com.manilalinkup.app.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import com.manilalinkup.app.R;

public class EditSeekerInformationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_seeker_information);

    }
}