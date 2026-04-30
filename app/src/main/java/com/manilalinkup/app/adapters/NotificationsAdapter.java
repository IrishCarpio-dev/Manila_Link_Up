package com.manilalinkup.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.models.NotificationsModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotifViewHolder> {

    public interface OnItemClickListener {
        void onNotificationClick(NotificationsModel notification);
    }

    private final List<NotificationsModel> notificationsModelList;
    private OnItemClickListener listener;

    public NotificationsAdapter(List<NotificationsModel> notificationsModelList) {
        this.notificationsModelList = notificationsModelList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<NotificationsModel> newItems) {
        notificationsModelList.clear();
        notificationsModelList.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return notificationsModelList.size();
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications_card, parent, false);
        return new NotifViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        NotificationsModel item = notificationsModelList.get(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificationClick(item);
        });
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final View unreadDot;
        private final ImageView imageType;
        private final TextView notifTitle;
        private final TextView descriptionTitle;
        private final TextView notifTimeStamp;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            card           = (CardView) itemView;
            unreadDot      = itemView.findViewById(R.id.view_unread_dot);
            imageType      = itemView.findViewById(R.id.item_card_notif_image_type);
            notifTitle     = itemView.findViewById(R.id.item_card_notif_title);
            descriptionTitle = itemView.findViewById(R.id.item_card_notif_details);
            notifTimeStamp = itemView.findViewById(R.id.text_view_notif_timestamp);
        }

        public void bind(NotificationsModel item) {
            imageType.setImageResource(item.getType());
            notifTitle.setText(item.getTitle());
            descriptionTitle.setText(item.getDescription());
            notifTimeStamp.setText(item.getTimestamp());

            if (!item.isRead()) {
                card.setCardBackgroundColor(Color.parseColor("#EEF2FB"));
                notifTitle.setTextColor(Color.parseColor("#0D47A1"));
                unreadDot.setVisibility(View.VISIBLE);
            } else {
                card.setCardBackgroundColor(Color.WHITE);
                notifTitle.setTextColor(Color.parseColor("#212121"));
                unreadDot.setVisibility(View.GONE);
            }
        }
    }
}
