package com.manilalinkup.app.activities;

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

import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.CredentialAdapter;
import com.manilalinkup.app.models.CredentialModel;

import java.util.ArrayList;
import java.util.List;

public class EmployerBusinessVerificationActivity extends BaseActivity {

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

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    if (isPickingMain) {
                        mainDocUri = uri;
                        imgMainPreview.setImageURI(uri);
                        imgMainPreview.setVisibility(View.VISIBLE);
                        uploadPlaceholder.setVisibility(View.GONE);
                    } else {
                        // Creating a unique name for the supporting document
                        String name = "Support_Doc_" + (supportingDocList.size() + 1);
                        supportingDocList.add(new CredentialModel(name, uri));

                        // Notify the adapter of the new item
                        adapter.notifyItemInserted(supportingDocList.size() - 1);
                        recyclerSupporting.scrollToPosition(supportingDocList.size() - 1);
                    }
                } else {
                    Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
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
        types.add("Mayor's Permit");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, types);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDocType.setAdapter(spinnerAdapter);
    }

    private void setupRecyclerView() {
        supportingDocList = new ArrayList<>();

        /* FIXED: Passing 'true' as the second parameter because this
           is the upload screen where items should be editable/removable.
        */
        adapter = new CredentialAdapter(supportingDocList, true);

        recyclerSupporting.setLayoutManager(new LinearLayoutManager(this));
        recyclerSupporting.setAdapter(adapter);

        // Disable nested scrolling to ensure smooth scrolling inside the parent ScrollView
        recyclerSupporting.setNestedScrollingEnabled(false);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

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
                Toast.makeText(this, "Please select a primary document type", Toast.LENGTH_SHORT).show();
            } else if (mainDocUri == null) {
                Toast.makeText(this, "Please upload your primary business document", Toast.LENGTH_SHORT).show();
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
                .setMessage("Manila LinkUp will review your business documents. This usually takes 1-3 business days.")
                .setPositiveButton("Submit", (dialog, which) -> {
                    // Here you would typically upload files to Firebase Storage
                    Toast.makeText(this, "Business Verification Submitted", Toast.LENGTH_LONG).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}