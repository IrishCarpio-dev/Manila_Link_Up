package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.EmployerApplicantsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicantModel;
import com.manilalinkup.app.models.GetApplicantsRequest;
import com.manilalinkup.app.models.SeekerProfileModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerListOfApplicants extends BaseActivity {

    private RecyclerView applicantsRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EmployerApplicantsAdapter applicantsAdapter;
    private List<ApplicantModel> applicantsList;
    private String jobId;
    private String jobTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_list_of_applicants);

        jobId = getIntent().getStringExtra("JOB_ID");
        jobTitle = getIntent().getStringExtra("JOB_TITLE");

        setupToolbar(R.id.toolbar);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.manila_blue);
        swipeRefreshLayout.setOnRefreshListener(this::loadApplicants);

        applicantsRecyclerView = findViewById(R.id.recycler_view_applicants);
        applicantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        applicantsList = new ArrayList<>();
        applicantsAdapter = new EmployerApplicantsAdapter(applicantsList, this::openApplicantProfile);
        applicantsRecyclerView.setAdapter(applicantsAdapter);

        swipeRefreshLayout.setRefreshing(true);
        loadApplicants();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!applicantsList.isEmpty()) {
            swipeRefreshLayout.setRefreshing(true);
            loadApplicants();
        }
    }

    private void openApplicantProfile(ApplicantModel applicant) {
        Intent intent = new Intent(this, ApplicantProfileActivity.class);
        intent.putExtra("APPLICATION_ID", applicant.getId());
        intent.putExtra("SEEKER_UID", applicant.getSeekerUid());
        intent.putExtra("STATUS", applicant.getStatus() != null ? applicant.getStatus() : 1);
        intent.putExtra("CHAT_ID", applicant.getChatId());
        intent.putExtra("JOB_TITLE", jobTitle);

        SeekerProfileModel seeker = applicant.getSeeker();
        if (seeker != null) {
            intent.putExtra("FIRST_NAME", seeker.getFirstName());
            intent.putExtra("LAST_NAME", seeker.getLastName());
            intent.putExtra("LOCATION", seeker.getLocation());
            intent.putExtra("PROFILE_PHOTO_URL", seeker.getProfilePhotoUrl());
            intent.putExtra("RATING_COUNT", seeker.getRatingCount() != null ? seeker.getRatingCount() : 0);
            intent.putExtra("BAYESIAN_AVG", seeker.getBayesianAvg() != null ? seeker.getBayesianAvg() : 0.0);
        }

        startActivity(intent);
    }

    private void loadApplicants() {
        if (jobId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getApplicants(new GetApplicantsRequest(jobId, null, null, null))
                    .enqueue(new Callback<ApiResponse<List<ApplicantModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ApplicantModel>>> call, Response<ApiResponse<List<ApplicantModel>>> response) {
                    swipeRefreshLayout.setRefreshing(false);
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
                    swipeRefreshLayout.setRefreshing(false);
                    ErrorUtils.showThrowableError(EmployerListOfApplicants.this, t);
                }
            });
        });
    }
}
