package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.CredentialAdapter;
import com.manilalinkup.app.models.CredentialModel;

import java.util.ArrayList;
import java.util.List;

public class SeekerDocumentVaultActivity extends BaseActivity {

    private ImageView btnBack;
    private ImageView imgMainId;
    private TextView tvIdType;
    private RecyclerView recyclerOtherDocs;
    private CredentialAdapter adapter;
    private List<CredentialModel> credentialList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seeker_document_vault);

        initializeViews();
        setupRecyclerView();
        loadDocuments();

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void initializeViews() {
        btnBack = findViewById(R.id.btn_back_vault);
        imgMainId = findViewById(R.id.img_vault_main_id);
        tvIdType = findViewById(R.id.tv_main_id_type);
        recyclerOtherDocs = findViewById(R.id.recycler_vault_docs);
    }

    private void setupRecyclerView() {
        credentialList = new ArrayList<>();

        adapter = new CredentialAdapter(credentialList, false);

        recyclerOtherDocs.setLayoutManager(new LinearLayoutManager(this));
        recyclerOtherDocs.setAdapter(adapter);

        // Prevents scrolling conflicts if the layout is inside a ScrollView
        recyclerOtherDocs.setNestedScrollingEnabled(false);
    }

    private void loadDocuments() {
        // Example: Primary ID Display
        tvIdType.setText("Philippine Passport - VERIFIED");

        // Clear list to avoid duplicates on reload
        credentialList.clear();

        /* UPDATED: Using the new constructor in CredentialModel
           Passing (FileName, Status)
        */
        credentialList.add(new CredentialModel("NBI Clearance.pdf", "VERIFIED"));
        credentialList.add(new CredentialModel("Barangay Certificate.jpg", "PENDING"));
        credentialList.add(new CredentialModel("UMID_Front.png", "REJECTED"));

        adapter.notifyDataSetChanged();
    }
}