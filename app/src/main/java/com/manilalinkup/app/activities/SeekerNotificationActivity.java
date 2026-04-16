package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.adapters.EmployerNotificationsAdapter;
import com.manilalinkup.app.models.EmployerNotificationsModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class SeekerNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private EmployerNotificationsAdapter adapterNotif;
    private List<EmployerNotificationsModel> notifListCard;
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

        adapterNotif = new EmployerNotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        // 3. Setup Bottom Navigation
        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view_seeker);

        // Force the Seeker Menu to ensure it doesn't default to Employer
        bottomNavigationViewSeeker.getMenu().clear();
        bottomNavigationViewSeeker.inflateMenu(R.menu.bottom_nav_menu_icons);

        // Highlights the 2nd icon (Notifications)
        bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_notifications_seeker);

        bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

            // All navigation stays within the Seeker Activity flow
            if (itemId == R.id.nav_home_seeker) {
                startActivity(new Intent(this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_notifications_seeker) {
                return true; // Already on this screen
            } else if (itemId == R.id.nav_activity_seeker) {
                // The middle button (My Activity)
                // If you don't have a separate Activity for this yet, keep it on Dashboard or current
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

    private void mockNotifDta() {
        notifListCard.add(new EmployerNotificationsModel(
                R.drawable.people_notif_icon,
                "Application Update",
                "Your application for 'Barista' has been viewed.",
                "2 mins ago"
        ));

        notifListCard.add(new EmployerNotificationsModel(
                R.drawable.chat_notif_icon,
                "New Message",
                "Starbucks Manila sent you an interview invite.",
                "1 hour ago"
        ));
    }
}