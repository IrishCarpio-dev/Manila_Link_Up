package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.R;

import java.util.List;
public class JobPostDashboardAdapter extends RecyclerView.Adapter<JobPostDashboardAdapter.JobPostDashboardViewHolder> {
    public interface OnJobClickListener {
        void onJobClick(JobPostDashboardModel job);
        void onRemoveClick(JobPostDashboardModel job, int position);
    }
    private final List<JobPostDashboardModel> jobPostDashboardModelList;
    private final OnJobClickListener listener;
    private final boolean isProfileView;
    private final boolean showActionIcon;

    public JobPostDashboardAdapter(List<JobPostDashboardModel> jobPostDashboardModelList, boolean isProfileView, OnJobClickListener listener) {
        this(jobPostDashboardModelList, isProfileView, listener, true);
    }

    public JobPostDashboardAdapter(List<JobPostDashboardModel> jobPostDashboardModelList, boolean isProfileView, OnJobClickListener listener, boolean showActionIcon) {
        this.jobPostDashboardModelList = jobPostDashboardModelList;
        this.isProfileView = isProfileView;
        this.listener = listener;
        this.showActionIcon = showActionIcon;
    }

    @Override
    public int getItemCount() {
        return jobPostDashboardModelList.size();
    }

    @NonNull
    @Override
    public JobPostDashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isProfileView ? R.layout.item_employer_job_list_profile : R.layout.item_job_post_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new JobPostDashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobPostDashboardViewHolder holder, int position) {
        JobPostDashboardModel currentJob = jobPostDashboardModelList.get(position);
        holder.bind(currentJob, listener, showActionIcon);
    }

    static class JobPostDashboardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView employer_pfp;
        private final TextView job_title;
        private final TextView employer_name;
        private final TextView job_location;
        private final TextView job_duration;
        private final TextView how_long_job_was_posted;
        private final ImageView removeButton;
        private final ImageView overflowButton;
        public JobPostDashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            employer_pfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            job_title = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employer_name = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            job_location = itemView.findViewById(R.id.item_card_location_placeholder);
            job_duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            how_long_job_was_posted = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);

            removeButton = itemView.findViewById(R.id.image_view_remove_button_job_post);
            overflowButton = itemView.findViewById(R.id.image_view_dots_button_job_post);
        }

        public void bind(JobPostDashboardModel jobBind, OnJobClickListener listener, boolean showActionIcon){

            if (employer_pfp != null) {
                Glide.with(itemView.getContext())
                        .load(jobBind.getEmployerProfilePicture())
                        .placeholder(R.drawable.user_placeholder)
                        .error(R.drawable.user_placeholder)
                        .centerCrop()
                        .into(employer_pfp);
            }
            if (employer_name != null) {
                employer_name.setText(jobBind.getEmployerName());
            }

            if (job_title != null) {
                job_title.setText(jobBind.getJobTitle());
            }

            if (job_location != null) {
                job_location.setText(jobBind.getJobPostLocation());
            }

            if (job_duration != null) {
                job_duration.setText(jobBind.getJob_duration());
            }

            if (how_long_job_was_posted != null) {
                how_long_job_was_posted.setText(jobBind.getHowLongJobIsPosted());
            }

            if (removeButton != null) {
                removeButton.setVisibility(showActionIcon ? View.VISIBLE : View.GONE);
                removeButton.setOnClickListener(showActionIcon && listener != null ? v -> {
                    int currentPosition = getAbsoluteAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        listener.onRemoveClick(jobBind, currentPosition);
                    }
                } : null);
            }

            if (overflowButton != null) {
                overflowButton.setVisibility(showActionIcon ? View.VISIBLE : View.GONE);
                overflowButton.setOnClickListener(null);
            }

            itemView.setClickable(listener != null);
            itemView.setFocusable(listener != null);
            itemView.setOnClickListener(listener == null ? null : v -> listener.onJobClick(jobBind));

        }
    }
}
