package com.manilalinkup.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;
import com.manilalinkup.app.models.CredentialModel;

import java.util.List;

public class CredentialAdapter extends RecyclerView.Adapter<CredentialAdapter.ViewHolder> {

    private List<CredentialModel> credentialList;
    private boolean isEditable; // true = show X, false = show Status

    public CredentialAdapter(List<CredentialModel> list, boolean isEditable) {
        this.credentialList = list;
        this.isEditable = isEditable;
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

        if (isEditable) {
            // UPLOAD MODE: Show the X button, hide the status
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);

            holder.btnRemove.setOnClickListener(v -> {
                int currentPos = holder.getAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION) {
                    credentialList.remove(currentPos);
                    notifyItemRemoved(currentPos);
                    notifyItemRangeChanged(currentPos, credentialList.size());
                }
            });
        } else {
            // VAULT/PRIVACY MODE: Hide the X, show the Status color
            holder.btnRemove.setVisibility(View.GONE);
            holder.tvStatus.setVisibility(View.VISIBLE);

            String status = item.getStatus();
            holder.tvStatus.setText(status);

            // Color coding the status
            if ("VERIFIED".equals(status)) {
                holder.tvStatus.setTextColor(Color.parseColor("#2ECC71")); // Green
            } else if ("REJECTED".equals(status)) {
                holder.tvStatus.setTextColor(Color.parseColor("#E74C3C")); // Red
            } else {
                holder.tvStatus.setTextColor(Color.parseColor("#F39C12")); // Orange (Pending)
            }
        }
    }

    @Override
    public int getItemCount() {
        return credentialList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName, tvStatus;
        ImageView btnRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvStatus = itemView.findViewById(R.id.tv_status_badge);
            btnRemove = itemView.findViewById(R.id.btn_remove_credential);
        }
    }
}