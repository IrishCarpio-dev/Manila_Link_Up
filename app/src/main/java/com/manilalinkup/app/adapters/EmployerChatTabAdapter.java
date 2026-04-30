package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import com.manilalinkup.app.utilities.ImageUtils;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ChatListItemModel;
import com.manilalinkup.app.utilities.DateUtils;

import java.util.List;

public class EmployerChatTabAdapter extends RecyclerView.Adapter<EmployerChatTabAdapter.EmployerChatViewHolder> {

    public interface OnChatClickListener {
        void onChatClick(ChatListItemModel chat);
    }

    public interface OnChatLongClickListener {
        void onChatLongClick(ChatListItemModel chat);
    }

    private final List<ChatListItemModel> chatList;
    private final OnChatClickListener clickListener;
    private final OnChatLongClickListener longClickListener;

    public EmployerChatTabAdapter(List<ChatListItemModel> chatList,
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
    public EmployerChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_card, parent, false);
        return new EmployerChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployerChatViewHolder holder, int position) {
        holder.bind(chatList.get(position), clickListener, longClickListener);
    }

    static class EmployerChatViewHolder extends RecyclerView.ViewHolder {
        private final ImageView contactProfilePicture;
        private final TextView contactName;
        private final TextView jobTitle;
        private final TextView messagePreview;
        private final TextView messageTimeStamp;
        private final TextView unreadBadge;

        EmployerChatViewHolder(@NonNull View itemView) {
            super(itemView);
            contactProfilePicture = itemView.findViewById(R.id.item_card_profile_picture);
            contactName           = itemView.findViewById(R.id.item_card_contact_name);
            jobTitle             = itemView.findViewById(R.id.text_view_job_title);
            messagePreview       = itemView.findViewById(R.id.item_card_message_preview);
            messageTimeStamp     = itemView.findViewById(R.id.text_view_chat_timestamp);
            unreadBadge          = itemView.findViewById(R.id.text_view_unread_badge);
        }

        void bind(ChatListItemModel chat,
                  OnChatClickListener clickListener,
                  OnChatLongClickListener longClickListener) {
            ChatListItemModel.CounterpartModel counterpart = chat.getCounterpart();
            ChatListItemModel.JobSummaryModel job = chat.getJob();

            String name = counterpart != null ? counterpart.getName() : "Unknown";
            contactName.setText(name);
            if (jobTitle != null) {
                jobTitle.setText(job != null && job.getTitle() != null ? job.getTitle() : "");
            }
            messagePreview.setText(chat.getLastMessage() != null ? chat.getLastMessage() : "");
            messageTimeStamp.setText(DateUtils.formatChatTimestamp(chat.getLastMessageAt()));

            int unread = chat.getUnreadCount();
            if (unreadBadge != null) {
                if (unread > 0) {
                    unreadBadge.setVisibility(View.VISIBLE);
                    unreadBadge.setText(String.valueOf(unread));
                } else {
                    unreadBadge.setVisibility(View.GONE);
                }
            }
            messagePreview.setTypeface(null, unread > 0
                    ? android.graphics.Typeface.BOLD
                    : android.graphics.Typeface.NORMAL);

            if (counterpart != null && counterpart.getProfilePhoto() != null) {
                byte[] photoBytes = ImageUtils.decodeBase64Safe(counterpart.getProfilePhoto());
                Glide.with(itemView.getContext())
                        .load(photoBytes)
                        .placeholder(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(contactProfilePicture);
            } else {
                contactProfilePicture.setImageResource(R.drawable.ic_person_placeholder);
            }

            itemView.setOnClickListener(v -> { if (clickListener != null) clickListener.onChatClick(chat); });
            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) longClickListener.onChatLongClick(chat);
                return true;
            });
        }
    }
}
