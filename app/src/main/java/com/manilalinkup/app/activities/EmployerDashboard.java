package com.manilalinkup.app.activities;

import static com.manilalinkup.app.utilities.RetrofitClient.BASE_URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.adapters.JobPostDashboardAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.GetJobsRequest;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.R;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerDashboard extends AppCompatActivity {

    private RecyclerView recyclerViewJobPost;
    private JobPostDashboardAdapter adapterJobPost;
    private List<JobPostDashboardModel> jobListJobCard;
    private ProgressBar progressBarLoadMore;
    private TextView greetingNameText;
    private CardView jobAddJob;
    BottomNavigationView bottomNavigationViewEmployer;

    private boolean isLoading = false;
    private boolean hasMorePages = true;
    private String lastCreatedAt = null;
    private static final int PAGE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_dashboard);

        recyclerViewJobPost = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewJobPost.setLayoutManager(new LinearLayoutManager(this));
        progressBarLoadMore = findViewById(R.id.progress_bar_load_more);
        greetingNameText = findViewById(R.id.textview_greeting_name_employer);

        jobListJobCard = new ArrayList<>();

        adapterJobPost = new JobPostDashboardAdapter(jobListJobCard, false, new JobPostDashboardAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(JobPostDashboardModel job) {
                Intent intent = new Intent(EmployerDashboard.this, EmployerViewJobPost.class);
                intent.putExtra("JOB_TITLE", job.getJobTitle());
                intent.putExtra("EMPLOYER_NAME", job.getEmployerName());
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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && currentUser.getDisplayName() != null) {
            greetingNameText.setText(currentUser.getDisplayName());
        }

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_home);
        bottomNavigationViewEmployer.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_profile) {
                    Intent intent = new Intent(EmployerDashboard.this, EmployerProfileActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_notifications) {
                    Intent intent = new Intent(EmployerDashboard.this, EmployerNotificationsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_add_job) {
                    Intent intent = new Intent(EmployerDashboard.this, EmployerAddJobActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                } else if (id == R.id.nav_chat) {
                    Intent intent = new Intent(EmployerDashboard.this, ChatEmployerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    return true;
                }

                return false;
            }
        });

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

        user.getIdToken(true).addOnCompleteListener(tokenTask -> {
            if (!tokenTask.isSuccessful()) {
                isLoading = false;
                progressBarLoadMore.setVisibility(View.GONE);
                return;
            }

            String token = tokenTask.getResult().getToken();
            String uid = user.getUid();

            GetJobsRequest request = new GetJobsRequest(PAGE_SIZE, lastCreatedAt, null, null, uid);
            ApiService apiService = RetrofitClient.getClient(token).create(ApiService.class);

            apiService.getJobs(request).enqueue(new Callback<ApiResponse<List<JobModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<JobModel>>> call, Response<ApiResponse<List<JobModel>>> response) {
                    isLoading = false;
                    progressBarLoadMore.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        List<JobModel> jobs = response.body().getData();
                        if (jobs == null || jobs.isEmpty()) {
                            hasMorePages = false;
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
                    progressBarLoadMore.setVisibility(View.GONE);
                    ErrorUtils.showThrowableError(EmployerDashboard.this, t);
                }
            });
        });
    }

    private JobPostDashboardModel mapToDisplayModel(JobModel job) {
        String employerName = job.getEmployer() != null ? job.getEmployer().getFullName() : "";
        String photoUrl = job.getEmployer() != null ? BASE_URL + job.getEmployer().getProfilePhotoUrl() : "";
        return new JobPostDashboardModel(
            job.getTitle(),
            employerName,
            job.getLocation(),
            job.getDuration(),
            photoUrl,
            getRelativeTime(job.getCreatedAt())
        );
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
