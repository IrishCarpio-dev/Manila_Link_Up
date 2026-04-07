package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.ViewHolder> {

    private List<CredentialModel> credentialList;

    public CredentialAdapter(List<CredentialModel> list) {
        this.credentialList = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_credential, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CredentialModel item = credentialList.get(position);
        holder.tvFileName.setText(item.getFileName());

        // Delete Logic
        holder.btnRemove.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                credentialList.remove(currentPos);
                notifyItemRemoved(currentPos);
                // Update following items to refresh their positions
                notifyItemRangeChanged(currentPos, credentialList.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        ImageView btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            btnRemove = itemView.findViewById(R.id.btn_remove_credential);
        }
    }
}