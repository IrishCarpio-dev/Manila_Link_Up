package com.manilalinkup.app.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.SeekerChatModel;
import com.manilalinkup.app.adapters.SeekerChatTabAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatSeekerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private SeekerChatTabAdapter seekerChatTabAdapter;
    private List<SeekerChatModel> seekerChatModelList;
    BottomNavigationView bottomNavigationViewSeeker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_seeker);

        recyclerViewChat = findViewById(R.id.recycler_view_seeker_chat_tab);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        seekerChatModelList = new ArrayList<>();
        setupMockData();


        seekerChatTabAdapter = new SeekerChatTabAdapter(seekerChatModelList);
        recyclerViewChat.setAdapter(seekerChatTabAdapter);

        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_chat_seeker);
        bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem ->  {

            if(menuItem.getItemId() == R.id.nav_home_seeker){
                startActivity(new Intent(ChatSeekerActivity.this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_notifications_seeker) {
                //pending notif view
                startActivity(new Intent(ChatSeekerActivity.this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_activity_seeker) {
                startActivity(new Intent(ChatSeekerActivity.this, SaveSeekerActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }else if(menuItem.getItemId() == R.id.nav_profile_seeker) {
                startActivity(new Intent(ChatSeekerActivity.this, SeekerProfileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return true;
        });
    }

    private void setupMockData() {
        seekerChatModelList.add(new SeekerChatModel(
                android.R.drawable.ic_menu_camera,
                "Don Kopi Malate",
                "When can you start for the Barista trial?",
                "10:30 AM"
        ));

        seekerChatModelList.add(new SeekerChatModel(
                android.R.drawable.ic_menu_myplaces,
                "LBC Express Pasay",
                "Your requirements have been verified.",
                "Yesterday"
        ));

        seekerChatModelList.add(new SeekerChatModel(
                android.R.drawable.ic_menu_send,
                "Creative Manila",
                "We liked your portfolio! Check your email.",
                "Apr 08"
        ));

        seekerChatModelList.add(new SeekerChatModel(
                android.R.drawable.ic_lock_idle_lock,
                "Eng Bee Tin Binondo",
                "Thank you for applying for the Store Assistant role.",
                "2 days ago"
        ));
    }
}