package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.ChatAdapter;
import com.manilalinkup.app.models.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class ChatThreadSeeker extends AppCompatActivity {
    MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatModel> chatList;
    private String seekerUid = "seeker_123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_thread_seeker);

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
        chatList.add(new ChatModel("Mag aaply po ako as baby sitter, san po ba kayo banda?", "seeker_456", System.currentTimeMillis()));
        chatList.add(new ChatModel("Yes you can visit us sa Matcha Mansion around whatever corner for the initial screening.", seekerUid, System.currentTimeMillis()));
        chatList.add(new ChatModel("Okay po free po ng 2PM today daan n lng po, salamat po.", "seeker_456", System.currentTimeMillis()));

        adapter = new ChatAdapter(chatList, seekerUid);
        recyclerView.setAdapter(adapter);

        EditText messageInput = findViewById(R.id.edit_chat_message);
        ImageButton btnSend = findViewById(R.id.btn_send_message);

        btnSend.setOnClickListener(v -> {
            String messageText = messageInput.getText().toString().trim();
            if (!messageText.isEmpty()) {
                ChatModel newMessage = new ChatModel(messageText, seekerUid, System.currentTimeMillis());

                chatList.add(newMessage);

                adapter.notifyItemInserted(chatList.size() - 1);

                recyclerView.scrollToPosition(chatList.size() - 1);

                messageInput.setText("");
            }
        });

    }
}