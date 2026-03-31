package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployerNotificationsAdapter extends RecyclerView.Adapter<EmployerNotificationsAdapter.EmployerNotificationsViewHolder> {

    private final List<EmployerNotificationsModel> employerNotificationsModelList;

    public EmployerNotificationsAdapter(List<EmployerNotificationsModel> employerNotificationsModelList) {
        this.employerNotificationsModelList = employerNotificationsModelList;
    }

    @Override
    public int getItemCount() {
        return employerNotificationsModelList.size();
    }

    @NonNull
    @Override
    public EmployerNotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notifications_employer_card, parent, false);
        return new EmployerNotificationsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployerNotificationsViewHolder holder, int position) {
        holder.bind(employerNotificationsModelList.get(position));
    }

    static class EmployerNotificationsViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageType;
        private TextView notifTitle;
        private TextView descriptionTitle;
        private TextView notifTimeStamp;
        public EmployerNotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageType = itemView.findViewById(R.id.item_card_notif_image_type);
            notifTitle = itemView.findViewById(R.id.item_card_notif_title);
            descriptionTitle = itemView.findViewById(R.id.item_card_notif_details);
            notifTimeStamp = itemView.findViewById(R.id.text_view_notif_timestamp);
        }

        public void bind(EmployerNotificationsModel employerNotificationsModel){
            imageType.setImageResource(employerNotificationsModel.imageType);
            notifTitle.setText(employerNotificationsModel.notifTitle);
            descriptionTitle.setText(employerNotificationsModel.descriptionNotif);
            notifTimeStamp.setText(employerNotificationsModel.notifTimeStamp);
        }
    }


}
