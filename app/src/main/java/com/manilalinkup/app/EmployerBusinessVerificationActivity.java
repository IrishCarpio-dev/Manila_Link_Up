package com.manilalinkup.app;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class EmployerBusinessVerificationActivity extends AppCompatActivity {

    private ImageView btnBack, imgMainPreview;
    private Spinner spinnerDocType;
    private CardView btnUploadMain;
    private View uploadPlaceholder;
    private TextView btnAddSupporting;
    private RecyclerView recyclerSupporting;
    private Button btnSubmit;

    private CredentialAdapter adapter;
    private List<CredentialModel> supportingDocList;
    private Uri mainDocUri;
    private boolean isPickingMain = true;

    // Unified Photo Picker
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    if (isPickingMain) {
                        mainDocUri = uri;
                        imgMainPreview.setImageURI(uri);
                        imgMainPreview.setVisibility(View.VISIBLE);
                        uploadPlaceholder.setVisibility(View.GONE);
                    } else {
                        String name = "Support_Doc_" + (supportingDocList.size() + 1);
                        supportingDocList.add(new CredentialModel(name, uri));
                        adapter.notifyItemInserted(supportingDocList.size() - 1);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_verification);

        initializeViews();
        setupSpinner();
        setupRecyclerView();
        setupListeners();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back_verify);
        spinnerDocType = findViewById(R.id.spinner_business_doc_type);
        btnUploadMain = findViewById(R.id.btn_upload_main_doc);
        imgMainPreview = findViewById(R.id.img_main_doc_preview);
        uploadPlaceholder = findViewById(R.id.upload_placeholder_main);
        btnAddSupporting = findViewById(R.id.btn_add_supporting_doc);
        recyclerSupporting = findViewById(R.id.recycler_supporting_docs);
        btnSubmit = findViewById(R.id.btn_submit_business_verification);
    }

    private void setupSpinner() {
        List<String> types = new ArrayList<>();
        types.add("-- Select Primary Document --");
        types.add("BIR Form 2303 (COR)");
        types.add("SEC Registration");
        types.add("DTI Certificate");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocType.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        supportingDocList = new ArrayList<>();
        adapter = new CredentialAdapter(supportingDocList);
        recyclerSupporting.setLayoutManager(new LinearLayoutManager(this));
        recyclerSupporting.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnUploadMain.setOnClickListener(v -> {
            isPickingMain = true;
            launchPicker();
        });

        btnAddSupporting.setOnClickListener(v -> {
            isPickingMain = false;
            launchPicker();
        });

        btnSubmit.setOnClickListener(v -> {
            if (spinnerDocType.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Select a document type", Toast.LENGTH_SHORT).show();
            } else if (mainDocUri == null) {
                Toast.makeText(this, "Upload your primary business document", Toast.LENGTH_SHORT).show();
            } else {
                showConfirmDialog();
            }
        });
    }

    private void launchPicker() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Submit Verification")
                .setMessage("Manila LinkUp will review your business documents. This takes 1-3 days.")
                .setPositiveButton("Submit", (d, w) -> {
                    Toast.makeText(this, "Application Submitted", Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}