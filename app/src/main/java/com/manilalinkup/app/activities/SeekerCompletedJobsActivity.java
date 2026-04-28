package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.AppliedJobsAdapter;
import com.manilalinkup.app.models.AppliedJobModel;
import com.manilalinkup.app.models.CompletedJobsResponse;
import com.manilalinkup.app.models.GetCompletedJobsRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeekerCompletedJobsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyState;
    private AppliedJobsAdapter adapter;
    private List<AppliedJobModel> completedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_completed_jobs);

        setupToolbar(R.id.toolbar);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.manila_blue);
        swipeRefreshLayout.setOnRefreshListener(this::loadCompletedJobs);

        recyclerView = findViewById(R.id.recycler_view_completed_jobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        emptyState = findViewById(R.id.empty_state_layout);

        completedList = new ArrayList<>();
        adapter = new AppliedJobsAdapter(completedList, job -> {
            Intent intent = new Intent(SeekerCompletedJobsActivity.this, AppliedJobPostActivity.class);
            intent.putExtra("APPLICATION_ID", job.getId());
            intent.putExtra("STATUS", job.getStatus() != null ? job.getStatus() : 6);
            intent.putExtra("SEEKER_HAS_COMPLETED", job.getSeekerCompletedAt() != null);
            intent.putExtra("CREATED_AT", job.getCreatedAt());
            if (job.getJob() != null) {
                intent.putExtra("JOB_TITLE", job.getJob().getTitle());
                intent.putExtra("LOCATION", job.getJob().getLocation());
                intent.putExtra("SALARY", job.getJob().getSalary() != null ? job.getJob().getSalary() : 0.0);
                intent.putExtra("DURATION", job.getJob().getDuration());
                intent.putExtra("DESCRIPTION", job.getJob().getDescription());
                intent.putExtra("EXPIRES_AT", job.getJob().getExpiresAt());
                if (job.getJob().getEmployer() != null) {
                    intent.putExtra("EMPLOYER_NAME", job.getJob().getEmployer().getFullName());
                    intent.putExtra("EMPLOYER_PHOTO", job.getJob().getEmployer().getProfilePhotoUrl());
                }
            }
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
        loadCompletedJobs();
    }

    private void loadCompletedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getCompletedJobs(new GetCompletedJobsRequest(null, null))
                    .enqueue(new Callback<CompletedJobsResponse>() {
                        @Override
                        public void onResponse(Call<CompletedJobsResponse> call,
                                               Response<CompletedJobsResponse> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                completedList.clear();
                                completedList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                ErrorUtils.showErrorMessage(SeekerCompletedJobsActivity.this, response.errorBody());
                            }
                            updateEmptyState();
                        }

                        @Override
                        public void onFailure(Call<CompletedJobsResponse> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            ErrorUtils.showThrowableError(SeekerCompletedJobsActivity.this, t);
                            updateEmptyState();
                        }
                    });
        });
    }

    private void updateEmptyState() {
        if (completedList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
