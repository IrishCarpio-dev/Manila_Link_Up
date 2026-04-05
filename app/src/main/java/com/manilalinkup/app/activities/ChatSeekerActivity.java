package com.manilalinkup.app.activities;

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
    BottomNavigationView bottomNavigationViewEmployer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_seeker);

        recyclerViewChat = findViewById(R.id.recycler_view_seeker_chat_tab);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        seekerChatModelList = new ArrayList<>();

        seekerChatTabAdapter = new SeekerChatTabAdapter(seekerChatModelList);
        recyclerViewChat.setAdapter(seekerChatTabAdapter);


    }
}