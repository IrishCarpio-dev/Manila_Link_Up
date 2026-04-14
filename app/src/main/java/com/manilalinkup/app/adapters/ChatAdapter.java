package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.models.ChatModel;
import com.manilalinkup.app.R;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SENT = 1;
    private static final int TYPE_RECEIVED = 2;

    List<ChatModel> chatModelList;
    String currentUserId; //for actual uid from firestore

    public ChatAdapter(List<ChatModel> chatModelList, String currentUserId) {
        this.chatModelList = chatModelList;
        this.currentUserId = currentUserId;
    }

    @Override
    public int getItemCount() {
        return chatModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatModel chat = chatModelList.get(position);
        // Compare UID to current user to decide side
        if(chat.getUid().equals(currentUserId)){
            return TYPE_SENT;
        } else {
            return TYPE_RECEIVED;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_SENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_sent, parent, false);
            return new SentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_received, parent, false);
            return new RecivedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatModel chat = chatModelList.get(position);

        if (holder instanceof SentViewHolder) {
            SentViewHolder sentHolder = (SentViewHolder) holder;
            sentHolder.chatText.setText(chat.getMessage());
            sentHolder.timeChat.setText(chat.getFormattedTime());
        } else if (holder instanceof RecivedViewHolder) {
            RecivedViewHolder receivedHolder = (RecivedViewHolder) holder;
            receivedHolder.chatText.setText(chat.getMessage());
            receivedHolder.timeChat.setText(chat.getFormattedTime());
        }
    }

    class SentViewHolder extends RecyclerView.ViewHolder {
        TextView chatText, timeChat;

        SentViewHolder(@NonNull View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.text_message_body_sent);
            timeChat = itemView.findViewById(R.id.text_message_time_sent);
        }
    }

    class RecivedViewHolder extends RecyclerView.ViewHolder {
        TextView chatText, timeChat;

        RecivedViewHolder(@NonNull View itemView) {
            super(itemView);
            chatText = itemView.findViewById(R.id.text_message_body_received);
            timeChat = itemView.findViewById(R.id.text_message_time_received);
        }
    }
}