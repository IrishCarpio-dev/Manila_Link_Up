package com.manilalinkup.app.activities;

import static com.manilalinkup.app.utilities.RetrofitClient.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.GetSeekerJobsRequest;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.SeekerJobsResponse;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.models.ServiceTagModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
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

public class SeekerDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    private ProgressBar progressBarLoadMore;
    private BottomNavigationView bottomNavigationView;

    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private boolean isCuratedExhausted = false;
    private String lastExpiresAt = null;
    private String lastCreatedAt = null;
    private static final int PAGE_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_dashboard);

        recyclerViewJobPost = findViewById(R.id.recycler_view_job_posts_dashboard);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        progressBarLoadMore = findViewById(R.id.progress_bar_load_more);

        jobListJobCard = new ArrayList<>();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(SeekerDashboardActivity.this, SeekerJobPostActivity.class);
                intent.putExtra("JOB_ID", job.getJobId());
                startActivity(intent);
            }

            @Override
            public void onRemoveClick(JobPostDashboardModel job, int position) {
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
        bottomNavigationView.setSelectedItemId(R.id.nav_home_seeker);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home_seeker) {
                    return true;
                } else if (id == R.id.nav_profile_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, SeekerProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_notifications_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, EmployerNotificationsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }else if (id == R.id.nav_activity_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, AppliedSeekerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_chat_seeker) {
                    Intent intent = new Intent(SeekerDashboardActivity.this, ChatSeekerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

        loadServiceTags();
        loadJobs();
    }

    private void loadJobs() {
        if (isLoading || !hasMorePages) return;
        isLoading = true;
        progressBarLoadMore.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            isLoading = false;
            progressBarLoadMore.setVisibility(View.GONE);
            return;
        }

        String mode = isCuratedExhausted ? "all" : "curated";

        Log.d("LoadJobs", mode);

        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                isLoading = false;
                progressBarLoadMore.setVisibility(View.GONE);
                return;
            }

            String token = tokenTask.getResult().getToken();
            ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);
            GetSeekerJobsRequest request = new GetSeekerJobsRequest(mode, PAGE_SIZE, lastExpiresAt, lastCreatedAt);

            apiService.getSeekerJobs(request).enqueue(new Callback<SeekerJobsResponse>() {
                @Override
                public void onResponse(Call<SeekerJobsResponse> call, Response<SeekerJobsResponse> response) {
                    isLoading = false;
                    progressBarLoadMore.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        SeekerJobsResponse body = response.body();
                        List<JobModel> jobs = body.getData();

                        if (jobs != null && !jobs.isEmpty()) {
                            int insertStart = jobListJobCard.size();
                            for (JobModel job : jobs) {
                                jobListJobCard.add(mapToDisplayModel(job));
                            }
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
                    progressBarLoadMore.setVisibility(View.GONE);
                    ErrorUtils.showThrowableError(SeekerDashboardActivity.this, t);
                }
            });
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
        String photoUrl = job.getEmployer() != null ? BASE_URL + job.getEmployer().getProfilePhotoUrl() : "";
        JobPostDashboardModel model = new JobPostDashboardModel(
            job.getTitle(),
            employerName,
            job.getLocation(),
            job.getDuration(),
            photoUrl,
            getRelativeTime(job.getCreatedAt())
        );
        model.setJobId(job.getId());
        model.setTagIds(job.getTags());
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
