package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.NotificationsAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.GetNotificationsRequest;
import com.manilalinkup.app.models.MarkNotificationReadRequest;
import com.manilalinkup.app.models.NotificationItemModel;
import com.manilalinkup.app.models.NotificationsModel;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.NotificationUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployerNotificationsActivity extends AppCompatActivity {

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

        adapterNotif.setOnItemClickListener(notification ->
                handleNotificationClick(notification.getNotifType()));

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_notifications);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_add_job) {
                startActivity(new Intent(this, EmployerAddJobActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatEmployerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, EmployerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });
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

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getNotifications(new GetNotificationsRequest()).enqueue(new Callback<ApiResponse<List<NotificationItemModel>>>() {
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
        api.markNotificationsRead(new MarkNotificationReadRequest(true)).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {}
            @Override public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {}
        });
    }

    private void handleNotificationClick(String type) {
        if (type == null) return;
        Intent intent;
        switch (type) {
            case NotificationUtils.TYPE_NEW_APPLICANT:
                intent = new Intent(this, EmployerListOfApplicants.class);
                break;
            case NotificationUtils.TYPE_RATING_RECEIVED:
            case NotificationUtils.TYPE_VERIFIED:
                intent = new Intent(this, EmployerProfileActivity.class);
                break;
            case NotificationUtils.TYPE_VERIFICATION_REJECTED:
                intent = new Intent(this, EmployerBusinessVerificationActivity.class);
                break;
            case NotificationUtils.TYPE_JOB_EXPIRING:
            case NotificationUtils.TYPE_JOB_COMPLETED:
                intent = new Intent(this, EmployerDashboard.class);
                break;
            default:
                return;
        }
        startActivity(intent);
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
