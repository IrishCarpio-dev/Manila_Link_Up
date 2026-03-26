package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SeekerChatTabAdapter extends RecyclerView.Adapter<SeekerChatTabAdapter.SeekerChatViewHolder> {

    private List<SeekerChatModel> seekerChatModelList;

    public SeekerChatTabAdapter(List<SeekerChatModel> seekerChatModelList) {
        this.seekerChatModelList = seekerChatModelList;
    }

    @Override
    public int getItemCount() {
        return seekerChatModelList.size();
    }

    @NonNull
    @Override
    public SeekerChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_messages_seeker_card, parent, false);
        return new SeekerChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeekerChatViewHolder holder, int position) {
        holder.bind(seekerChatModelList.get(position));
    }
    static class SeekerChatViewHolder extends RecyclerView.ViewHolder {

        private ImageView employerProfilePicture;
        private TextView employerName;
        private TextView messagePreview;
        private TextView messageTimeStamp;
        public SeekerChatViewHolder(@NonNull View itemView) {
            super(itemView);
            employerProfilePicture = itemView.findViewById(R.id.item_card_employer_profile_picture);
            employerName = itemView.findViewById(R.id.item_card_employer_name);
            messagePreview = itemView.findViewById(R.id.item_card_message_preview);
            messageTimeStamp = itemView.findViewById(R.id.text_view_chat_timestamp);
        }

        public void bind(SeekerChatModel seekerChatModel){
            employerProfilePicture.setImageResource(seekerChatModel.employerImage);
            employerName.setText(seekerChatModel.employerName);
            messagePreview.setText(seekerChatModel.messagePreview);
            messageTimeStamp.setText(seekerChatModel.messagePreview);
        }
    }

}
