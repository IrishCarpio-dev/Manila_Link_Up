package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.EmployerApplicantsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicantModel;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.GetApplicantsRequest;
import com.manilalinkup.app.models.UpdateApplicationStatusRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerListOfApplicants extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private RecyclerView applicantsRecyclerView;
    private EmployerApplicantsAdapter applicantsAdapter;
    private List<ApplicantModel> applicantsList;
    private String jobId;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_list_of_applicants);

        jobId = getIntent().getStringExtra("JOB_ID");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        applicantsRecyclerView = findViewById(R.id.recycler_view_applicants);
        applicantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading applicants...");
        progressDialog.setCancelable(false);

        applicantsList = new ArrayList<>();
        applicantsAdapter = new EmployerApplicantsAdapter(applicantsList, new EmployerApplicantsAdapter.OnActionListener() {
            @Override
            public void onInterview(ApplicantModel applicant) {
                updateStatus(applicant, 2);
            }

            @Override
            public void onHire(ApplicantModel applicant) {
                new AlertDialog.Builder(EmployerListOfApplicants.this)
                        .setTitle("Hire applicant?")
                        .setMessage("This will reject all other applicants and archive this job. Continue?")
                        .setPositiveButton("Hire", (d, w) -> updateStatus(applicant, 5))
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onOpenChat(ApplicantModel applicant) {
                Intent intent = new Intent(EmployerListOfApplicants.this, ChatThreadEmployer.class);
                intent.putExtra("CHAT_ID", applicant.getChatId());
                startActivity(intent);
            }
        });
        applicantsRecyclerView.setAdapter(applicantsAdapter);

        loadApplicants();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!applicantsList.isEmpty()) loadApplicants();
    }

    private void loadApplicants() {
        if (jobId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        progressDialog.show();
        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getApplicants(new GetApplicantsRequest(jobId, null, null, null))
                    .enqueue(new Callback<ApiResponse<List<ApplicantModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ApplicantModel>>> call, Response<ApiResponse<List<ApplicantModel>>> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        applicantsList.clear();
                        applicantsList.addAll(response.body().getData());
                        applicantsAdapter.notifyDataSetChanged();
                    } else {
                        ErrorUtils.showErrorMessage(EmployerListOfApplicants.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ApplicantModel>>> call, Throwable t) {
                    progressDialog.dismiss();
                    ErrorUtils.showThrowableError(EmployerListOfApplicants.this, t);
                }
            });
        });
    }

    private void updateStatus(ApplicantModel applicant, int newStatus) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        progressDialog.setMessage(newStatus == 5 ? "Hiring applicant..." : "Updating status...");
        progressDialog.show();

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.updateApplicationStatus(new UpdateApplicationStatusRequest(applicant.getId(), newStatus))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        String msg = newStatus == 2 ? "Moved to interview" : "Applicant hired!";
                        Toast.makeText(EmployerListOfApplicants.this, msg, Toast.LENGTH_SHORT).show();
                        loadApplicants();
                    } else {
                        ErrorUtils.showErrorMessage(EmployerListOfApplicants.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                    progressDialog.dismiss();
                    ErrorUtils.showThrowableError(EmployerListOfApplicants.this, t);
                }
            });
        });
    }
}
