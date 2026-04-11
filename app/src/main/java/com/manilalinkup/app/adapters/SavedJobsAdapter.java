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
import com.manilalinkup.app.models.JobPostDashboardModel;
import java.util.List;

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.ViewHolder> {

    public interface OnSavedJobClickListener {
        void onRemoveClick(int position);
        void onJobClick(JobPostDashboardModel job);
    }

    private List<JobPostDashboardModel> savedJobs;
    private OnSavedJobClickListener listener;

    public SavedJobsAdapter(List<JobPostDashboardModel> savedJobs, OnSavedJobClickListener listener) {
        this.savedJobs = savedJobs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Fixed: Corrected layout name to item_my_activity_card
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_activity_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JobPostDashboardModel job = savedJobs.get(position);
        holder.jobTitle.setText(job.getJobTitle());
        holder.employerName.setText(job.getEmployerName());
        holder.location.setText(job.getJobPostLocation());
        holder.timePosted.setText(job.getHowLongJobIsPosted());

        Glide.with(holder.itemView.getContext())
                .load(job.getEmployerProfilePicture())
                .circleCrop()
                .placeholder(R.drawable.chipsstarters)
                .into(holder.employerLogo);

        holder.removeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJobClick(job);
            }
        });
    }

    @Override
    public int getItemCount() {
        return savedJobs != null ? savedJobs.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView employerLogo, removeButton;
        TextView jobTitle, employerName, location, timePosted;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            employerLogo = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            jobTitle = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employerName = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            location = itemView.findViewById(R.id.item_card_location_placeholder);
            timePosted = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
            removeButton = itemView.findViewById(R.id.image_view_remove_button_job_post);
        }
    }
}
