package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.models.EmployerChatModel;
import com.manilalinkup.app.R;

import java.util.List;

public class EmployerChatTabAdapter extends RecyclerView.Adapter<EmployerChatTabAdapter.EmployerChatViewHolder> {

    private List<EmployerChatModel> employerChatModelList;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(EmployerChatModel chat);
    }

    public EmployerChatTabAdapter(List<EmployerChatModel> employerChatModelList, OnChatClickListener listener) {
        this.employerChatModelList = employerChatModelList;
        this.listener = listener;
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
        holder.bind(employerChatModelList.get(position), listener);
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

        public void bind(EmployerChatModel employerChatModel, OnChatClickListener listener){
            seekerProfilePicture.setImageResource(employerChatModel.getSeekerImage());
            seekerName.setText(employerChatModel.getSeekerName());
            messagePreview.setText(employerChatModel.getMessagePreview());
            messageTimeStamp.setText(employerChatModel.getMessageTimeStamp());

            itemView.setOnClickListener(v -> {
                if(listener != null){
                    listener.onChatClick(employerChatModel);
                }
            });
        }

    }
}
