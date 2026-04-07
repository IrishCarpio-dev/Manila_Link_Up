package com.manilalinkup.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.manilalinkup.app.R;

public class HelpCenterActivity extends AppCompatActivity {

    private Button btnContactSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        btnContactSupport = findViewById(R.id.btn_contact_support);

        btnContactSupport.setOnClickListener(v -> {
            composeEmail();
        });
    }

    private void composeEmail() {
        // Pre-fills an email to your support address
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@manilalinkup.ph"));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Manila LinkUp Seeker");

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }
}