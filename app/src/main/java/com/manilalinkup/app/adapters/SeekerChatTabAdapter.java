package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ChatListItemModel;

import java.util.List;

public class SeekerChatTabAdapter extends RecyclerView.Adapter<SeekerChatTabAdapter.SeekerChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(ChatListItemModel chat);
    }

    public interface OnChatLongClickListener {
        void onChatLongClick(ChatListItemModel chat);
    }

    private final List<ChatListItemModel> chatList;
    private final OnChatClickListener clickListener;
    private final OnChatLongClickListener longClickListener;

    public SeekerChatTabAdapter(List<ChatListItemModel> chatList,
                                OnChatClickListener clickListener,
                                OnChatLongClickListener longClickListener) {
        this.chatList = chatList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public int getItemCount() { return chatList.size(); }

    @NonNull
    @Override
    public SeekerChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_messages_seeker_card, parent, false);
        return new SeekerChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeekerChatViewHolder holder, int position) {
        holder.bind(chatList.get(position), clickListener, longClickListener);
    }

    static class SeekerChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView employerProfilePicture;
        private final TextView employerName;
        private final TextView messagePreview;
        private final TextView messageTimeStamp;
        private final TextView unreadBadge;

        SeekerChatViewHolder(@NonNull View itemView) {
            super(itemView);
            employerProfilePicture = itemView.findViewById(R.id.item_card_employer_profile_picture);
            employerName           = itemView.findViewById(R.id.item_card_employer_name);
            messagePreview         = itemView.findViewById(R.id.item_card_message_preview);
            messageTimeStamp       = itemView.findViewById(R.id.text_view_chat_timestamp);
            unreadBadge            = itemView.findViewById(R.id.text_view_unread_badge);
        }

        void bind(ChatListItemModel chat,
                  OnChatClickListener clickListener,
                  OnChatLongClickListener longClickListener) {
            ChatListItemModel.CounterpartModel counterpart = chat.getCounterpart();
            ChatListItemModel.JobSummaryModel job = chat.getJob();

            String name = counterpart != null ? counterpart.getName() : "Unknown";
            employerName.setText(name);
            messagePreview.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "");
            messageTimeStamp.setText(chat.getLastMessageAt() != null ? chat.getLastMessageAt() : "");

            if (unreadBadge != null) {
                int unread = chat.getUnreadCount();
                if (unread > 0) {
                    unreadBadge.setVisibility(View.VISIBLE);
                    unreadBadge.setText(String.valueOf(unread));
                } else {
                    unreadBadge.setVisibility(View.GONE);
                }
            }

            if (counterpart != null && counterpart.getProfilePhotoUrl() != null) {
                Glide.with(itemView.getContext())
                        .load(counterpart.getProfilePhotoUrl())
                        .placeholder(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(employerProfilePicture);
            } else {
                employerProfilePicture.setImageResource(R.drawable.ic_person_placeholder);
            }

            itemView.setOnClickListener(v -> { if (clickListener != null) clickListener.onChatClick(chat); });
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) longClickListener.onChatLongClick(chat);
                return true;
            });
        }
    }
}
