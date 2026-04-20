package com.manilalinkup.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.MarkCompleteRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerViewJobPost extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialButton viewApplicantsButton;
    private Button btnMarkComplete;
    private Button btnRate;

    private String jobId;
    private String applicationId;
    private String seekerName;
    private int currentStatus;
    private boolean employerHasCompleted;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_job_post);

        jobId               = getIntent().getStringExtra("JOB_ID");
        applicationId       = getIntent().getStringExtra("APPLICATION_ID");
        seekerName          = getIntent().getStringExtra("SEEKER_NAME");
        currentStatus       = getIntent().getIntExtra("STATUS", 1);
        employerHasCompleted = getIntent().getBooleanExtra("EMPLOYER_HAS_COMPLETED", false);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        viewApplicantsButton = findViewById(R.id.button_view_applicants);
        viewApplicantsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EmployerListOfApplicants.class);
            intent.putExtra("JOB_ID", jobId);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnMarkComplete = findViewById(R.id.btn_mark_complete);
        btnRate         = findViewById(R.id.btn_rate);

        updateActionVisibility();
    }

    private void updateActionVisibility() {
        if (btnMarkComplete != null) {
            btnMarkComplete.setVisibility(currentStatus == 5 && !employerHasCompleted ? View.VISIBLE : View.GONE);
            btnMarkComplete.setOnClickListener(v -> markComplete());
        }

        if (btnRate != null) {
            btnRate.setVisibility(currentStatus == 6 ? View.VISIBLE : View.GONE);
            btnRate.setOnClickListener(v -> openRating());
        }
    }

    private void markComplete() {
        if (applicationId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        progressDialog.setMessage("Marking as complete...");
        progressDialog.show();

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.markApplicationComplete(new MarkCompleteRequest(applicationId))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        ApplicationModel updated = response.body().getData();
                        currentStatus = updated.getStatus() != null ? updated.getStatus() : currentStatus;
                        employerHasCompleted = true;
                        Toast.makeText(EmployerViewJobPost.this, "Marked as complete!", Toast.LENGTH_SHORT).show();
                        updateActionVisibility();
                    } else {
                        ErrorUtils.showErrorMessage(EmployerViewJobPost.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                    progressDialog.dismiss();
                    ErrorUtils.showThrowableError(EmployerViewJobPost.this, t);
                }
            });
        });
    }

    private void openRating() {
        Intent intent = new Intent(this, SubmitRatingActivity.class);
        intent.putExtra("APPLICATION_ID", applicationId);
        intent.putExtra("COUNTERPART_NAME", seekerName != null ? seekerName : "Worker");
        startActivity(intent);
    }
}
