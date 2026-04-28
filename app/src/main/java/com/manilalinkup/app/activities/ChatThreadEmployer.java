package com.manilalinkup.app.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.manilalinkup.app.models.ApiResponse;
import com.manilalinkup.app.models.ApplicationModel;
import com.manilalinkup.app.models.MarkCompleteRequest;
import com.manilalinkup.app.models.UpdateApplicationStatusRequest;

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

public class ChatThreadEmployer extends BaseActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<ChatModel> chatList;
    private EditText messageInput;
    private ImageButton btnSend;

    private String chatId;
    private String jobTitle;
    private String currentUid;
    private String applicationId;
    private String counterpartName;
    private int applicationStatus;

    private LinearLayout buttonContainer;
    private MaterialButton btnSecondary;
    private MaterialButton btnPrimary;

    private FirebaseFirestore db;
    private CollectionReference messagesRef;
    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_thread_employer);

        chatId = getIntent().getStringExtra("CHAT_ID");
        jobTitle = getIntent().getStringExtra("JOB_TITLE");
        applicationId = getIntent().getStringExtra("APPLICATION_ID");
        applicationStatus = getIntent().getIntExtra("APPLICATION_STATUS", 0);
        counterpartName = getIntent().getStringExtra("COUNTERPART_NAME");
        String seekerUid = getIntent().getStringExtra("SEEKER_UID");
        String employerUid = getIntent().getStringExtra("EMPLOYER_UID");

        setupToolbar(R.id.toolbar);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(counterpartName != null ? counterpartName : "");
        if (jobTitle != null && !jobTitle.isEmpty()) toolbar.setSubtitle(jobTitle);


        buttonContainer = findViewById(R.id.button_container);
        btnSecondary = findViewById(R.id.button_accept_seeker);
        btnPrimary = findViewById(R.id.button_reject_seeker);
        updateActionBar();

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
                    db.collection("chats").document(chatId).update("unreadCountSeeker", FieldValue.increment(1));
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
            data.put("role", "seeker");
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
        db.collection("chats").document(chatId).update("unreadCountEmployer", 0);
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

    private void updateActionBar() {
        if (applicationId == null) {
            buttonContainer.setVisibility(View.GONE);
            return;
        }
        buttonContainer.setVisibility(View.VISIBLE);
        switch (applicationStatus) {
            case 1:
            case 2:
                btnSecondary.setVisibility(View.VISIBLE);
                btnSecondary.setText("Reject");
                btnSecondary.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Reject applicant?")
                            .setMessage("This will reject this applicant's application.")
                            .setPositiveButton("Reject", (d, w) -> updateStatus(3))
                            .setNegativeButton("Cancel", null)
                            .show()
                );
                btnPrimary.setText("Hire");
                btnPrimary.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Hire applicant?")
                            .setMessage("This will reject all other applicants and archive the job. Continue?")
                            .setPositiveButton("Hire", (d, w) -> updateStatus(5))
                            .setNegativeButton("Cancel", null)
                            .show()
                );
                break;
            case 5:
                btnSecondary.setVisibility(View.GONE);
                btnPrimary.setText("Mark Complete");
                btnPrimary.setOnClickListener(v ->
                    new AlertDialog.Builder(this)
                            .setTitle("Mark job as complete?")
                            .setMessage("This will mark the job as complete. This action cannot be undone.")
                            .setPositiveButton("Confirm", (d, w) -> markComplete())
                            .setNegativeButton("Cancel", null)
                            .show()
                );
                break;
            case 6:
                btnSecondary.setVisibility(View.GONE);
                btnPrimary.setText("Rate Worker");
                btnPrimary.setOnClickListener(v -> openRating());
                break;
            default:
                buttonContainer.setVisibility(View.GONE);
        }
    }

    private void markComplete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        showProgress("Marking as complete...");
        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.markApplicationComplete(new MarkCompleteRequest(applicationId))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                            hideProgress();
                            if (response.isSuccessful() && response.body() != null) {
                                applicationStatus = 6;
                                Toast.makeText(ChatThreadEmployer.this, "Marked as complete!", Toast.LENGTH_SHORT).show();
                                updateActionBar();
                            } else {
                                ErrorUtils.showErrorMessage(ChatThreadEmployer.this, response.errorBody());
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                            hideProgress();
                            ErrorUtils.showThrowableError(ChatThreadEmployer.this, t);
                        }
                    });
        });
    }

    private void openRating() {
        Intent intent = new Intent(this, SubmitRatingActivity.class);
        intent.putExtra("APPLICATION_ID", applicationId);
        intent.putExtra("COUNTERPART_NAME", counterpartName != null ? counterpartName : "Worker");
        startActivity(intent);
    }

    private void updateStatus(int newStatus) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;
        showProgress(newStatus == 5 ? "Hiring applicant..." : "Rejecting applicant...");
        user.getIdToken(true).addOnSuccessListener(result -> {
            ApiService api = RetrofitClient.getClient(result.getToken()).create(ApiService.class);
            api.updateApplicationStatus(new UpdateApplicationStatusRequest(applicationId, newStatus))
                    .enqueue(new Callback<ApiResponse<ApplicationModel>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<ApplicationModel>> call, Response<ApiResponse<ApplicationModel>> response) {
                            hideProgress();
                            if (response.isSuccessful()) {
                                String msg = newStatus == 5 ? "Applicant hired!" : "Applicant rejected";
                                Toast.makeText(ChatThreadEmployer.this, msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                ErrorUtils.showErrorMessage(ChatThreadEmployer.this, response.errorBody());
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<ApplicationModel>> call, Throwable t) {
                            hideProgress();
                            ErrorUtils.showThrowableError(ChatThreadEmployer.this, t);
                        }
                    });
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
