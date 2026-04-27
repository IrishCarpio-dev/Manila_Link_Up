package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.EmployerChatTabAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ChatListItemModel;
import com.manilalinkup.app.models.GetChatsRequest;
import com.manilalinkup.app.models.HideChatRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatEmployerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private EmployerChatTabAdapter employerChatTabAdapter;
    private List<ChatListItemModel> chatList;
    private BottomNavigationView bottomNavigationViewEmployer;
    private View emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db;
    private final Map<String, ListenerRegistration> chatListeners = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_employer);

        recyclerViewChat = findViewById(R.id.recycler_view_seeker_chat_tab);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        emptyState = findViewById(R.id.empty_state_chats);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadChats);

        chatList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        employerChatTabAdapter = new EmployerChatTabAdapter(
                chatList,
                chat -> {
                    Intent intent = new Intent(ChatEmployerActivity.this, ChatThreadEmployer.class);
                    intent.putExtra("CHAT_ID", chat.getId());
                    intent.putExtra("JOB_TITLE", chat.getJob() != null ? chat.getJob().getTitle() : "");
                    intent.putExtra("COUNTERPART_NAME", chat.getCounterpart() != null ? chat.getCounterpart().getName() : "");
                    intent.putExtra("SEEKER_UID", chat.getSeekerUid());
                    intent.putExtra("EMPLOYER_UID", chat.getEmployerUid());
                    intent.putExtra("APPLICATION_ID", chat.getApplicationId());
                    if (chat.getApplicationStatus() != null) {
                        intent.putExtra("APPLICATION_STATUS", chat.getApplicationStatus());
                    }
                    startActivity(intent);
                },
                chat -> showHideDialog(chat)
        );
        recyclerViewChat.setAdapter(employerChatTabAdapter);

        bottomNavigationViewEmployer = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewEmployer.setSelectedItemId(R.id.nav_chat);
        bottomNavigationViewEmployer.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, EmployerDashboard.class));
                overridePendingTransition(0, 0);
            } else if (menuItem.getItemId() == R.id.nav_notifications) {
                startActivity(new Intent(this, EmployerNotificationsActivity.class));
                overridePendingTransition(0, 0);
            } else if (menuItem.getItemId() == R.id.nav_add_job) {
                startActivity(new Intent(this, EmployerAddJobActivity.class));
                overridePendingTransition(0, 0);
            } else if (menuItem.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(this, EmployerProfileActivity.class));
                overridePendingTransition(0, 0);
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChats();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (ListenerRegistration reg : chatListeners.values()) reg.remove();
        chatListeners.clear();
    }

    private void attachUnreadListeners() {
        for (ListenerRegistration reg : chatListeners.values()) reg.remove();
        chatListeners.clear();
        for (ChatListItemModel chat : chatList) {
            ListenerRegistration reg = db.collection("chats").document(chat.getId())
                    .addSnapshotListener((snap, err) -> {
                        if (err != null || snap == null) return;
                        Long count = snap.getLong("unreadCountEmployer");
                        chat.setUnreadCount(count != null ? count.intValue() : 0);
                        int idx = chatList.indexOf(chat);
                        if (idx >= 0) employerChatTabAdapter.notifyItemChanged(idx);
                    });
            chatListeners.put(chat.getId(), reg);
        }
    }

    private void loadChats() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getChats(new GetChatsRequest(20, null)).enqueue(new Callback<ApiResponse<List<ChatListItemModel>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<ChatListItemModel>>> call, Response<ApiResponse<List<ChatListItemModel>>> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null) {
                        chatList.clear();
                        chatList.addAll(response.body().getData());
                        employerChatTabAdapter.notifyDataSetChanged();
                        if (emptyState != null) {
                            emptyState.setVisibility(chatList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                        attachUnreadListeners();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ChatListItemModel>>> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    ErrorUtils.showThrowableError(ChatEmployerActivity.this, t);
                }
            });
        });
    }

    private void showHideDialog(ChatListItemModel chat) {
        new AlertDialog.Builder(this)
                .setTitle("Hide chat")
                .setMessage("Hide this conversation? It will reappear if you receive a new message.")
                .setPositiveButton("Hide", (d, w) -> hideChat(chat))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void hideChat(ChatListItemModel chat) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.hideChat(new HideChatRequest(chat.getId())).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        chatList.remove(chat);
                        employerChatTabAdapter.notifyDataSetChanged();
                        Toast.makeText(ChatEmployerActivity.this, "Chat hidden", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ErrorUtils.showThrowableError(ChatEmployerActivity.this, t);
                }
            });
        });
    }
}
