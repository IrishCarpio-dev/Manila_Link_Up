package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.ChatAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ChatMessageModel;
import com.manilalinkup.app.models.ChatModel;
import com.manilalinkup.app.models.GetMessagesRequest;
import com.manilalinkup.app.models.MarkReadRequest;
import com.manilalinkup.app.models.SendMessageRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatThreadSeeker extends AppCompatActivity {

    private static final long POLL_INTERVAL_MS = 3000;

    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatModel> chatList;
    private EditText messageInput;
    private ImageButton btnSend;

    private String chatId;
    private String currentUid;
    private String lastMessageAt;
    private final Handler pollHandler = new Handler(Looper.getMainLooper());
    private Runnable pollRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_thread_seeker);

        chatId = getIntent().getStringExtra("CHAT_ID");
        String jobTitle = getIntent().getStringExtra("JOB_TITLE");
        String counterpartName = getIntent().getStringExtra("COUNTERPART_NAME");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (jobTitle != null && !jobTitle.isEmpty()) {
            toolbar.setTitle(jobTitle);
            toolbar.setSubtitle(counterpartName);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        recyclerView = findViewById(R.id.recycler_chat_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        chatList = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUid = user != null ? user.getUid() : "";

        adapter = new ChatAdapter(chatList, currentUid);
        recyclerView.setAdapter(adapter);

        messageInput = findViewById(R.id.edit_chat_message);
        btnSend = findViewById(R.id.btn_send_message);

        btnSend.setOnClickListener(v -> sendMessage());

        pollRunnable = new Runnable() {
            @Override
            public void run() {
                pollMessages();
                pollHandler.postDelayed(this, POLL_INTERVAL_MS);
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInitialMessages();
        markRead();
    }

    @Override
    protected void onPause() {
        super.onPause();
        pollHandler.removeCallbacks(pollRunnable);
    }

    private void loadInitialMessages() {
        if (chatId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getMessages(new GetMessagesRequest(chatId, 30, null))
                    .enqueue(new Callback<ApiResponse<List<ChatMessageModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ChatMessageModel>>> call, Response<ApiResponse<List<ChatMessageModel>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ChatMessageModel> messages = response.body().getData();
                        // API returns DESC; reverse for display
                        Collections.reverse(messages);
                        chatList.clear();
                        for (ChatMessageModel m : messages) {
                            chatList.add(toDisplayModel(m));
                        }
                        if (!chatList.isEmpty()) {
                            lastMessageAt = messages.get(0).getCreatedAt();
                        }
                        adapter.notifyDataSetChanged();
                        if (!chatList.isEmpty()) recyclerView.scrollToPosition(chatList.size() - 1);
                        pollHandler.postDelayed(pollRunnable, POLL_INTERVAL_MS);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ChatMessageModel>>> call, Throwable t) {}
            });
        });
    }

    private void pollMessages() {
        if (chatId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getMessages(new GetMessagesRequest(chatId, 30, lastMessageAt))
                    .enqueue(new Callback<ApiResponse<List<ChatMessageModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ChatMessageModel>>> call, Response<ApiResponse<List<ChatMessageModel>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ChatMessageModel> newMsgs = response.body().getData();
                        if (!newMsgs.isEmpty()) {
                            Collections.reverse(newMsgs);
                            int insertAt = chatList.size();
                            for (ChatMessageModel m : newMsgs) {
                                chatList.add(toDisplayModel(m));
                            }
                            lastMessageAt = newMsgs.get(0).getCreatedAt();
                            adapter.notifyItemRangeInserted(insertAt, newMsgs.size());
                            recyclerView.scrollToPosition(chatList.size() - 1);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ChatMessageModel>>> call, Throwable t) {}
            });
        });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty() || chatId == null) return;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        btnSend.setEnabled(false);
        messageInput.setText("");

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.sendMessage(new SendMessageRequest(chatId, text))
                    .enqueue(new Callback<ApiResponse<ChatMessageModel>>() {
                @Override
                public void onResponse(Call<ApiResponse<ChatMessageModel>> call, Response<ApiResponse<ChatMessageModel>> response) {
                    btnSend.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        ChatModel local = new ChatModel(text, currentUid, System.currentTimeMillis());
                        chatList.add(local);
                        adapter.notifyItemInserted(chatList.size() - 1);
                        recyclerView.scrollToPosition(chatList.size() - 1);
                    } else {
                        Toast.makeText(ChatThreadSeeker.this, "Failed to send", Toast.LENGTH_SHORT).show();
                        messageInput.setText(text);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<ChatMessageModel>> call, Throwable t) {
                    btnSend.setEnabled(true);
                    Toast.makeText(ChatThreadSeeker.this, "Network error", Toast.LENGTH_SHORT).show();
                    messageInput.setText(text);
                }
            });
        });
    }

    private void markRead() {
        if (chatId == null) return;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.markChatRead(new MarkReadRequest(chatId)).enqueue(new Callback<ResponseBody>() {
                @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}
                @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        });
    }

    private ChatModel toDisplayModel(ChatMessageModel m) {
        long ts = 0;
        if (m.getCreatedAt() != null) {
            try { ts = Long.parseLong(m.getCreatedAt()); } catch (NumberFormatException ignored) {}
        }
        ChatModel cm = new ChatModel(m.getText(), m.getSenderUid(), ts);
        cm.setId(m.getId());
        cm.setReadAt(m.getReadAt());
        return cm;
    }
}
