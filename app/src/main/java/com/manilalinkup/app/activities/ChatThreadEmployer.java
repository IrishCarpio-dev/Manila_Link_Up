package com.manilalinkup.app.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.manilalinkup.app.adapters.ChatAdapter;
import com.manilalinkup.app.models.ChatModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class ChatThreadEmployer extends AppCompatActivity {
    MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatModel> chatList;
    private String employerUid = "employer_123"; // Dummy ID for testing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_thread_employer);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        recyclerView = findViewById(R.id.recycler_chat_messages);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        chatList = new ArrayList<>();

        // Message from the Seeker (Received - Left)
        chatList.add(new ChatModel("Hi! Ano po requirements for your babysitting job post po?", "seeker_456", System.currentTimeMillis()));

        // Message from the Employer (Sent - Right)
        chatList.add(new ChatModel("Yes you can visit us sa Matcha Mansion around whatever corner for the initial screening.", employerUid, System.currentTimeMillis()));

        // Another message from the Seeker (Received - Left)
        chatList.add(new ChatModel("Okay po free po ng 2PM today daan n lng po, salamat po.", "seeker_456", System.currentTimeMillis()));

        adapter = new ChatAdapter(chatList, employerUid);
        recyclerView.setAdapter(adapter);


    }
}