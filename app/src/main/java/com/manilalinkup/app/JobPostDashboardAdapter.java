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
    boolean isProfileView;

    public JobPostDashboardAdapter(List<JobPostDashboardModel> jobPostDashboardModelList, boolean isProfileView) {
        this.jobPostDashboardModelList = jobPostDashboardModelList;
        this.isProfileView = isProfileView;
    }

    @Override
    public int getItemCount() {
        return jobPostDashboardModelList.size();
    }

    @NonNull
    @Override
    public JobPostDashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_post_card, parent, false);
        int layoutId = isProfileView ? R.layout.item_employer_job_list_profile : R.layout.item_job_post_card;

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new JobPostDashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobPostDashboardViewHolder holder, int position) {
        holder.bind(jobPostDashboardModelList.get(position));
    }

    static class JobPostDashboardViewHolder extends RecyclerView.ViewHolder {
        private ImageView employer_pfp;
        private TextView job_title;
        private TextView employer_name;
        private TextView job_location;
        private TextView job_duration;
        private TextView how_long_job_was_posted;
        public JobPostDashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            employer_pfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            job_title = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employer_name = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            job_location = itemView.findViewById(R.id.item_card_location_placeholder);
            job_duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            how_long_job_was_posted = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
        }

        public void bind(JobPostDashboardModel jobBind){

            if (employer_pfp != null) {
                employer_pfp.setImageResource(jobBind.employerProfilePicture);
            }
            if (employer_name != null) {
                employer_name.setText(jobBind.employerName);
            }

            if (job_title != null) {
                job_title.setText(jobBind.jobTitle);
            }

            if (job_location != null) {
                job_location.setText(jobBind.jobPostLocation);
            }

            if (job_duration != null) {
                job_duration.setText(jobBind.job_duration);
            }

            if (how_long_job_was_posted != null) {
                how_long_job_was_posted.setText(jobBind.howLongJobIsPosted);
            }
        }
    }
}




