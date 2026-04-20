package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.adapters.NotificationsAdapter;
import com.manilalinkup.app.models.NotificationsModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class EmployerNotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private NotificationsAdapter adapterNotif;
    private List<NotificationsModel> notifListCard;
    BottomNavigationView bottomNavigationViewEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_notifications);

        recyclerViewNotifications = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notifListCard = new ArrayList<>();
        mockNotifDta();

        adapterNotif = new NotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_notifications);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem ->  {
            if(menuItem.getItemId() == R.id.nav_home){
                startActivity(new Intent(EmployerNotificationsActivity.this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_add_job){
                startActivity(new Intent(EmployerNotificationsActivity.this, EmployerAddJobActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_chat) {
                startActivity(new Intent(EmployerNotificationsActivity.this, ChatEmployerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(EmployerNotificationsActivity.this, EmployerProfileActivity.class));
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
                "New Applicant: Service Crew",
                "Juan Dela Cruz applied for your Binondo branch.",
                "2 mins ago"
        ));

        // 2. Message/Chat Notification
        notifListCard.add(new NotificationsModel(
                R.drawable.chat_notif_icon,
                "Inquiry from Maria",
                "\"Is the Barista position still available?\"",
                "1 hour ago"
        ));

        // 3. Reminder/System Notification
        notifListCard.add(new NotificationsModel(
                R.drawable.schedule_notif_icontwo,
                "Urgent: Complete Profile",
                "Add your business permit to verify your account.",
                "3 hours ago"
        ));

        // 4. Job Post Update
        notifListCard.add(new NotificationsModel(
                R.drawable.people_notif_icon,
                "New Applicant: Delivery Rider",
                "Mark Santos submitted his resume for Malate.",
                "Yesterday"
        ));

    }
}