package com.manilalinkup.app.activities;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.manilalinkup.app.utilities.SeekerNavHelper;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.google.android.material.badge.BadgeDrawable;
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.GetJobsRequest;
import com.manilalinkup.app.models.GetSeekerJobsRequest;
import com.manilalinkup.app.models.JobListResponse;
import com.manilalinkup.app.models.UnreadCountResponse;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.SeekerJobsResponse;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.models.UserProfileModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.ProfilePhotoCache;
import com.manilalinkup.app.utilities.RetrofitClient;
import com.manilalinkup.app.utilities.SessionCache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeekerDashboardActivity extends BaseActivity {

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    private ProgressBar progressBarLoadMore;
    private SwipeRefreshLayout swipeRefreshLayout;
    private BottomNavigationView bottomNavigationView;

    private ActivityResultLauncher<Intent> jobPostLauncher;

    private boolean isLoading = false;
    private boolean isRefreshing = false;
    private boolean hasMorePages = true;
    private boolean isCuratedExhausted = false;
    private boolean isVerified = false;
    private String lastExpiresAt = null;
    private String lastCreatedAt = null;
    private String lastStartAfter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_dashboard);

        jobPostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    String appliedJobId = result.getData().getStringExtra("JOB_ID");
                    if (appliedJobId != null) {
                        for (int i = 0; i < jobListJobCard.size(); i++) {
                            if (appliedJobId.equals(jobListJobCard.get(i).getJobId())) {
                                jobListJobCard.remove(i);
                                adapterJobPost.notifyItemRemoved(i);
                                break;
                            }
                        }
                    }
                }
            }
        );

        recyclerViewJobPost = findViewById(R.id.recycler_view_job_posts_dashboard);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        progressBarLoadMore = findViewById(R.id.progress_bar_load_more);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshJobs);

        jobListJobCard = new ArrayList<>();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(SeekerDashboardActivity.this, SeekerJobPostActivity.class);
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
                intent.putStringArrayListExtra("TAG_IDS", new ArrayList<>(job.getTagIds() != null ? job.getTagIds() : new ArrayList<>()));
                jobPostLauncher.launch(intent);
            }

        });
        recyclerViewJobPost.setAdapter(adapterJobPost);

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

        bottomNavigationView = findViewById(R.id.bottom_navigation_view_seeker);
        SeekerNavHelper.setup(this, bottomNavigationView, R.id.nav_home_seeker);

        UserProfileModel profile = SessionCache.getInstance().getUserProfile();
        isVerified = profile != null
                && profile.getSeekers() != null
                && Boolean.TRUE.equals(profile.getSeekers().getVerified());

        loadServiceTags();
        loadJobs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUnreadCount();
    }

    private void loadUnreadCount() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService apiService = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            apiService.getNotificationUnreadCount().enqueue(new Callback<UnreadCountResponse>() {
                @Override
                public void onResponse(Call<UnreadCountResponse> call, Response<UnreadCountResponse> response) {
                    if (!isFinishing() && response.isSuccessful() && response.body() != null) {
                        int count = response.body().getCount();
                        if (count > 0) {
                            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.nav_notifications_seeker);
                            badge.setNumber(count);
                        } else {
                            bottomNavigationView.removeBadge(R.id.nav_notifications_seeker);
                        }
                    }
                }
                @Override public void onFailure(Call<UnreadCountResponse> call, Throwable t) {}
            });
        });
    }

    private void refreshJobs() {
        isRefreshing = true;
        jobListJobCard.clear();
        adapterJobPost.notifyDataSetChanged();
        hasMorePages = true;
        isLoading = false;
        lastExpiresAt = null;
        lastCreatedAt = null;
        lastStartAfter = null;
        isCuratedExhausted = false;
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
            ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

            if (isVerified) {
                String mode = isCuratedExhausted ? "all" : "curated";
                GetSeekerJobsRequest request = new GetSeekerJobsRequest(mode, null, lastExpiresAt, lastCreatedAt);
                apiService.getSeekerJobs(request).enqueue(new Callback<SeekerJobsResponse>() {
                    @Override
                    public void onResponse(Call<SeekerJobsResponse> call, Response<SeekerJobsResponse> response) {
                        isLoading = false;
                        if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                        progressBarLoadMore.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            SeekerJobsResponse body = response.body();
                            List<JobModel> jobs = body.getData();
                            if (jobs != null && !jobs.isEmpty()) {
                                int insertStart = jobListJobCard.size();
                                for (JobModel job : jobs) jobListJobCard.add(mapToDisplayModel(job));
                                adapterJobPost.notifyItemRangeInserted(insertStart, jobs.size());
                            }
                            if (body.isHasMore() && body.getNextCursor() != null) {
                                lastExpiresAt = body.getNextCursor().getExpiresAt();
                                lastCreatedAt = body.getNextCursor().getCreatedAt();
                            } else if (!isCuratedExhausted) {
                                isCuratedExhausted = true;
                                lastExpiresAt = null;
                                lastCreatedAt = null;
                                hasMorePages = true;
                                loadJobs();
                            } else {
                                hasMorePages = false;
                            }
                        } else {
                            ErrorUtils.showErrorMessage(SeekerDashboardActivity.this, response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<SeekerJobsResponse> call, Throwable t) {
                        isLoading = false;
                        if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                        progressBarLoadMore.setVisibility(View.GONE);
                        ErrorUtils.showThrowableError(SeekerDashboardActivity.this, t);
                    }
                });
            } else {
                GetJobsRequest request = new GetJobsRequest(null, lastStartAfter, lastExpiresAt, null, null, null, null, null);
                apiService.getJobs(request).enqueue(new Callback<JobListResponse>() {
                    @Override
                    public void onResponse(Call<JobListResponse> call, Response<JobListResponse> response) {
                        isLoading = false;
                        if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                        progressBarLoadMore.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            JobListResponse body = response.body();
                            List<JobModel> jobs = body.getData();
                            if (jobs != null && !jobs.isEmpty()) {
                                int insertStart = jobListJobCard.size();
                                for (JobModel job : jobs) jobListJobCard.add(mapToDisplayModel(job));
                                adapterJobPost.notifyItemRangeInserted(insertStart, jobs.size());
                            }
                            if (body.isHasMore() && body.getNextCursor() != null) {
                                lastStartAfter = body.getNextCursor().getPrimary();
                                lastExpiresAt = body.getNextCursor().getExpiresAt();
                            } else {
                                hasMorePages = false;
                            }
                        } else {
                            ErrorUtils.showErrorMessage(SeekerDashboardActivity.this, response.errorBody());
                        }
                    }

                    @Override
                    public void onFailure(Call<JobListResponse> call, Throwable t) {
                        isLoading = false;
                        if (isRefreshing) { isRefreshing = false; swipeRefreshLayout.setRefreshing(false); }
                        progressBarLoadMore.setVisibility(View.GONE);
                        ErrorUtils.showThrowableError(SeekerDashboardActivity.this, t);
                    }
                });
            }
        });
    }

    private void loadServiceTags() {
        SessionCache.getInstance().ensureServiceTags(new SessionCache.ServiceTagsCallback() {
            @Override
            public void onAvailable(List<ServiceTagModel> tags) {
                adapterJobPost.setTagLabelsById(SessionCache.getInstance().getServiceTagLabelsById());
            }

            @Override
            public void onError() {
            }
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
        model.setHasApplied(job.isHasApplied());
        model.setApplicationStatus(job.getApplicationStatus());
        model.setChatId(job.getChatId());
        return model;
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
