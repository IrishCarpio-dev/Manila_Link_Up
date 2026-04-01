package com.manilalinkup.app;

import android.content.Intent;
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

public class ChatEmployerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EmployerChatTabAdapter employerChatTabAdapter;
    private List<EmployerChatModel> employerChatModelList;
    BottomNavigationView bottomNavigationViewEmployer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_employer);

        recyclerViewChat = findViewById(R.id.recycler_view_seeker_chat_tab);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        employerChatModelList = new ArrayList<>();
        mockChatData();

        employerChatTabAdapter = new EmployerChatTabAdapter(employerChatModelList);
        recyclerViewChat.setAdapter(employerChatTabAdapter);


        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_chat);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem ->  {

            if(menuItem.getItemId() == R.id.nav_home){
                startActivity(new Intent(ChatEmployerActivity.this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications) {
                startActivity(new Intent(ChatEmployerActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_add_job) {
                startActivity(new Intent(ChatEmployerActivity.this, EmployerAddJobActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(ChatEmployerActivity.this, EmployerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }

            return true;
        });
    }

    private void mockChatData() {
        // 1. A new message from an applicant
        employerChatModelList.add(new EmployerChatModel(
                R.drawable.frieren, // Replace with your actual drawable
                "Frieren Chan",
                "Hello! Is the Barista position still open?",
                "10:45 AM"
        ));

        // 2. A follow-up message
        employerChatModelList.add(new EmployerChatModel(
                R.drawable.seeker_prof_mock1,
                "Fern Frieren",
                "I have sent my resume to your email. Thank you!",
                "Yesterday"
        ));

        // 3. An older conversation
        employerChatModelList.add(new EmployerChatModel(
                R.drawable.profpic_mock2,
                "Stark Rizal",
                "When can I start the orientation?",
                "Mar 25"
        ));

        // 4. Another inquiry
        employerChatModelList.add(new EmployerChatModel(
                R.drawable.profpicmock3,
                "Himmel Bonifacio",
                "Is the salary paid weekly or monthly?",
                "Mar 24"
        ));

    }
}