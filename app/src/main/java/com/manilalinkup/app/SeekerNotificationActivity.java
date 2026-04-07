package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SeekerNotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EmployerNotificationsAdapter adapter;
    private List<EmployerNotificationsModel> notificationList;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seeker_notification);

        recyclerView = findViewById(R.id.recycler_view_seeker_notifications);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationList = new ArrayList<>();
        loadNotifications();

        adapter = new EmployerNotificationsAdapter(notificationList);
        recyclerView.setAdapter(adapter);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);

        
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_my_activity) {
                startActivity(new Intent(this, SaveSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, SeekerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void loadNotifications() {
        notificationList.add(new EmployerNotificationsModel(
                R.drawable.people_notif_icon,
                "Application Update",
                "Your application for 'Events/Catering Helper' has been viewed.",
                "10 mins ago"
        ));
        notificationList.add(new EmployerNotificationsModel(
                R.drawable.chat_notif_icon,
                "New Message",
                "Don Kopi sent you a message regarding your application.",
                "2 hours ago"
        ));
        notificationList.add(new EmployerNotificationsModel(
                R.drawable.schedule_notif_icontwo,
                "Interview Scheduled",
                "You have an interview tomorrow at 10:00 AM.",
                "5 hours ago"
        ));
    }
}