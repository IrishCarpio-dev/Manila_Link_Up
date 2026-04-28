package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.AppliedJobsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.AppliedJobModel;
import com.manilalinkup.app.models.GetAppliedJobsRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppliedSeekerActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyState;
    private AppliedJobsAdapter appliedAdapter;
    private List<AppliedJobModel> appliedList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applied_seeker);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setSelectedItemId(R.id.nav_activity_seeker);
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home_seeker) {
                startActivity(new Intent(AppliedSeekerActivity.this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_notifications_seeker) {
                startActivity(new Intent(AppliedSeekerActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_chat_seeker) {
                startActivity(new Intent(AppliedSeekerActivity.this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (menuItem.getItemId() == R.id.nav_profile_seeker) {
                startActivity(new Intent(AppliedSeekerActivity.this, SeekerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });

        recyclerView = findViewById(R.id.recycler_view_employer_own_posts);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        emptyState = findViewById(R.id.empty_state_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.manila_blue);
        swipeRefreshLayout.setOnRefreshListener(this::fetchAppliedJobs);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appliedList = new ArrayList<>();

        initAdapter();
        recyclerView.setAdapter(appliedAdapter);

        fetchAppliedJobs();
    }

    private void fetchAppliedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        swipeRefreshLayout.setRefreshing(true);
        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getAppliedJobs(new GetAppliedJobsRequest(null, null, null))
                    .enqueue(new Callback<ApiResponse<List<AppliedJobModel>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<AppliedJobModel>>> call,
                                               Response<ApiResponse<List<AppliedJobModel>>> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                appliedList.clear();
                                appliedList.addAll(response.body().getData());
                                appliedAdapter.notifyDataSetChanged();
                            } else {
                                ErrorUtils.showErrorMessage(AppliedSeekerActivity.this, response.errorBody());
                            }
                            checkEmptyState(appliedList);
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<AppliedJobModel>>> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            ErrorUtils.showThrowableError(AppliedSeekerActivity.this, t);
                            checkEmptyState(appliedList);
                        }
                    });
        });
    }

    private void checkEmptyState(List<?> list) {
        if (list.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        appliedAdapter = new AppliedJobsAdapter(appliedList, job -> {
            Intent intent = new Intent(AppliedSeekerActivity.this, AppliedJobPostActivity.class);
            intent.putExtra("APPLICATION_ID", job.getId());
            intent.putExtra("STATUS", job.getStatus() != null ? job.getStatus() : 1);
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
    }
}
