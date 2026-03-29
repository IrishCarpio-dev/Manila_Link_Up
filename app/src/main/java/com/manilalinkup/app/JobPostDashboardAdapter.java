package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class JobPostDashboardAdapter extends RecyclerView.Adapter<JobPostDashboardAdapter.JobPostDashboardViewHolder> {

    private final List<JobPostDashboardModel> jobPostDashboardModelList;
    private OnSaveClickListener onSaveClickListener;

    public interface OnSaveClickListener {
        void onSaveClick(JobPostDashboardModel job);
    }

    public void setOnSaveClickListener(OnSaveClickListener listener) {
        this.onSaveClickListener = listener;
    }

    public JobPostDashboardAdapter(List<JobPostDashboardModel> jobPostDashboardModelList) {
        this.jobPostDashboardModelList = jobPostDashboardModelList;
    }

    @Override
    public int getItemCount() {
        return jobPostDashboardModelList.size();
    }

    @NonNull
    @Override
    public JobPostDashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_post_card, parent, false);
        return new JobPostDashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobPostDashboardViewHolder holder, int position) {
        JobPostDashboardModel job = jobPostDashboardModelList.get(position);
        holder.bind(job);
        
        holder.saveButton.setOnClickListener(v -> {
            if (onSaveClickListener != null) {
                onSaveClickListener.onSaveClick(job);
            }
        });
    }

    static class JobPostDashboardViewHolder extends RecyclerView.ViewHolder {
        private ImageView employer_pfp;
        private TextView job_title;
        private TextView employer_name;
        private TextView job_location;
        private TextView job_duration;
        private TextView how_long_job_was_posted;
        ImageView saveButton;

        public JobPostDashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            employer_pfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            job_title = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employer_name = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            job_location = itemView.findViewById(R.id.item_card_location_placeholder);
            job_duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            how_long_job_was_posted = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
            saveButton = itemView.findViewById(R.id.image_view_report_button_job_post);
        }

        public void bind(JobPostDashboardModel jobBind){
            employer_pfp.setImageResource(jobBind.employerProfilePicture);
            job_title.setText(jobBind.jobTitle);
            employer_name.setText(jobBind.employerName);
            job_location.setText(jobBind.jobPostLocation);
            job_duration.setText(jobBind.job_duration);
            how_long_job_was_posted.setText(jobBind.howLongJobIsPosted);
        }
    }
}