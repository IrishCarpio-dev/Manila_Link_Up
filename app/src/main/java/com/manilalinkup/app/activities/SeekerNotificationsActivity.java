package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.NotificationsAdapter;
import com.manilalinkup.app.models.NotificationsModel;

import java.util.ArrayList;
import java.util.List;

public class SeekerNotificationsActivity extends AppCompatActivity {
    private RecyclerView recyclerViewNotifications;
    private NotificationsAdapter adapterNotif;
    private List<NotificationsModel> notifListCard;
    private BottomNavigationView bottomNavigationViewSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_notifications);

        recyclerViewNotifications = findViewById(R.id.recycler_view_seeker_notif);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notifListCard = new ArrayList<>();
        mockNotifDta();

        adapterNotif = new NotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_notifications_seeker);
        bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem ->  {
            if(menuItem.getItemId() == R.id.nav_home_seeker){
                startActivity(new Intent(SeekerNotificationsActivity.this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_activity_seeker){
                startActivity(new Intent(SeekerNotificationsActivity.this, AppliedSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat_seeker) {
                startActivity(new Intent(SeekerNotificationsActivity.this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_profile_seeker) {
                startActivity(new Intent(SeekerNotificationsActivity.this, SeekerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });


    }

    private void mockNotifDta() {
        // 1. Applicant Notification
        notifListCard.add(new NotificationsModel(
                R.drawable.people_notif_icon,
                "Employer Notification",
                "Juan Dela Cruz accepted your application.",
                "2 mins ago"
        ));

        // 2. Message/Chat Notification
        notifListCard.add(new NotificationsModel(
                R.drawable.chat_notif_icon,
                "Message from Justine",
                "\"Are you available for an one on one call?\"",
                "1 hour ago"
        ));

        // 3. Reminder/System Notification
        notifListCard.add(new NotificationsModel(
                R.drawable.schedule_notif_icontwo,
                "Urgent: Complete Profile",
                "Add your valid id to verify your account.",
                "3 hours ago"
        ));

        // 4. Job Post Update
        notifListCard.add(new NotificationsModel(
                R.drawable.people_notif_icon,
                "job Application",
                "Mark Santos made you as an applicant",
                "Yesterday"
        ));
    }
}