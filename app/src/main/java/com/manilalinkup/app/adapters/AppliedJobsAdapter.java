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
import com.manilalinkup.app.models.JobPostDashboardModel; // Reusing model for now
import java.util.List;

public class AppliedJobsAdapter extends RecyclerView.Adapter<AppliedJobsAdapter.AppliedViewHolder> {

    private List<JobPostDashboardModel> appliedJobs;

    public AppliedJobsAdapter(List<JobPostDashboardModel> appliedJobs) {
        this.appliedJobs = appliedJobs;
    }

    @NonNull
    @Override
    public AppliedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applied_tab_card, parent, false);
        return new AppliedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppliedViewHolder holder, int position) {
        JobPostDashboardModel job = appliedJobs.get(position);
        holder.jobTitle.setText(job.getJobTitle());
        holder.employerName.setText(job.getEmployerName());

        String status;
        if (position % 3 == 0) {
            status = "PENDING";
        } else if (position % 3 == 1) {
            status = "INTERVIEW";
        } else {
            status = "ACCEPTED";
        }

        holder.statusBadge.setText(status);

        switch (status) {
            case "PENDING":
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_pending);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#E65100")); // Dark Orange
                break;
            case "INTERVIEW":
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_interview);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#1565C0")); // Dark Blue
                break;
            case "ACCEPTED":
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#2E7D32")); // Dark Green
                break;
        }

        Glide.with(holder.itemView.getContext())
                .load(job.getEmployerProfilePicture())
                .circleCrop()
                .into(holder.employerLogo);
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
}