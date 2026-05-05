package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.adapters.AppliedJobsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ArchiveJobModel;
import com.manilalinkup.app.models.GetArchivedJobsRequest;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerViewArchivedJobs extends BaseActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyState;
    private AppliedJobsAdapter.ArchiveJobAdapter adapter;
    private List<ArchiveJobModel> archiveList;
    private String currentStatusFilter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_view_archived_jobs);

        setupToolbar(R.id.toolbar);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.manila_blue);
        swipeRefreshLayout.setOnRefreshListener(this::loadArchivedJobs);

        emptyState = findViewById(R.id.empty_state_layout);

        recyclerView = findViewById(R.id.recycler_view_employer_archived_jobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        archiveList = new ArrayList<>();
        adapter = new AppliedJobsAdapter.ArchiveJobAdapter(archiveList);
        recyclerView.setAdapter(adapter);

        ChipGroup chipGroup = findViewById(R.id.chip_group_filter);
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            int checkedId = checkedIds.isEmpty() ? R.id.chip_all : checkedIds.get(0);
            if (checkedId == R.id.chip_archived) currentStatusFilter = "archived";
            else if (checkedId == R.id.chip_expired) currentStatusFilter = "expired";
            else if (checkedId == R.id.chip_completed) currentStatusFilter = "completed";
            else currentStatusFilter = null;
            archiveList.clear();
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(true);
            loadArchivedJobs();
        });

        swipeRefreshLayout.setRefreshing(true);
        loadArchivedJobs();
    }

    private void updateEmptyState() {
        if (archiveList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadArchivedJobs() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getArchivedJobs(new GetArchivedJobsRequest(null, currentStatusFilter))
                    .enqueue(new Callback<ApiResponse<List<ArchiveJobModel>>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<List<ArchiveJobModel>>> call,
                                               Response<ApiResponse<List<ArchiveJobModel>>> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                archiveList.clear();
                                archiveList.addAll(response.body().getData());
                                adapter.notifyDataSetChanged();
                            } else {
                                ErrorUtils.showErrorMessage(EmployerViewArchivedJobs.this, response.errorBody());
                            }
                            updateEmptyState();
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<List<ArchiveJobModel>>> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            ErrorUtils.showThrowableError(EmployerViewArchivedJobs.this, t);
                            updateEmptyState();
                        }
                    });
        });
    }
}
