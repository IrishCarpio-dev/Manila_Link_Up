package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SeekerDashboardAdapter extends RecyclerView.Adapter<SeekerDashboardAdapter.SeekerDashboardViewHolder> {
    public interface OnJobPostClickListener {
        void onJobPostClick(JobPostDashboardModel item);
    }

    private final List<JobPostDashboardModel> items;
    private final OnJobPostClickListener onJobPostClickListener;

    public SeekerDashboardAdapter(
            List<JobPostDashboardModel> items,
            OnJobPostClickListener onJobPostClickListener
    ) {
        this.items = items;
        this.onJobPostClickListener = onJobPostClickListener;
    }

    @NonNull
    @Override
    public SeekerDashboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_job_post_card, parent, false);
        return new SeekerDashboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeekerDashboardViewHolder holder, int position) {
        holder.bind(items.get(position), onJobPostClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class SeekerDashboardViewHolder extends RecyclerView.ViewHolder {
        private final ImageView employerPhoto;
        private final TextView jobTitle;
        private final TextView employerName;
        private final TextView location;
        private final TextView duration;
        private final TextView postedAgo;

        SeekerDashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            employerPhoto = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            jobTitle = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employerName = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            location = itemView.findViewById(R.id.item_card_location_placeholder);
            duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            postedAgo = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
        }

        void bind(JobPostDashboardModel item, OnJobPostClickListener onJobPostClickListener) {
            employerPhoto.setImageResource(item.getEmployerProfilePicture());
            jobTitle.setText(item.getJobTitle());
            employerName.setText(item.getEmployerName());
            location.setText(item.getJobPostLocation());
            duration.setText(item.getJobDuration());
            postedAgo.setText(item.getHowLongJobIsPosted());
            itemView.setOnClickListener(v -> onJobPostClickListener.onJobPostClick(item));
        }
    }
}
