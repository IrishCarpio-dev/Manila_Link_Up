package com.manilalinkup.app.activities;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.manilalinkup.app.utilities.EmployerNavHelper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ArchiveJobRequest;
import com.manilalinkup.app.models.GetJobsRequest;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.ProfilePhotoCache;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import okhttp3.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerDashboard extends BaseActivity {

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout emptyState;
    private TextView greetingNameText;
    private CardView jobAddJob;
    BottomNavigationView bottomNavigationViewEmployer;

    private boolean isLoading = false;
    private boolean isRefreshing = false;
    private boolean hasMorePages = true;
    private String lastCreatedAt = null;
    private static final int PAGE_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_dashboard);

        recyclerViewJobPost = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        progressBarLoadMore = findViewById(R.id.progress_bar_load_more);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        emptyState = findViewById(R.id.empty_state_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshJobs);
        greetingNameText = findViewById(R.id.textview_greeting_name_employer);

        jobListJobCard = new ArrayList<>();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(EmployerDashboard.this, EmployerViewJobPost.class);
                intent.putExtra("JOB_ID", job.getJobId());
                intent.putExtra("JOB_TITLE", job.getJobTitle());
                intent.putExtra("EMPLOYER_NAME", job.getEmployerName());
                intent.putExtra("LOCATION", job.getJobPostLocation());
                intent.putExtra("DURATION", job.getJob_duration());
                intent.putExtra("SALARY", job.getSalary() != null ? job.getSalary() : 0.0);
                intent.putExtra("DESCRIPTION", job.getDescription());
                intent.putExtra("EXPIRES_AT", job.getExpiresAt());
                intent.putExtra("HOW_LONG_POSTED", job.getHowLongJobIsPosted());
                intent.putExtra("EMPLOYER_PHOTO", job.getEmployerProfilePicture());
                intent.putStringArrayListExtra("TAG_IDS", new ArrayList<>(job.getTagIds() != null ? job.getTagIds() : Collections.emptyList()));
                intent.putExtra("IS_OWNER", true);
                if (job.getApplicationId() != null) {
                    intent.putExtra("APPLICATION_ID", job.getApplicationId());
                    intent.putExtra("STATUS", job.getApplicationStatus() != null ? job.getApplicationStatus() : 1);
                    intent.putExtra("EMPLOYER_HAS_COMPLETED", job.isEmployerHasCompleted());
                    intent.putExtra("SEEKER_NAME", job.getSeekerName());
                }
                startActivity(intent);
            }
        });


            recyclerViewJobPost.setAdapter(adapterJobPost);

        loadServiceTags();

        recyclerViewJobPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                int lastVisible = lm.findLastVisibleItemPosition();
                int total = lm.getItemCount();
                if (!isLoading && hasMorePages && lastVisible >= total - 3) {
                    loadJobs();
                }
            }
        });

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            greetingNameText.setText(currentUser.getDisplayName());
        }

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        EmployerNavHelper.setup(this, bottomNavigationViewEmployer, R.id.nav_home);

        loadJobs();

        jobAddJob = findViewById(R.id.card_view_post_new_job);
        jobAddJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EmployerDashboard.this, EmployerAddJobActivity.class);
                startActivity(intent);
            }
        });

    }

    private void refreshJobs() {
        isRefreshing = true;
        jobListJobCard.clear();
        adapterJobPost.notifyDataSetChanged();
        hasMorePages = true;
        isLoading = false;
        lastCreatedAt = null;
        loadJobs();
    }

    private void loadJobs() {
        if (isLoading || !hasMorePages) return;
        isLoading = true;
        if (!isRefreshing) progressBarLoadMore.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            isLoading = false;
            progressBarLoadMore.setVisibility(View.GONE);
            if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
            return;
        }

        user.getIdToken(false).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                isLoading = false;
                progressBarLoadMore.setVisibility(View.GONE);
                if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                return;
            }

            String token = tokenTask.getResult().getToken();
            String uid = user.getUid();

            GetJobsRequest request = new GetJobsRequest(PAGE_SIZE, lastCreatedAt, null, null, uid, null, null);
            ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

            apiService.getJobs(request).enqueue(new Callback<ApiResponse<List<JobModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<JobModel>>> call, Response<ApiResponse<List<JobModel>>> response) {
                    isLoading = false;
                    if (isRefreshing) {
                        isRefreshing = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressBarLoadMore.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<JobModel> jobs = response.body().getData();
                        if (jobs == null || jobs.isEmpty()) {
                            hasMorePages = false;
                            if (jobListJobCard.isEmpty()) {
                                emptyState.setVisibility(View.VISIBLE);
                                recyclerViewJobPost.setVisibility(View.GONE);
                            }
                            return;
                        }
                        if (jobs.size() < PAGE_SIZE) hasMorePages = false;
                        lastCreatedAt = jobs.get(jobs.size() - 1).getCreatedAt();
                        int insertStart = jobListJobCard.size();
                        for (JobModel job : jobs) {
                            jobListJobCard.add(mapToDisplayModel(job));
                        }
                        adapterJobPost.notifyItemRangeInserted(insertStart, jobs.size());
                    } else {
                        ErrorUtils.showErrorMessage(EmployerDashboard.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<JobModel>>> call, Throwable t) {
                    isLoading = false;
                    if (isRefreshing) {
                        isRefreshing = false;
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    progressBarLoadMore.setVisibility(View.GONE);
                    ErrorUtils.showThrowableError(EmployerDashboard.this, t);
                    if (jobListJobCard.isEmpty()) {
                        emptyState.setVisibility(View.VISIBLE);
                        recyclerViewJobPost.setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    private JobPostDashboardModel mapToDisplayModel(JobModel job) {
        String employerName = job.getEmployer() != null ? job.getEmployer().getFullName() : "";
        String employerUid = job.getEmployer() != null ? job.getEmployer().getUid() : null;
        String photoUrl = job.getEmployer() != null ? job.getEmployer().getProfilePhoto() : null;
        if (employerUid != null && photoUrl != null) {
            ProfilePhotoCache.getInstance().put(employerUid, photoUrl);
        }
        JobPostDashboardModel model = new JobPostDashboardModel(
            job.getTitle(),
            employerName,
            job.getLocation(),
            job.getDuration(),
            photoUrl,
            getRelativeTime(job.getCreatedAt())
        );
        model.setJobId(job.getId());
        model.setEmployerUid(employerUid);
        model.setTagIds(job.getTags());
        model.setSalary(job.getSalary());
        model.setDescription(job.getDescription());
        model.setExpiresAt(job.getExpiresAt());

        com.manilalinkup.app.models.ApplicantModel hired = job.getHiredApplication();
        if (hired != null) {
            model.setApplicationId(hired.getId());
            model.setApplicationStatus(hired.getStatus());
            model.setEmployerHasCompleted(hired.isEmployerCompleted());
            if (hired.getSeeker() != null) {
                String firstName = hired.getSeeker().getFirstName() != null ? hired.getSeeker().getFirstName() : "";
                String lastName = hired.getSeeker().getLastName() != null ? hired.getSeeker().getLastName() : "";
                model.setSeekerName((firstName + " " + lastName).trim());
            }
        }

        return model;
    }

    private void loadServiceTags() {
        SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
            @Override
            public void onAvailable(List<ServiceTagModel> tags) {
                adapterJobPost.setTagLabelsById(SessionCache.getInstance().getServiceTagLabelsById());
            }

            @Override
            public void onError() {
                // Non-fatal: cards will still render without tag labels.
            }
        });
    }

    private void archiveJob(JobPostDashboardModel job, int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Archiving job...");
        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.archiveJob(new ArchiveJobRequest(job.getJobId()))
                    .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgress();
                    if (response.isSuccessful()) {
                        jobListJobCard.remove(position);
                        adapterJobPost.notifyItemRemoved(position);
                        adapterJobPost.notifyItemRangeChanged(position, jobListJobCard.size());
                        Toast.makeText(EmployerDashboard.this, "Job archived", Toast.LENGTH_SHORT).show();
                        if (jobListJobCard.isEmpty()) {
                            emptyState.setVisibility(View.VISIBLE);
                            recyclerViewJobPost.setVisibility(View.GONE);
                        }
                    } else {
                        ErrorUtils.showErrorMessage(EmployerDashboard.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(EmployerDashboard.this, t);
                }
            });
        });
    }

    private String getRelativeTime(String createdAt) {
        if (createdAt == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.US);
            Date date = sdf.parse(createdAt);
            long diffMs = System.currentTimeMillis() - date.getTime();
            long minutes = diffMs / 60000;
            if (minutes < 60) return minutes <= 1 ? "just now" : minutes + " minutes ago";
            long hours = minutes / 60;
            if (hours < 24) return hours == 1 ? "1 hour ago" : hours + " hours ago";
            long days = hours / 24;
            if (days < 30) return days == 1 ? "1 day ago" : days + " days ago";
            long months = days / 30;
            return months == 1 ? "1 month ago" : months + " months ago";
        } catch (Exception e) {
            return "";
        }
    }
}
