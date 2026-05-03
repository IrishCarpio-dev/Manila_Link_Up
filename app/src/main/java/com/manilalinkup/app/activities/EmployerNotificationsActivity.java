package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import static com.manilalinkup.app.utilities.RetrofitClient.BASE_URL;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.NotificationsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.JobModel;
import com.manilalinkup.app.models.NotificationItemModel;
import com.manilalinkup.app.models.NotificationsModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.EmployerNavHelper;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.NotificationUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerNotificationsActivity extends BaseActivity {

    private RecyclerView recyclerViewNotifications;
    private NotificationsAdapter adapterNotif;
    private List<NotificationsModel> notifListCard;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private BottomNavigationView bottomNavigationViewEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_notifications);

        progressBar               = findViewById(R.id.progress_bar_notif);
        textViewEmpty             = findViewById(R.id.text_view_notif_empty);
        recyclerViewNotifications = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notifListCard = new ArrayList<>();
        adapterNotif  = new NotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        adapterNotif.setOnItemClickListener(this::handleNotificationClick);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        EmployerNavHelper.setup(this, bottomNavigationViewEmployer, R.id.nav_notifications);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }

    private void loadNotifications() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showLoading();

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getNotifications(null, null, null).enqueue(new Callback<ApiResponse<List<NotificationItemModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<NotificationItemModel>>> call,
                                       Response<ApiResponse<List<NotificationItemModel>>> response) {
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getData() != null) {
                        List<NotificationsModel> display = new ArrayList<>();
                        for (NotificationItemModel item : response.body().getData()) {
                            display.add(NotificationUtils.toDisplayModel(item));
                        }
                        showList(display);
                        markAllRead(result.getToken());
                    } else {
                        showEmpty();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<NotificationItemModel>>> call, Throwable t) {
                    showEmpty();
                    ErrorUtils.showThrowableError(EmployerNotificationsActivity.this, t);
                }
            });
        });
    }

    private void markAllRead(String token) {
        ApiService api = RetrofitClient.getClient(token).create(ApiService.class);
        api.markAllNotificationsRead().enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {}
            @Override public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {}
        });
    }

    private void handleNotificationClick(NotificationsModel notification) {
        String type = notification.getNotifType();
        if (type == null) return;
        Intent intent;
        switch (type) {
            case NotificationUtils.TYPE_NEW_APPLICANT:
                fetchJobAndOpen(notification.getJobId());
                return;
            case NotificationUtils.TYPE_RATING_RECEIVED:
            case NotificationUtils.TYPE_VERIFIED:
                intent = new Intent(this, EmployerProfileActivity.class);
                break;
            case NotificationUtils.TYPE_VERIFICATION_REJECTED:
                intent = new Intent(this, EmployerBusinessVerificationActivity.class);
                break;
            case NotificationUtils.TYPE_JOB_EXPIRING:
                fetchJobAndOpen(notification.getJobId());
                return;
            case NotificationUtils.TYPE_JOB_COMPLETED:
                intent = new Intent(this, EmployerViewAllRatings.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }

    private void fetchJobAndOpen(String jobId) {
        if (jobId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        showProgress("Loading...");

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getJob(jobId).enqueue(new Callback<ApiResponse<JobModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<JobModel>> call,
                                       Response<ApiResponse<JobModel>> response) {
                    hideProgress();
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getData() != null) {
                        startActivity(buildJobIntent(response.body().getData()));
                    } else {
                        ErrorUtils.showErrorMessage(EmployerNotificationsActivity.this, response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<JobModel>> call, Throwable t) {
                    hideProgress();
                    ErrorUtils.showThrowableError(EmployerNotificationsActivity.this, t);
                }
            });
        });
    }

    private Intent buildJobIntent(JobModel job) {
        String employerName = job.getEmployer() != null ? job.getEmployer().getFullName() : null;
        String photoUrl     = job.getEmployer() != null ? job.getEmployer().getProfilePhoto() : null;
        Intent intent = new Intent(this, EmployerViewJobPost.class);
        intent.putExtra("JOB_ID",          job.getId());
        intent.putExtra("JOB_TITLE",       job.getTitle());
        intent.putExtra("EMPLOYER_NAME",   employerName);
        intent.putExtra("LOCATION",        job.getLocation());
        intent.putExtra("DURATION",        job.getDuration());
        intent.putExtra("SALARY",          job.getSalary() != null ? job.getSalary() : 0.0);
        intent.putExtra("DESCRIPTION",     job.getDescription());
        intent.putExtra("EXPIRES_AT",      job.getExpiresAt());
        intent.putExtra("HOW_LONG_POSTED", NotificationUtils.relativeTime(job.getCreatedAt()));
        intent.putExtra("EMPLOYER_PHOTO",  photoUrl);
        intent.putExtra("IS_OWNER",        true);
        if (job.getTags() != null) {
            intent.putStringArrayListExtra("TAG_IDS", new ArrayList<>(job.getTags()));
        }
        com.manilalinkup.app.models.ApplicantModel hired = job.getHiredApplication();
        if (hired != null) {
            intent.putExtra("APPLICATION_ID",       hired.getId());
            intent.putExtra("STATUS",               hired.getStatus() != null ? hired.getStatus() : 1);
            intent.putExtra("EMPLOYER_HAS_COMPLETED", hired.isEmployerCompleted());
            if (hired.getSeeker() != null) {
                String fn = hired.getSeeker().getFirstName() != null ? hired.getSeeker().getFirstName() : "";
                String ln = hired.getSeeker().getLastName()  != null ? hired.getSeeker().getLastName()  : "";
                intent.putExtra("SEEKER_NAME", (fn + " " + ln).trim());
            }
        }
        return intent;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerViewNotifications.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.GONE);
    }

    private void showList(List<NotificationsModel> items) {
        progressBar.setVisibility(View.GONE);
        if (items.isEmpty()) {
            showEmpty();
        } else {
            adapterNotif.updateData(items);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }

    private void showEmpty() {
        progressBar.setVisibility(View.GONE);
        recyclerViewNotifications.setVisibility(View.GONE);
        textViewEmpty.setVisibility(View.VISIBLE);
    }
}
