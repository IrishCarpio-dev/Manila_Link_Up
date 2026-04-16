package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.models.SeekerNotificationModel;
import com.manilalinkup.app.R;

import java.util.List;

public class SeekerNotificationsAdapter extends RecyclerView.Adapter<SeekerNotificationsAdapter.SeekerNotificationsViewHolder> {

    private final List<SeekerNotificationModel> seekerNotificationsList;

    public SeekerNotificationsAdapter(List<SeekerNotificationModel> seekerNotificationsList) {
        this.seekerNotificationsList = seekerNotificationsList;
    }

    @Override
    public int getItemCount() {
        return seekerNotificationsList.size();
    }

    @NonNull
    @Override
    public SeekerNotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notifications_seeker_card, parent, false);
        return new SeekerNotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeekerNotificationsViewHolder holder, int position) {
        holder.bind(seekerNotificationsList.get(position));
    }

    public static class SeekerNotificationsViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageType;
        private final TextView notifTitle;
        private final TextView descriptionTitle;
        private final TextView notifTimeStamp;

        public SeekerNotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageType = itemView.findViewById(R.id.notif_icon);
            notifTitle = itemView.findViewById(R.id.notif_title);
            descriptionTitle = itemView.findViewById(R.id.notif_description);
            notifTimeStamp = itemView.findViewById(R.id.notif_time);
        }

        public void bind(SeekerNotificationModel model) {
            imageType.setImageResource(model.getImageType());
            notifTitle.setText(model.getNotifTitle());
            descriptionTitle.setText(model.getDescriptionNotif());
            notifTimeStamp.setText(model.getNotifTimeStamp());
        }
    }
}