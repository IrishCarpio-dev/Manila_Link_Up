package com.manilalinkup.app.activities;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.manilalinkup.app.R;
import com.manilalinkup.app.adapters.ChatAdapter;
import com.manilalinkup.app.models.ChatModel;
import com.manilalinkup.app.models.NotifyChatRequest;
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

public class ChatThreadSeeker extends BaseActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatModel> chatList;
    private EditText messageInput;
    private ImageButton btnSend;

    private String chatId;
    private String jobTitle;
    private String currentUid;

    private FirebaseFirestore db;
    private CollectionReference messagesRef;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_thread_seeker);

        chatId = getIntent().getStringExtra("CHAT_ID");
        jobTitle = getIntent().getStringExtra("JOB_TITLE");
        String counterpartName = getIntent().getStringExtra("COUNTERPART_NAME");
        String seekerUid = getIntent().getStringExtra("SEEKER_UID");
        String employerUid = getIntent().getStringExtra("EMPLOYER_UID");

        setupToolbar(R.id.toolbar);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(counterpartName != null ? counterpartName : "");
        if (jobTitle != null && !jobTitle.isEmpty()) toolbar.setSubtitle(jobTitle);

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

        db = FirebaseFirestore.getInstance();
        if (chatId != null) {
            messagesRef = db.collection("chats").document(chatId).collection("messages");
            initChatDocument(seekerUid, employerUid, jobTitle);
        }
    }

    private void initChatDocument(String seekerUid, String employerUid, String jobTitle) {
        if (seekerUid == null || employerUid == null) return;
        Map<String, Object> meta = new HashMap<>();
        meta.put("seekerUid", seekerUid);
        meta.put("employerUid", employerUid);
        if (jobTitle != null) meta.put("jobTitle", jobTitle);
        db.collection("chats").document(chatId).set(meta, SetOptions.merge());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startListening();
        markRead();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (messagesListener != null) {
            messagesListener.remove();
            messagesListener = null;
        }
    }

    private void startListening() {
        if (messagesRef == null) return;
        messagesListener = messagesRef
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) return;
                    chatList.clear();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        chatList.add(docToModel(doc));
                    }
                    adapter.notifyDataSetChanged();
                    if (!chatList.isEmpty()) recyclerView.post(() -> recyclerView.scrollToPosition(chatList.size() - 1));
                });
    }

    private void sendMessage() {
        String text = messageInput.getText().toString().trim();
        if (text.isEmpty() || messagesRef == null) return;

        btnSend.setEnabled(false);
        messageInput.setText("");
        if (!chatList.isEmpty()) recyclerView.post(() -> recyclerView.scrollToPosition(chatList.size() - 1));

        Map<String, Object> msg = new HashMap<>();
        msg.put("senderUid", currentUid);
        msg.put("text", text);
        msg.put("createdAt", FieldValue.serverTimestamp());
        msg.put("readAt", null);

        messagesRef.add(msg)
                .addOnSuccessListener(ref -> {
                    btnSend.setEnabled(true);
                    Map<String, Object> chatUpdate = new HashMap<>();
                    chatUpdate.put("unreadCountEmployer", FieldValue.increment(1));
                    chatUpdate.put("lastMessage", text);
                    chatUpdate.put("lastMessageAt", FieldValue.serverTimestamp());
                    db.collection("chats").document(chatId).set(chatUpdate, SetOptions.merge());
                    notifyRecipient(text);
                })
                .addOnFailureListener(e -> {
                    btnSend.setEnabled(true);
                    messageInput.setText(text);
                    ErrorUtils.showThrowableError(this, e);
                });
    }

    private void notifyRecipient(String text) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        user.getIdToken(false).addOnSuccessListener(result -> {
            Map<String, String> data = new HashMap<>();
            data.put("type", "chat_message");
            data.put("chatId", chatId);
            data.put("role", "employer");
            String title = (jobTitle != null && !jobTitle.isEmpty()) ? jobTitle : "New Message";
            NotifyChatRequest request = new NotifyChatRequest(chatId, title, text, data);
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.notifyChat(request).enqueue(new Callback<ResponseBody>() {
                @Override public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}
                @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
            });
        });
    }

    private void markRead() {
        if (messagesRef == null || currentUid.isEmpty()) return;
        Map<String, Object> readUpdate = new HashMap<>();
        readUpdate.put("unreadCountSeeker", 0);
        db.collection("chats").document(chatId).set(readUpdate, SetOptions.merge());
        messagesRef.whereEqualTo("readAt", null)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (snapshots.isEmpty()) return;
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        if (!currentUid.equals(doc.getString("senderUid"))) {
                            batch.update(doc.getReference(), "readAt", FieldValue.serverTimestamp());
                        }
                    }
                    batch.commit();
                });
    }

    private ChatModel docToModel(DocumentSnapshot doc) {
        String text = doc.getString("text");
        String senderUid = doc.getString("senderUid");
        Timestamp ts = doc.getTimestamp("createdAt");
        long millis = ts != null ? ts.toDate().getTime() : 0;

        ChatModel cm = new ChatModel(text, senderUid, millis);
        cm.setId(doc.getId());

        Timestamp readAt = doc.getTimestamp("readAt");
        if (readAt != null) cm.setReadAt(String.valueOf(readAt.toDate().getTime()));

        return cm;
    }
}
