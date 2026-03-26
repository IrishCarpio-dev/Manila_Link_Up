package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

public class EmployerChatTabAdapter extends RecyclerView.Adapter<EmployerChatTabAdapter.EmployerChatViewHolder> {

    private List<EmployerChatModel> employerChatModelList;

    public EmployerChatTabAdapter(List<EmployerChatModel> employerChatModelList) {
        this.employerChatModelList = employerChatModelList;
    }


    @Override
    public int getItemCount() {
        return employerChatModelList.size();
    }

    @NonNull
    @Override
    public EmployerChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messages_employer_card, parent, false);
        return new EmployerChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployerChatViewHolder holder, int position) {
        holder.bind(employerChatModelList.get(position));
    }

    static class  EmployerChatViewHolder extends RecyclerView.ViewHolder {
        private ImageView seekerProfilePicture;
        private TextView seekerName;
        private TextView messagePreview;
        private TextView messageTimeStamp;

        public EmployerChatViewHolder(@NonNull View itemView){
            super(itemView);
            seekerProfilePicture = itemView.findViewById(R.id.item_card_seeker_profile_picture);
            seekerName = itemView.findViewById(R.id.item_card_seeker_name);
            messagePreview = itemView.findViewById(R.id.item_card_message_preview);
            messageTimeStamp = itemView.findViewById(R.id.text_view_chat_timestamp);

        }

        public void bind(EmployerChatModel employerChatModel){
            seekerProfilePicture.setImageResource(employerChatModel.seekerImage);
            seekerName.setText(employerChatModel.seekerName);
            messagePreview.setText(employerChatModel.messagePreview);
            messageTimeStamp.setText(employerChatModel.messageTimeStamp);
        }

    }
}
