package com.manilalinkup.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class SeekerVerifyIdentityActivity extends AppCompatActivity {

    private Spinner spinnerIdType;
    private CardView btnUploadId;
    private ImageView imgIdPreview;
    private Button btnSubmit;
    private View uploadPlaceholderLayout;

    private Uri selectedImageUri;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imgIdPreview.setImageURI(uri);
                    imgIdPreview.setVisibility(View.VISIBLE);
                    uploadPlaceholderLayout.setVisibility(View.GONE);
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_verify_identity);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        spinnerIdType = findViewById(R.id.spinner_id_type);
        btnUploadId = findViewById(R.id.btn_upload_id);
        imgIdPreview = findViewById(R.id.img_id_preview);
        btnSubmit = findViewById(R.id.btn_submit_verification);

        // This is the LinearLayout inside the CardView containing the camera icon/text
        uploadPlaceholderLayout = btnUploadId.getChildAt(0);
    }

    private void setupListeners() {

        btnUploadId.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Submit Button
        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void validateAndSubmit() {
        String selectedId = spinnerIdType.getSelectedItem().toString();

        if (spinnerIdType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select an ID type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload a photo of your ID", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Submission")
                .setMessage("Are you sure all details on your " + selectedId + " are clear and readable? This process cannot be undone once submitted.")
                .setPositiveButton("Submit", (dialog, which) -> {
                    performUpload();
                })
                .setNegativeButton("Review Again", null)
                .show();
    }

    private void performUpload() {
        Toast.makeText(this, "Verification Submitted! Please wait 24-48 hours for review.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}