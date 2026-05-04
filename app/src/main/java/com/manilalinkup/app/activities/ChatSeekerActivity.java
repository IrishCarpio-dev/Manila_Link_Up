package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.SeekerChatTabAdapter;
import com.manilalinkup.app.models.ChatsResponse;
import com.manilalinkup.app.models.ChatListItemModel;
import com.manilalinkup.app.models.GetChatsRequest;
import com.manilalinkup.app.models.HideChatRequest;
import com.manilalinkup.app.utilities.ApiService;
import com.manilalinkup.app.utilities.ErrorUtils;
import com.manilalinkup.app.utilities.SeekerNavHelper;
import com.manilalinkup.app.utilities.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSeekerActivity extends BaseActivity {

    private RecyclerView recyclerViewChat;
    private SeekerChatTabAdapter seekerChatTabAdapter;
    private List<ChatListItemModel> chatList;
    private BottomNavigationView bottomNavigationViewSeeker;
    private View emptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseFirestore db;
    private final Map<String, ListenerRegistration> chatListeners = new HashMap<>();

    private String nextCursor = null;
    private boolean hasMore = false;
    private boolean isLoadingMore = false;

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
        db = FirebaseFirestore.getInstance();

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

        recyclerViewChat.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm == null) return;
                int lastVisible = lm.findLastVisibleItemPosition();
                if (!isLoadingMore && hasMore && lastVisible >= lm.getItemCount() - 3) {
                    loadMoreChats();
                }
            }
        });

        bottomNavigationViewSeeker = findViewById(R.id.bottom_navigation_view);
        SeekerNavHelper.setup(this, bottomNavigationViewSeeker, R.id.nav_chat_seeker);
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
        attachListenersForChats(chatList);
    }

    private void attachListenersForChats(List<ChatListItemModel> chats) {
        for (ChatListItemModel chat : chats) {
            if (chatListeners.containsKey(chat.getId())) continue;
            ListenerRegistration reg = db.collection("chats").document(chat.getId())
                    .addSnapshotListener((snap, err) -> {
                        if (err != null || snap == null) return;
                        Long count = snap.getLong("unreadCountSeeker");
                        chat.setUnreadCount(count != null ? count.intValue() : 0);
                        String lastMsg = snap.getString("lastMessage");
                        Timestamp lastMsgAt = snap.getTimestamp("lastMessageAt");
                        if (lastMsg != null) chat.setLastMessage(lastMsg);
                        if (lastMsgAt != null) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.US);
                            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                            chat.setLastMessageAt(sdf.format(lastMsgAt.toDate()));
                        }
                        int idx = chatList.indexOf(chat);
                        if (idx >= 0) seekerChatTabAdapter.notifyItemChanged(idx);
                    });
            chatListeners.put(chat.getId(), reg);
        }
    }

    private void loadChats() {
        nextCursor = null;
        hasMore = false;
        isLoadingMore = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        swipeRefreshLayout.setRefreshing(true);

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getChats(new GetChatsRequest(20, null)).enqueue(new Callback<ChatsResponse>() {
                @Override
                public void onResponse(Call<ChatsResponse> call, Response<ChatsResponse> response) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (response.isSuccessful() && response.body() != null) {
                        chatList.clear();
                        List<ChatListItemModel> data = response.body().getData();
                        if (data != null) chatList.addAll(data);
                        hasMore = response.body().isHasMore();
                        nextCursor = response.body().getNextCursor();
                        seekerChatTabAdapter.notifyDataSetChanged();
                        if (emptyState != null) {
                            emptyState.setVisibility(chatList.isEmpty() ? View.VISIBLE : View.GONE);
                        }
                        attachUnreadListeners();
                    }
                }

                @Override
                public void onFailure(Call<ChatsResponse> call, Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    ErrorUtils.showThrowableError(ChatSeekerActivity.this, t);
                }
            });
        });
    }

    private void loadMoreChats() {
        if (isLoadingMore || !hasMore || nextCursor == null) return;
        isLoadingMore = true;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) { isLoadingMore = false; return; }

        user.getIdToken(false).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.getChats(new GetChatsRequest(20, nextCursor)).enqueue(new Callback<ChatsResponse>() {
                @Override
                public void onResponse(Call<ChatsResponse> call, Response<ChatsResponse> response) {
                    isLoadingMore = false;
                    if (response.isSuccessful() && response.body() != null) {
                        List<ChatListItemModel> newChats = response.body().getData();
                        if (newChats != null && !newChats.isEmpty()) {
                            int start = chatList.size();
                            chatList.addAll(newChats);
                            seekerChatTabAdapter.notifyItemRangeInserted(start, newChats.size());
                            attachListenersForChats(newChats);
                        }
                        hasMore = response.body().isHasMore();
                        nextCursor = response.body().getNextCursor();
                    }
                }

                @Override
                public void onFailure(Call<ChatsResponse> call, Throwable t) {
                    isLoadingMore = false;
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

        user.getIdToken(false).addOnSuccessListener(result -> {
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
