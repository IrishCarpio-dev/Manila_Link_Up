package com.manilalinkup.app.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.CredentialAdapter;
import com.manilalinkup.app.models.CredentialModel;

public class SeekerVerifyIdentityActivity extends BaseActivity {

    // General UI
    private ImageView btnBack;
    private Spinner spinnerIdType;
    private CardView btnUploadId;
    private ImageView imgIdPreview;
    private Button btnSubmit;
    private View uploadPlaceholderLayout;
    private TextView btnViewDocs;
    private TextView btnAddNewCredential;
    private RecyclerView recyclerCredentials;
    private CredentialAdapter credentialAdapter;
    private List<CredentialModel> credentialList;

    private Uri selectedIdUri;
    private boolean pickingMainId = true;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    if (pickingMainId) {
                        selectedIdUri = uri;
                        imgIdPreview.setImageURI(uri);
                        imgIdPreview.setVisibility(View.VISIBLE);
                        uploadPlaceholderLayout.setVisibility(View.GONE);
                    } else {
                        String fileName = "Credential_" + (credentialList.size() + 1);
                        credentialList.add(new CredentialModel(fileName, uri));

                        // Notify adapter and scroll to bottom
                        credentialAdapter.notifyItemInserted(credentialList.size() - 1);
                        recyclerCredentials.scrollToPosition(credentialList.size() - 1);

                        Toast.makeText(this, "Document attached", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_verify_identity);

        initializeViews();
        setupIdSpinner();
        setupRecyclerView();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back_verify);
        spinnerIdType = findViewById(R.id.spinner_id_type);
        btnUploadId = findViewById(R.id.btn_upload_id);
        imgIdPreview = findViewById(R.id.img_id_preview);
        btnSubmit = findViewById(R.id.btn_submit_verification);
        btnAddNewCredential = findViewById(R.id.btn_add_credential);
        recyclerCredentials = findViewById(R.id.recycler_credentials);
        uploadPlaceholderLayout = findViewById(R.id.upload_placeholder);
        btnViewDocs = findViewById(R.id.btn_view_uploaded_docs);
    }

    private void setupIdSpinner() {
        List<String> idTypes = new ArrayList<>();
        idTypes.add("-- Select ID Type --");
        idTypes.add("UMID");
        idTypes.add("Driver's License");
        idTypes.add("Philippine Passport");
        idTypes.add("PhilID (National ID)");
        idTypes.add("PRC ID");
        idTypes.add("Postal ID");
        idTypes.add("NBI Clearance");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                idTypes
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIdType.setAdapter(spinnerAdapter);
    }

    private void setupRecyclerView() {
        credentialList = new ArrayList<>();

        credentialAdapter = new CredentialAdapter(credentialList, true);

        recyclerCredentials.setLayoutManager(new LinearLayoutManager(this));
        recyclerCredentials.setAdapter(credentialAdapter);

        recyclerCredentials.setNestedScrollingEnabled(false);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnUploadId.setOnClickListener(v -> {
            pickingMainId = true;
            launchPicker();
        });

        btnAddNewCredential.setOnClickListener(v -> {
            pickingMainId = false;
            launchPicker();
        });

        btnSubmit.setOnClickListener(v -> validateAndSubmit());

        if (btnViewDocs != null) {
            btnViewDocs.setOnClickListener(v -> {
                Intent intent = new Intent(this, SeekerDocumentVaultActivity.class);
                startActivity(intent);
            });
        }
    }

    private void launchPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }
    private void validateAndSubmit() {
        if (spinnerIdType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select an ID type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedIdUri == null) {
            Toast.makeText(this, "Please upload a photo of your ID", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirm Submission")
                .setMessage("Submit your identity verification for review? This usually takes 24-48 hours.")
                .setPositiveButton("Submit", (dialog, which) -> performUpload())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void performUpload() {
        // Logic to upload to Firebase would go here
        Toast.makeText(this, "Verification Submitted! Manila LinkUp is reviewing your documents.", Toast.LENGTH_LONG).show();
        finish();
    }
}