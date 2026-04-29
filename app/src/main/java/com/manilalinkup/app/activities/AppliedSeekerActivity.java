package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.manilalinkup.app.models.CompletedJobsResponse;
import com.manilalinkup.app.models.GetAppliedJobsRequest;
import com.manilalinkup.app.models.GetCompletedJobsRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.SeekerNavHelper;
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

    private RecyclerView completedRecyclerView;
    private SwipeRefreshLayout completedSwipeRefresh;
    private LinearLayout completedEmptyState;
    private AppliedJobsAdapter completedAdapter;
    private List<AppliedJobModel> completedList;

    private LinearLayout tabActive;
    private LinearLayout tabCompleted;
    private TextView tvTabActive;
    private TextView tvTabCompleted;
    private View indicatorActive;
    private View indicatorCompleted;

    private boolean completedLoaded = false;
    private boolean activeTab = true;

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_applied_seeker);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        SeekerNavHelper.setup(this, bottomNavigationView, R.id.nav_activity_seeker);

        tabActive = findViewById(R.id.tab_active);
        tabCompleted = findViewById(R.id.tab_completed);
        tvTabActive = findViewById(R.id.tv_tab_active);
        tvTabCompleted = findViewById(R.id.tv_tab_completed);
        indicatorActive = findViewById(R.id.indicator_active);
        indicatorCompleted = findViewById(R.id.indicator_completed);

        recyclerView = findViewById(R.id.recycler_view_employer_own_posts);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        emptyState = findViewById(R.id.empty_state_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.manila_blue);
        swipeRefreshLayout.setOnRefreshListener(this::fetchAppliedJobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        appliedList = new ArrayList<>();
        initAppliedAdapter();
        recyclerView.setAdapter(appliedAdapter);

        completedRecyclerView = findViewById(R.id.recycler_view_completed);
        completedSwipeRefresh = findViewById(R.id.swipe_refresh_completed);
        completedEmptyState = findViewById(R.id.empty_state_completed);
        completedSwipeRefresh.setColorSchemeResources(R.color.manila_blue);
        completedSwipeRefresh.setOnRefreshListener(this::fetchCompletedJobs);
        completedRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        completedList = new ArrayList<>();
        initCompletedAdapter();
        completedRecyclerView.setAdapter(completedAdapter);

        tabActive.setOnClickListener(v -> selectTab(true));
        tabCompleted.setOnClickListener(v -> selectTab(false));

        fetchAppliedJobs();
    }

    private void selectTab(boolean active) {
        if (activeTab == active) return;
        activeTab = active;

        if (active) {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            completedSwipeRefresh.setVisibility(View.GONE);
            completedEmptyState.setVisibility(View.GONE);
            checkEmptyState(appliedList, emptyState, recyclerView);

            tvTabActive.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTabActive.setTextColor(getResources().getColor(R.color.manila_blue, getTheme()));
            indicatorActive.setVisibility(View.VISIBLE);

            tvTabCompleted.setTypeface(null, android.graphics.Typeface.NORMAL);
            tvTabCompleted.setTextColor(getResources().getColor(R.color.dark_text, getTheme()));
            indicatorCompleted.setVisibility(View.INVISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.GONE);
            emptyState.setVisibility(View.GONE);
            completedSwipeRefresh.setVisibility(View.VISIBLE);

            tvTabCompleted.setTypeface(null, android.graphics.Typeface.BOLD);
            tvTabCompleted.setTextColor(getResources().getColor(R.color.manila_blue, getTheme()));
            indicatorCompleted.setVisibility(View.VISIBLE);

            tvTabActive.setTypeface(null, android.graphics.Typeface.NORMAL);
            tvTabActive.setTextColor(getResources().getColor(R.color.dark_text, getTheme()));
            indicatorActive.setVisibility(View.INVISIBLE);

            if (!completedLoaded) {
                completedLoaded = true;
                fetchCompletedJobs();
            } else {
                checkEmptyState(completedList, completedEmptyState, completedRecyclerView);
            }
        }
    }

    private void fetchAppliedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        swipeRefreshLayout.setRefreshing(true);
        user.getIdToken(false).addOnSuccessListener(result -> {
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
                            checkEmptyState(appliedList, emptyState, recyclerView);
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<AppliedJobModel>>> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            ErrorUtils.showThrowableError(AppliedSeekerActivity.this, t);
                            checkEmptyState(appliedList, emptyState, recyclerView);
                        }
                    });
        });
    }

    private void fetchCompletedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        completedSwipeRefresh.setRefreshing(true);
        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getCompletedJobs(new GetCompletedJobsRequest(null, null))
                    .enqueue(new Callback<CompletedJobsResponse>() {
                        @Override
                        public void onResponse(Call<CompletedJobsResponse> call,
                                               Response<CompletedJobsResponse> response) {
                            completedSwipeRefresh.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                completedList.clear();
                                completedList.addAll(response.body().getData());
                                completedAdapter.notifyDataSetChanged();
                            } else {
                                ErrorUtils.showErrorMessage(AppliedSeekerActivity.this, response.errorBody());
                            }
                            checkEmptyState(completedList, completedEmptyState, completedRecyclerView);
                        }

                        @Override
                        public void onFailure(Call<CompletedJobsResponse> call, Throwable t) {
                            completedSwipeRefresh.setRefreshing(false);
                            ErrorUtils.showThrowableError(AppliedSeekerActivity.this, t);
                            checkEmptyState(completedList, completedEmptyState, completedRecyclerView);
                        }
                    });
        });
    }

    private void checkEmptyState(List<?> list, LinearLayout empty, RecyclerView rv) {
        if (list.isEmpty()) {
            empty.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        } else {
            empty.setVisibility(View.GONE);
            rv.setVisibility(View.VISIBLE);
        }
    }

    private void initAppliedAdapter() {
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

    private void initCompletedAdapter() {
        completedAdapter = new AppliedJobsAdapter(completedList, job -> {
            Intent intent = new Intent(AppliedSeekerActivity.this, AppliedJobPostActivity.class);
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
    }
}
