package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.EmployerApplicantsAdapter;
import com.manilalinkup.app.models.ApplicantModel;
import com.manilalinkup.app.models.ApplicantsResponse;
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
    private ScrollView scrollView;
    private ProgressBar loadingMoreProgress;
    private EmployerApplicantsAdapter applicantsAdapter;
    private List<ApplicantModel> applicantsList;
    private String jobId;
    private String jobTitle;

    private String nextCursor = null;
    private boolean hasMore = false;
    private boolean isLoadingMore = false;

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
        swipeRefreshLayout.setOnRefreshListener(() -> loadApplicants(false));

        scrollView = findViewById(R.id.scroll_view_applicants);
        loadingMoreProgress = findViewById(R.id.loading_more_progress);

        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (!hasMore || isLoadingMore) return;
            View child = scrollView.getChildAt(0);
            if (child == null) return;
            int diff = child.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
            if (diff <= 300) loadApplicants(true);
        });

        applicantsRecyclerView = findViewById(R.id.recycler_view_applicants);
        applicantsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicantsRecyclerView.setNestedScrollingEnabled(false);

        applicantsList = new ArrayList<>();
        applicantsAdapter = new EmployerApplicantsAdapter(applicantsList, this::openApplicantProfile);
        applicantsRecyclerView.setAdapter(applicantsAdapter);

        swipeRefreshLayout.setRefreshing(true);
        loadApplicants(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!applicantsList.isEmpty()) {
            swipeRefreshLayout.setRefreshing(true);
            loadApplicants(false);
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
            intent.putExtra("RATING_COUNT", seeker.getRatingCount() != null ? seeker.getRatingCount() : 0);
            intent.putExtra("BAYESIAN_AVG", seeker.getBayesianAvg() != null ? seeker.getBayesianAvg() : 0.0);
        }

        startActivity(intent);
    }

    private void loadApplicants(boolean loadMore) {
        if (jobId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        if (loadMore) {
            if (isLoadingMore || !hasMore) return;
            isLoadingMore = true;
            loadingMoreProgress.setVisibility(View.VISIBLE);
        } else {
            nextCursor = null;
            swipeRefreshLayout.setRefreshing(true);
        }

        String cursor = loadMore ? nextCursor : null;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getApplicants(new GetApplicantsRequest(jobId, null, cursor, null))
                    .enqueue(new Callback<ApplicantsResponse>() {
                        @Override
                        public void onResponse(Call<ApplicantsResponse> call, Response<ApplicantsResponse> response) {
                            swipeRefreshLayout.setRefreshing(false);
                            isLoadingMore = false;
                            loadingMoreProgress.setVisibility(View.GONE);
                            if (response.isSuccessful() && response.body() != null
                                    && response.body().getData() != null) {
                                if (!loadMore) applicantsList.clear();
                                int insertPos = applicantsList.size();
                                applicantsList.addAll(response.body().getData());
                                if (loadMore) {
                                    applicantsAdapter.notifyItemRangeInserted(insertPos, response.body().getData().size());
                                } else {
                                    applicantsAdapter.notifyDataSetChanged();
                                }
                                hasMore = response.body().isHasMore();
                                nextCursor = response.body().getNextCursor();
                            } else {
                                ErrorUtils.showErrorMessage(EmployerListOfApplicants.this, response.errorBody());
                            }
                        }

                        @Override
                        public void onFailure(Call<ApplicantsResponse> call, Throwable t) {
                            swipeRefreshLayout.setRefreshing(false);
                            isLoadingMore = false;
                            loadingMoreProgress.setVisibility(View.GONE);
                            ErrorUtils.showThrowableError(EmployerListOfApplicants.this, t);
                        }
                    });
        });
    }
}
