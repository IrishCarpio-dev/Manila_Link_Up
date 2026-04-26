package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.AppliedJobModel;
import com.manilalinkup.app.models.ArchiveJobModel;
import com.manilalinkup.app.activities.EmployerAddJobActivity;

import android.content.Intent;
import android.widget.Button;

import java.util.List;

public class AppliedJobsAdapter extends RecyclerView.Adapter<AppliedJobsAdapter.AppliedViewHolder> {
    public interface OnAppliedJobClickListener {
        void onJobClick(AppliedJobModel job);
    }
    private List<AppliedJobModel> appliedJobs;
    private OnAppliedJobClickListener listener;

    public AppliedJobsAdapter(List<AppliedJobModel> appliedJobs, OnAppliedJobClickListener listener) {
        this.appliedJobs = appliedJobs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppliedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applied_tab_card, parent, false);
        return new AppliedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppliedViewHolder holder, int position) {
        AppliedJobModel application = appliedJobs.get(position);
        int status = application.getStatus() != null ? application.getStatus() : 1;

        if (application.getJob() != null) {
            holder.jobTitle.setText(application.getJob().getTitle());
            if (application.getJob().getEmployer() != null) {
                holder.employerName.setText(application.getJob().getEmployer().getFullName());
                Glide.with(holder.itemView.getContext())
                        .load(application.getJob().getEmployer().getProfilePhotoUrl())
                        .circleCrop()
                        .into(holder.employerLogo);
            }
        }

        switch (status) {
            case 1:
                holder.statusBadge.setText("Pending");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_pending);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#E65100"));
                break;
            case 2:
                holder.statusBadge.setText("Interview");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_interview);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#1565C0"));
                break;
            case 3:
                holder.statusBadge.setText("Rejected");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_rejected);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#B71C1C"));
                break;
            case 5:
                holder.statusBadge.setText("Hired");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            case 6:
                holder.statusBadge.setText("Completed");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            default:
                holder.statusBadge.setText("Pending");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_pending);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#E65100"));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJobClick(application);
            }
        });
    }

    @Override
    public int getItemCount() { return appliedJobs.size(); }

    static class AppliedViewHolder extends RecyclerView.ViewHolder {
        ImageView employerLogo;
        TextView jobTitle, employerName, statusBadge;

        public AppliedViewHolder(@NonNull View itemView) {
            super(itemView);
            employerLogo = itemView.findViewById(R.id.applied_employer_logo);
            jobTitle = itemView.findViewById(R.id.applied_job_title);
            employerName = itemView.findViewById(R.id.applied_employer_name);
            statusBadge = itemView.findViewById(R.id.applied_status_badge);
        }
    }

    public static class ArchiveJobAdapter extends RecyclerView.Adapter<ArchiveJobAdapter.ArchiveJobViewHolder> {

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
                            archiveJobModelList.remove(actualPosition);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, archiveJobModelList.size());
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });

        }

        static class ArchiveJobViewHolder extends RecyclerView.ViewHolder {
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

            public void bind(ArchiveJobModel archiveJobBind) {
                job_title.setText(archiveJobBind.getJobTitle());
                statusText.setText(archiveJobBind.getStatusText());
                insightText.setText(archiveJobBind.getInsightText());
            }
        }
    }
}
