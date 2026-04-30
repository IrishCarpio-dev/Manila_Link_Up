package com.manilalinkup.app.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.AppliedSeekerActivity;
import com.manilalinkup.app.activities.EmployerBusinessVerificationActivity;
import com.manilalinkup.app.activities.EmployerDashboard;
import com.manilalinkup.app.activities.EmployerListOfApplicants;
import com.manilalinkup.app.activities.EmployerNotificationsActivity;
import com.manilalinkup.app.activities.EmployerProfileActivity;
import com.manilalinkup.app.activities.SeekerDashboardActivity;
import com.manilalinkup.app.activities.SeekerJobPreferences;
import com.manilalinkup.app.activities.SeekerNotificationsActivity;
import com.manilalinkup.app.activities.SeekerProfileActivity;
import com.manilalinkup.app.activities.SeekerVerifyIdentityActivity;
import com.manilalinkup.app.models.RegisterDeviceRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.NotificationUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MLUFirebaseMessagingService extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "mlu_notifications";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.registerDevice(new RegisterDeviceRequest(token, "android"))
                    .enqueue(new Callback<ResponseBody>() {
                        @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}
                        @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
                    });
        });

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("fcmToken", token);
        FirebaseFirestore.getInstance()
                .collection("users").document(user.getUid())
                .set(tokenData, SetOptions.merge());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String type = data.get("type");

        String title = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getTitle() : "ManilaLinkUp";
        String body = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getBody() : "";

        int iconRes = NotificationUtils.iconForType(type);
        Intent intent = buildDeepLinkIntent(type, data);
        showNotification(title, body, iconRes, intent);
    }

    private Intent buildDeepLinkIntent(String type, Map<String, String> data) {
        if (type == null) return defaultIntent();
        String role = data.get("role");

        switch (type) {
            // --- Seeker notifications ---
            case NotificationUtils.TYPE_INTERVIEW_OFFER:
            case NotificationUtils.TYPE_HIRED:
            case NotificationUtils.TYPE_REJECTED:
            case NotificationUtils.TYPE_JOB_FILLED:
            case NotificationUtils.TYPE_JOB_COMPLETED:
                return new Intent(this, AppliedSeekerActivity.class);

            case NotificationUtils.TYPE_NEW_MATCHING_JOB:
                return new Intent(this, SeekerDashboardActivity.class);

            case NotificationUtils.TYPE_PREFERENCES_NUDGE:
                return new Intent(this, SeekerJobPreferences.class);

            case NotificationUtils.TYPE_VERIFICATION_REJECTED:
                if ("employer".equals(role)) return new Intent(this, EmployerBusinessVerificationActivity.class);
                return new Intent(this, SeekerVerifyIdentityActivity.class);

            // --- Shared notifications ---
            case NotificationUtils.TYPE_RATING_RECEIVED:
                if ("employer".equals(role)) return new Intent(this, EmployerProfileActivity.class);
                return new Intent(this, SeekerProfileActivity.class);

            case NotificationUtils.TYPE_VERIFIED:
                if ("employer".equals(role)) return new Intent(this, EmployerNotificationsActivity.class);
                return new Intent(this, SeekerNotificationsActivity.class);

            // --- Employer notifications ---
            case NotificationUtils.TYPE_NEW_APPLICANT: {
                Intent i = new Intent(this, EmployerListOfApplicants.class);
                String jobId = data.get("jobId");
                if (jobId == null) jobId = data.get("job_id");
                if (jobId != null) i.putExtra("JOB_ID", jobId);
                return i;
            }

            case NotificationUtils.TYPE_JOB_EXPIRING:
                return new Intent(this, EmployerDashboard.class);

            default:
                return defaultIntent();
        }
    }

    private Intent defaultIntent() {
        return getPackageManager().getLaunchIntentForPackage(getPackageName());
    }

    private void showNotification(String title, String body, int smallIconRes, Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "ManilaLinkUp", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, (int) System.currentTimeMillis(), intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_work)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
