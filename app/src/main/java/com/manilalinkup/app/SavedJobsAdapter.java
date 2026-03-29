package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.ViewHolder> {
    private List<JobPostDashboardModel> savedJobs;
    private OnRemoveClickListener listener;

    public interface OnRemoveClickListener {
        void onRemoveClick(JobPostDashboardModel job);
    }

    public void setOnRemoveClickListener(OnRemoveClickListener listener) {
        this.listener = listener;
    }

    public SavedJobsAdapter(List<JobPostDashboardModel> savedJobs) {
        this.savedJobs = savedJobs;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myactivity_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobPostDashboardModel job = savedJobs.get(position);
        holder.jobTitle.setText(job.getJobTitle());
        holder.employerName.setText(job.getEmployerName());
        holder.location.setText(job.getJobPostLocation());
        holder.duration.setText(job.getJob_duration());
        holder.pfp.setImageResource(job.getEmployerProfilePicture());
        holder.howLongAgo.setText(job.getHowLongJobIsPosted());

        if (listener != null) {
            holder.removeBtn.setOnClickListener(v -> listener.onRemoveClick(job));
        }
    }

    @Override
    public int getItemCount() { return savedJobs.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, employerName, location, duration, howLongAgo;
        ImageView pfp, removeBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employerName = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            location = itemView.findViewById(R.id.item_card_location_placeholder);
            duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            howLongAgo = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
            pfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            removeBtn = itemView.findViewById(R.id.image_view_remove_button_job_post);
        }
    }
}
