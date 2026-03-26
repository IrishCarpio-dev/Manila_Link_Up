package com.manilalinkup.app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ChatEmployerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EmployerChatTabAdapter employerChatTabAdapter;
    private List<EmployerChatModel> employerChatModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_employer);

        recyclerViewChat = findViewById(R.id.recycler_view_seeker_chat_tab);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));

        employerChatModelList = new ArrayList<>();

        employerChatTabAdapter = new EmployerChatTabAdapter(employerChatModelList);
        recyclerViewChat.setAdapter(employerChatTabAdapter);
    }
}