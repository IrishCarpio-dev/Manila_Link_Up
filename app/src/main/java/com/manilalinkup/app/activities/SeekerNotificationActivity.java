package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.adapters.SeekerNotificationsAdapter;
import com.manilalinkup.app.models.SeekerNotificationModel;
import com.manilalinkup.app.R;
import java.util.ArrayList;
import java.util.List;

public class SeekerNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private SeekerNotificationsAdapter adapterNotif;
    private List<SeekerNotificationModel> notifListCard;
    private BottomNavigationView bottomNavigationViewSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_notification);

        recyclerViewNotifications = findViewById(R.id.recycler_view_seeker_notifications);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notifListCard = new ArrayList<>();
        mockNotifDta();

        // 2. Adapter Setup
        adapterNotif = new SeekerNotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        // 3. Bottom Navigation Setup
        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view_seeker);

        if (bottomNavigationViewSeeker != null) {
            bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_notifications_seeker);
            bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem -> {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.nav_home_seeker) {
                    startActivity(new Intent(this, SeekerDashboardActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_notifications_seeker) {
                    return true;
                } else if (itemId == R.id.nav_activity_seeker) {
                    startActivity(new Intent(this, SeekerDashboardActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_chat_seeker) {
                    startActivity(new Intent(this, ChatSeekerActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                } else if (itemId == R.id.nav_profile_seeker) {
                    startActivity(new Intent(this, SeekerProfileActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;
            });
        }
    }

    private void mockNotifDta() {
        notifListCard.add(new SeekerNotificationModel(R.drawable.people_notif_icon, "Application Update", "Your application for 'Barista' has been viewed.", "2 mins ago"));
        notifListCard.add(new SeekerNotificationModel(R.drawable.chat_notif_icon, "New Message", "You have a new interview invite.", "1 hour ago"));
        notifListCard.add(new SeekerNotificationModel(R.drawable.schedule_notif_icontwo, "Interview Reminder", "Don't forget your interview tomorrow at 2 PM.", "3 hours ago"));
    }
}