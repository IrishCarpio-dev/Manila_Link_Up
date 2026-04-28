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

public class SeekerNotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private NotificationsAdapter adapterNotif;
    private List<NotificationsModel> notifListCard;
    private ProgressBar progressBar;
    private TextView textViewEmpty;
    private BottomNavigationView bottomNavigationViewSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_notifications);

        progressBar              = findViewById(R.id.progress_bar_notif);
        textViewEmpty            = findViewById(R.id.text_view_notif_empty);
        recyclerViewNotifications = findViewById(R.id.recycler_view_seeker_notif);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notifListCard = new ArrayList<>();
        adapterNotif  = new NotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        adapterNotif.setOnItemClickListener(notification ->
                handleNotificationClick(notification.getNotifType()));

        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_notifications_seeker);
        bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home_seeker) {
                startActivity(new Intent(this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_activity_seeker) {
                startActivity(new Intent(this, AppliedSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_chat_seeker) {
                startActivity(new Intent(this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile_seeker) {
                startActivity(new Intent(this, SeekerProfileActivity.class));
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
            api.getNotifications().enqueue(new Callback<ApiResponse<List<NotificationItemModel>>>() {
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
                    ErrorUtils.showThrowableError(SeekerNotificationsActivity.this, t);
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

    private void handleNotificationClick(String type) {
        if (type == null) return;
        Intent intent;
        switch (type) {
            case NotificationUtils.TYPE_CHAT_MESSAGE:
                intent = new Intent(this, ChatSeekerActivity.class);
                break;
            case NotificationUtils.TYPE_APPLICATION_REVIEW:
            case NotificationUtils.TYPE_INTERVIEW_SCHEDULED:
            case NotificationUtils.TYPE_HIRED:
            case NotificationUtils.TYPE_APPLICATION_REJECTED:
            case NotificationUtils.TYPE_JOB_CLOSED:
                intent = new Intent(this, AppliedSeekerActivity.class);
                break;
            case NotificationUtils.TYPE_RATING_RECEIVED:
            case NotificationUtils.TYPE_ID_APPROVED:
                intent = new Intent(this, SeekerProfileActivity.class);
                break;
            case NotificationUtils.TYPE_PROFILE_INCOMPLETE:
                intent = new Intent(this, EditSeekerProfileActivity.class);
                break;
            case NotificationUtils.TYPE_PREFERENCES_INCOMPLETE:
                intent = new Intent(this, SeekerJobPreferences.class);
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
