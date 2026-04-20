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
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.ChatThreadEmployer;
import com.manilalinkup.app.activities.ChatThreadSeeker;
import com.manilalinkup.app.models.RegisterDeviceRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.RetrofitClient;

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
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String, String> data = remoteMessage.getData();
        String type = data.get("type");

        String title = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getTitle() : "ManilaLinkUp";
        String body  = remoteMessage.getNotification() != null
                ? remoteMessage.getNotification().getBody() : "";

        Intent intent = buildDeepLinkIntent(type, data);
        showNotification(title, body, intent);
    }

    private Intent buildDeepLinkIntent(String type, Map<String, String> data) {
        if ("chat_message".equals(type)) {
            String chatId = data.get("chatId");
            // Determine role — try seeker first; employer fallback handled at activity level
            Intent intent = new Intent(this, ChatThreadSeeker.class);
            intent.putExtra("CHAT_ID", chatId);
            return intent;
        }
        // For status_change and rating_received, go to main activity; deep-linking can be added later
        return getPackageManager().getLaunchIntentForPackage(getPackageName());
    }

    private void showNotification(String title, String body, Intent intent) {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (manager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "ManilaLinkUp", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_work)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
