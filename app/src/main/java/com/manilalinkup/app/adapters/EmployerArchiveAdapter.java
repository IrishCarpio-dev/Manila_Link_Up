package com.manilalinkup.app.adapters;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.JobPostDashboardModel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmployerArchiveAdapter extends RecyclerView.Adapter<EmployerArchiveAdapter.ArchiveViewHolder> {

    private final List<JobPostDashboardModel> items;

    public EmployerArchiveAdapter(List<JobPostDashboardModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_employer_dashboard_job_post_card, parent, false);
        return new ArchiveViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void replaceItems(List<JobPostDashboardModel> updatedItems) {
        items.clear();
        items.addAll(updatedItems);
        notifyDataSetChanged();
    }

    static class ArchiveViewHolder extends RecyclerView.ViewHolder {
        private final ImageView employerPhoto;
        private final TextView jobTitle;
        private final TextView employerName;
        private final TextView location;
        private final TextView duration;
        private final TextView postedAgo;

        ArchiveViewHolder(@NonNull View itemView) {
            super(itemView);
            employerPhoto = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            jobTitle = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employerName = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            location = itemView.findViewById(R.id.item_card_location_placeholder);
            duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            postedAgo = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
        }

        void bind(JobPostDashboardModel item) {
            Glide.with(itemView.getContext())
                 .load(item.getEmployerProfilePicture())
                 .into(employerPhoto);
            jobTitle.setText(item.getJobTitle());
            employerName.setText(item.getEmployerName());
            location.setText(item.getJobPostLocation());
            duration.setText(item.getJobDuration());
            postedAgo.setText(item.getHowLongJobIsPosted());
        }
    }
}
