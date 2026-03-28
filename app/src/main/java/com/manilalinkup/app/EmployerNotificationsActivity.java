package com.manilalinkup.app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class EmployerNotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotifications;
    private EmployerNotificationsAdapter adapterNotif;
    private List<EmployerNotificationsModel> notifListCard;
    BottomNavigationView bottomNavigationViewEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_employer_notifications);

        recyclerViewNotifications = findViewById(R.id.recycler_view_employer_own_posts);
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));

        notifListCard = new ArrayList<>();

        adapterNotif = new EmployerNotificationsAdapter(notifListCard);
        recyclerViewNotifications.setAdapter(adapterNotif);

        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_notifications);


    }
}