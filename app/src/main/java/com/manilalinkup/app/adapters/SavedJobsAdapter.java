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

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.SavedViewHolder> {

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
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_activity_card, parent, false);
        return new SavedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        JobPostDashboardModel job = savedJobs.get(position);
        holder.jobTitle.setText(job.getJobTitle());
        holder.employerName.setText(job.getEmployerName());
        holder.location.setText(job.getJobPostLocation());
        holder.duration.setText(job.getJobDuration());
        holder.postedDate.setText(job.getHowLongJobIsPosted());

        Glide.with(holder.itemView.getContext())
                .load(job.getEmployerProfilePicture())
                .circleCrop()
                .placeholder(R.drawable.user_placeholder)
                .into(holder.employerPfp);

        holder.removeBtn.setOnClickListener(v -> {
            int currentPos = holder.getAbsoluteAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION) {
                listener.onRemoveClick(currentPos);
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onJobClick(job));
    }

    @Override
    public int getItemCount() { return savedJobs.size(); }

    static class SavedViewHolder extends RecyclerView.ViewHolder {
        ImageView employerPfp, removeBtn;
        TextView jobTitle, employerName, location, duration, postedDate;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            employerPfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            removeBtn = itemView.findViewById(R.id.image_view_remove_button_job_post);
            jobTitle = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employerName = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            location = itemView.findViewById(R.id.item_card_location_placeholder);
            duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            postedDate = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
        }
    }
}