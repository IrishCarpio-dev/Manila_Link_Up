package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.CredentialAdapter;
import com.manilalinkup.app.models.CredentialModel;

import java.util.ArrayList;
import java.util.List;

public class EmployerPrivacyActivity extends BaseActivity {

    private ImageView btnBack;
    private Switch switchVisibility;
    private TextView btnUploadNewDoc, btnDownloadData, btnClearHistory, btnDeleteAccount;
    private RecyclerView recyclerDocsStatus;
    private CredentialAdapter adapter;
    private List<CredentialModel> docList;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_privacy_and_data);

        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        setupRecyclerView();
        setupListeners();
        loadDocumentStatuses();
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back_privacy);
        switchVisibility = findViewById(R.id.switch_company_visibility);

        recyclerDocsStatus = findViewById(R.id.recycler_business_docs_status);
        btnUploadNewDoc = findViewById(R.id.btn_upload_new_legal_doc);

        btnDownloadData = findViewById(R.id.btn_download_hiring_data);
        btnClearHistory = findViewById(R.id.btn_clear_job_history);
        btnDeleteAccount = findViewById(R.id.btn_close_business_account);
    }

    private void setupRecyclerView() {
        docList = new ArrayList<>();

        /* isEditable = false
           This ensures that in the Privacy/Vault view, the "X" is hidden
           and the Status Badge (Verified/Pending/Rejected) is shown instead.
        */
        adapter = new CredentialAdapter(docList, false);

        recyclerDocsStatus.setLayoutManager(new LinearLayoutManager(this));
        recyclerDocsStatus.setAdapter(adapter);

        // Prevents scrolling conflicts within the parent ScrollView
        recyclerDocsStatus.setNestedScrollingEnabled(false);
    }

    private void loadDocumentStatuses() {
        docList.clear();

        /* Using the updated CredentialModel constructor:
           new CredentialModel(FileName, Status)
        */
        docList.add(new CredentialModel("BIR Form 2303.pdf", "VERIFIED"));
        docList.add(new CredentialModel("SEC Registration.pdf", "PENDING"));
        docList.add(new CredentialModel("Mayor's Permit.jpg", "REJECTED"));

        adapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (switchVisibility != null) {
            switchVisibility.setOnCheckedChangeListener((buttonView, isChecked) -> {
                String msg = isChecked ? "Company is now discoverable" : "Company is now hidden from search";
                showToast(msg);
            });
        }

        // Navigate back to verification screen if they need to upload more
        if (btnUploadNewDoc != null) {
            btnUploadNewDoc.setOnClickListener(v -> {
                Intent intent = new Intent(this, EmployerBusinessVerificationActivity.class);
                startActivity(intent);
            });
        }

        if (btnDownloadData != null) {
            btnDownloadData.setOnClickListener(v ->
                    showToast("Preparing recruitment data for export... Check your email."));
        }

        if (btnClearHistory != null) {
            btnClearHistory.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Clear History")
                            .setMessage("Delete all search and applicant filtering logs?")
                            .setPositiveButton("Clear", (dialog, which) -> showToast("History cleared"))
                            .setNegativeButton("Cancel", null)
                            .show());
        }

        if (btnDeleteAccount != null) {
            btnDeleteAccount.setOnClickListener(v -> showDeleteAccountDialog());
        }
    }

    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Close Business Account")
                .setMessage("This action is permanent. All active job postings and applicant data will be deleted. Are you sure?")
                .setPositiveButton("Close Account", (dialog, which) -> {
                    showToast("Account termination request sent.");
                })
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}