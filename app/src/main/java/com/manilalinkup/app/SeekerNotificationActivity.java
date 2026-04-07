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

        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view_seeker);
        bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_notifications);
        bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem ->  {
            int id = menuItem.getItemId();
            if(id == R.id.nav_home){
                startActivity(new Intent(SeekerNotificationActivity.this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if(id == R.id.nav_chat) {
                startActivity(new Intent(SeekerNotificationActivity.this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if(id == R.id.nav_profile) {
                startActivity(new Intent(SeekerNotificationActivity.this, SeekerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if(id == R.id.nav_activity) {
                // Placeholder for Seeker Activity
                return true;
            }
            return true;
        });


    }

    private void mockNotifDta() {
        // 1. Applicant Notification
        notifListCard.add(new EmployerNotificationsModel(
                R.drawable.people_notif_icon,
                "New Applicant: Service Crew",
                "Juan Dela Cruz applied for your Binondo branch.",
                "2 mins ago"
        ));

        // 2. Message/Chat Notification
        notifListCard.add(new EmployerNotificationsModel(
                R.drawable.chat_notif_icon,
                "Inquiry from Maria",
                "\"Is the Barista position still available?\"",
                "1 hour ago"
        ));

        // 3. Reminder/System Notification
        notifListCard.add(new EmployerNotificationsModel(
                R.drawable.schedule_notif_icontwo,
                "Urgent: Complete Profile",
                "Add your business permit to verify your account.",
                "3 hours ago"
        ));

        // 4. Job Post Update
        notifListCard.add(new EmployerNotificationsModel(
                R.drawable.people_notif_icon,
                "New Applicant: Delivery Rider",
                "Mark Santos submitted his resume for Malate.",
                "Yesterday"
        ));

    }
}