package com.manilalinkup.app.utilities;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.EmployerAddJobActivity;
import com.manilalinkup.app.models.ArchiveJobModel;

import java.util.List;

public class ArchiveJobAdapter extends RecyclerView.Adapter<ArchiveJobAdapter.ArchiveJobViewHolder> {

    private List<ArchiveJobModel> archiveJobModelList;

    public ArchiveJobAdapter(List<ArchiveJobModel> archiveJobModelList) {
        this.archiveJobModelList = archiveJobModelList;
    }

    @Override
    public int getItemCount() {
        return archiveJobModelList.size();
    }

    @NonNull
    @Override
    public ArchiveJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer_view_archived_jobs, parent, false);
        return new ArchiveJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveJobViewHolder holder, int position) {
        ArchiveJobModel jobAtBindingTime = archiveJobModelList.get(position);

        holder.bind(jobAtBindingTime);

        holder.repostButton.setOnClickListener(v -> {
            int actualPos = holder.getBindingAdapterPosition();
            if (actualPos == RecyclerView.NO_POSITION) return;

            ArchiveJobModel currentJob = archiveJobModelList.get(actualPos);
            Intent intent = new Intent(v.getContext(), EmployerAddJobActivity.class);

            intent.putExtra("job_id", currentJob.getJobId());
            intent.putExtra("is_repost", true);
            v.getContext().startActivity(intent);
        });

        holder.editButton.setOnClickListener(v -> {
            int actualPos = holder.getBindingAdapterPosition();
            if (actualPos == RecyclerView.NO_POSITION) return;

            ArchiveJobModel currentJob = archiveJobModelList.get(actualPos);
            Intent intent = new Intent(v.getContext(), EmployerAddJobActivity.class);

            intent.putExtra("job_id", currentJob.getJobId());
            // Note: is_repost will be 'false' by default on the next screen because we didn't add it here
            v.getContext().startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> {
            int actualPosition = holder.getBindingAdapterPosition();
            if (actualPosition == RecyclerView.NO_POSITION) return;

            ArchiveJobModel jobToDelete = archiveJobModelList.get(actualPosition);

            new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("Delete Post?")
                    .setMessage("Are you sure you want to permanently delete " + jobToDelete.getJobTitle() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Logic: Should call API here before removing from the list!
                        archiveJobModelList.remove(actualPosition);
                        notifyItemRemoved(actualPosition);
                        notifyItemRangeChanged(actualPosition, archiveJobModelList.size());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

    }

    static class ArchiveJobViewHolder extends RecyclerView.ViewHolder{
        TextView job_title;
        TextView statusText;
        TextView insightText;
        Button repostButton;
        Button editButton;
        Button deleteButton;

        public ArchiveJobViewHolder(@NonNull View itemView) {
            super(itemView);
            job_title = itemView.findViewById(R.id.text_view_archived_title);
            statusText = itemView.findViewById(R.id.text_view_status_badge);
            insightText = itemView.findViewById(R.id.text_view_insights_text);
            repostButton = itemView.findViewById(R.id.button_repost_archived);
            editButton = itemView.findViewById(R.id.button_edit_archived);
            deleteButton = itemView.findViewById(R.id.button_delete_archived);
        }

        public void bind(ArchiveJobModel archiveJobBind){
            job_title.setText(archiveJobBind.getJobTitle());
            statusText.setText(archiveJobBind.getStatusText());
            insightText.setText(archiveJobBind.getInsightText());
        }

    }

}
