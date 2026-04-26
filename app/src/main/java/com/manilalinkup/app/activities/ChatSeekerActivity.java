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
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.SeekerChatTabAdapter;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ChatListItemModel;
import com.manilalinkup.app.models.GetChatsRequest;
import com.manilalinkup.app.models.HideChatRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSeekerActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChat;
    private SeekerChatTabAdapter seekerChatTabAdapter;
    private List<ChatListItemModel> chatList;
    private BottomNavigationView bottomNavigationViewSeeker;
    private View emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_seeker);

        recyclerViewChat = findViewById(R.id.recycler_view_seeker_chat_tab);
        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this));
        emptyState = findViewById(R.id.empty_state_chats);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::loadChats);

        chatList = new ArrayList<>();

        seekerChatTabAdapter = new SeekerChatTabAdapter(
                chatList,
                chat -> {
                    Intent intent = new Intent(ChatSeekerActivity.this, ChatThreadSeeker.class);
                    intent.putExtra("CHAT_ID", chat.getId());
                    intent.putExtra("JOB_TITLE", chat.getJob() != null ? chat.getJob().getTitle() : "");
                    intent.putExtra("COUNTERPART_NAME", chat.getCounterpart() != null ? chat.getCounterpart().getName() : "");
                    intent.putExtra("SEEKER_UID", chat.getSeekerUid());
                    intent.putExtra("EMPLOYER_UID", chat.getEmployerUid());
                    startActivity(intent);
                },
                chat -> showHideDialog(chat)
        );
        recyclerViewChat.setAdapter(seekerChatTabAdapter);

        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view);
        bottomNavigationViewSeeker.setSelectedItemId(R.id.nav_chat_seeker);
        bottomNavigationViewSeeker.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_home_seeker) {
                startActivity(new Intent(this, SeekerDashboardActivity.class));
                overridePendingTransition(0, 0);
            } else if (menuItem.getItemId() == R.id.nav_notifications_seeker) {
                startActivity(new Intent(this, SeekerNotificationsActivity.class));
                overridePendingTransition(0, 0);
            } else if (menuItem.getItemId() == R.id.nav_activity_seeker) {
                startActivity(new Intent(this, AppliedSeekerActivity.class));
                overridePendingTransition(0, 0);
            } else if (menuItem.getItemId() == R.id.nav_profile_seeker) {
                startActivity(new Intent(this, SeekerProfileActivity.class));
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
                        seekerChatTabAdapter.notifyDataSetChanged();
                        if (emptyState != null) {
                            emptyState.setVisibility(chatList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<ChatListItemModel>>> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    ErrorUtils.showThrowableError(ChatSeekerActivity.this, t);
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
                        seekerChatTabAdapter.notifyDataSetChanged();
                        Toast.makeText(ChatSeekerActivity.this, "Chat hidden", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ErrorUtils.showThrowableError(ChatSeekerActivity.this, t);
                }
            });
        });
    }
}
