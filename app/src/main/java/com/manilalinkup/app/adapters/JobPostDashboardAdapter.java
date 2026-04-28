package com.manilalinkup.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.manilalinkup.app.models.JobPostDashboardModel;
import com.manilalinkup.app.R;

import java.util.Collections;
import java.util.List;
import java.util.Map;
public class JobPostDashboardAdapter extends RecyclerView.Adapter<JobPostDashboardAdapter.JobPostDashboardViewHolder> {
    public interface OnJobClickListener {
        void onJobClick(JobPostDashboardModel job);
        void onRemoveClick(JobPostDashboardModel job, int position);
    }
    private final List<JobPostDashboardModel> jobPostDashboardModelList;
    OnJobClickListener listener;
    boolean isProfileView;
    private boolean showOptionsMenu = false;
    private Map<String, String> tagLabelsById = Collections.emptyMap();

    public JobPostDashboardAdapter(List<JobPostDashboardModel> jobPostDashboardModelList, boolean isProfileView, OnJobClickListener listener) {
        this.jobPostDashboardModelList = jobPostDashboardModelList;
        this.isProfileView = isProfileView;
        this.listener = listener;
    }

    public void setShowOptionsMenu(boolean showOptionsMenu) {
        this.showOptionsMenu = showOptionsMenu;
    }

    public void setTagLabelsById(Map<String, String> tagLabelsById) {
        this.tagLabelsById = tagLabelsById != null ? tagLabelsById : Collections.emptyMap();
        notifyDataSetChanged();
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
        holder.bind(currentJob, listener, showOptionsMenu, tagLabelsById);
    }

    static class JobPostDashboardViewHolder extends RecyclerView.ViewHolder {
        private ImageView employer_pfp;
        private TextView job_title;
        private TextView employer_name;
        private TextView job_location;
        private TextView job_duration;
        private TextView how_long_job_was_posted;
        private TextView tvStatusBadge;
        private ImageView optionsButton;
        private ChipGroup chipGroupTags;
        public JobPostDashboardViewHolder(@NonNull View itemView) {
            super(itemView);
            employer_pfp = itemView.findViewById(R.id.item_card_employer_profile_picture_placeholder);
            job_title = itemView.findViewById(R.id.item_card_job_title_placeholder);
            employer_name = itemView.findViewById(R.id.item_card_employer_name_placeholder);
            job_location = itemView.findViewById(R.id.item_card_location_placeholder);
            job_duration = itemView.findViewById(R.id.item_card_calendar_placeholder);
            how_long_job_was_posted = itemView.findViewById(R.id.item_card_how_long_job_post_posted_placeholder);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            optionsButton = itemView.findViewById(R.id.image_view_job_card_options);
            chipGroupTags = itemView.findViewById(R.id.chip_group_job_card_tags);
        }

        public void bind(JobPostDashboardModel jobBind, OnJobClickListener listener, boolean showOptionsMenu, Map<String, String> tagLabelsById){

            if (employer_pfp != null) {
                Glide.with(itemView.getContext())
                        .load(jobBind.getEmployerProfilePicture())
                        .override(50, 50)
                        .placeholder(R.drawable.user_placeholder)
                        .circleCrop()
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

            if (tvStatusBadge != null) {
                Integer appStatus = jobBind.getApplicationStatus();
                if (appStatus != null && appStatus == 5 && !jobBind.isEmployerHasCompleted()) {
                    tvStatusBadge.setText("Hired");
                    tvStatusBadge.setVisibility(View.VISIBLE);
                } else {
                    tvStatusBadge.setVisibility(View.GONE);
                }
            }

            if (optionsButton != null) {
                optionsButton.setVisibility(showOptionsMenu ? View.VISIBLE : View.GONE);
                optionsButton.setOnClickListener(v -> {
                    int currentPosition = getAbsoluteAdapterPosition();
                    if (listener == null || currentPosition == RecyclerView.NO_POSITION) return;

                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.menu_job_post_card_options, popup.getMenu());
                    popup.setOnMenuItemClickListener(item -> {
                        if (item.getItemId() == R.id.menu_archive_job) {
                            listener.onRemoveClick(jobBind, currentPosition);
                            return true;
                        }
                        return false;
                    });
                    popup.show();
                });
            }

            if (chipGroupTags != null) {
                chipGroupTags.removeAllViews();
                List<String> tagIds = jobBind.getTagIds();
                if (tagIds != null) {
                    for (String tagId : tagIds) {
                        String label = tagLabelsById.get(tagId);
                        if (label == null) continue;
                        Chip chip = new Chip(chipGroupTags.getContext());
                        chip.setText(label);
                        chip.setChipBackgroundColorResource(R.color.manila_blue);
                        chip.setTextColor(Color.WHITE);
                        chip.setCheckable(false);
                        chip.setClickable(false);
                        chip.setFocusable(false);
                        chip.setEnsureMinTouchTargetSize(false);
                        chipGroupTags.addView(chip);
                    }
                }
            }

            itemView.setOnClickListener(v -> {
                if(listener != null){
                    listener.onJobClick(jobBind);
                }
            });

        }
    }
}
