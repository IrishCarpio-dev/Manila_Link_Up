package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;
import com.manilalinkup.app.activities.SaveSeekerActivity;
import com.manilalinkup.app.models.SeekerJobModel;

import java.util.List;

public class SeekerJobAdapter extends RecyclerView.Adapter<SeekerJobAdapter.SeekerJobViewHolder> {

    private final List<SeekerJobModel> seekerJobList;

    public SeekerJobAdapter(List<SeekerJobModel> seekerJobList) {
        this.seekerJobList = seekerJobList;
    }

    @NonNull
    @Override
    public SeekerJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_job_post_card, parent, false);
        return new SeekerJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeekerJobViewHolder holder, int position) {
        SeekerJobModel job = seekerJobList.get(position);
        holder.bind(job);

        holder.saveBtn.setOnClickListener(v -> {
            if (!SaveSeekerActivity.savedList.contains(job)) {
                SaveSeekerActivity.savedList.add(job);
                Toast.makeText(v.getContext(), "Job Saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(v.getContext(), "Already Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return seekerJobList.size();
    }

    public static class SeekerJobViewHolder extends RecyclerView.ViewHolder {
        private final ImageView employerPfp;
        private final TextView jobTitle;
        private final TextView employerName;
        private final TextView jobLocation;
        private final TextView jobDuration;
        private final TextView timePosted;
        private final ImageView saveBtn;

        public SeekerJobViewHolder(@NonNull View itemView) {
            super(itemView);
            employerPfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            jobTitle = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employerName = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            jobLocation = itemView.findViewById(R.id.item_card_location_placeholder);
            jobDuration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            timePosted = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
            saveBtn = itemView.findViewById(R.id.image_view_report_button_job_post);
        }

        public void bind(SeekerJobModel job) {
            employerPfp.setImageResource(job.getEmployerProfilePicture());
            jobTitle.setText(job.getJobTitle());
            employerName.setText(job.getEmployerName());
            jobLocation.setText(job.getJobLocation());
            jobDuration.setText(job.getJobDuration());
            timePosted.setText(job.getTimePosted());
        }
    }
}
