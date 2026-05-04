package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.manilalinkup.app.utilities.ImageUtils;
import com.manilalinkup.app.utilities.ProfilePhotoCache;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.AppliedJobModel;
import com.manilalinkup.app.models.ArchiveJobModel;
import com.manilalinkup.app.activities.EmployerAddJobActivity;
import com.manilalinkup.app.activities.EmployerViewJobPost;

import android.content.Intent;
import android.widget.Button;
import androidx.core.content.ContextCompat;

import java.util.List;

public class AppliedJobsAdapter extends RecyclerView.Adapter<AppliedJobsAdapter.AppliedViewHolder> {
    public interface OnAppliedJobClickListener {
        void onJobClick(AppliedJobModel job);
    }
    private List<AppliedJobModel> appliedJobs;
    private OnAppliedJobClickListener listener;

    public AppliedJobsAdapter(List<AppliedJobModel> appliedJobs, OnAppliedJobClickListener listener) {
        this.appliedJobs = appliedJobs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppliedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_applied_tab_card, parent, false);
        return new AppliedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppliedViewHolder holder, int position) {
        AppliedJobModel application = appliedJobs.get(position);
        int status = application.getStatus() != null ? application.getStatus() : 1;

        if (application.getJob() != null) {
            holder.jobTitle.setText(application.getJob().getTitle());
            if (application.getJob().getEmployer() != null) {
                holder.employerName.setText(application.getJob().getEmployer().getFullName());
                String employerUid = application.getJob().getEmployer().getUid();
                String embeddedPhoto = application.getJob().getEmployer().getProfilePhoto();
                holder.employerLogo.setTag(employerUid);
                holder.employerLogo.setImageResource(R.drawable.ic_person_placeholder);
                if (employerUid != null) {
                    if (embeddedPhoto != null) {
                        ProfilePhotoCache.getInstance().put(employerUid, embeddedPhoto);
                    }
                    ProfilePhotoCache.getInstance().load(employerUid, base64 -> {
                        if (employerUid.equals(holder.employerLogo.getTag()) && base64 != null) {
                            byte[] photoBytes = ImageUtils.decodeBase64Safe(base64);
                            Glide.with(holder.itemView.getContext())
                                    .load(photoBytes)
                                    .circleCrop()
                                    .into(holder.employerLogo);
                        }
                    });
                } else if (embeddedPhoto != null) {
                    byte[] photoBytes = ImageUtils.decodeBase64Safe(embeddedPhoto);
                    Glide.with(holder.itemView.getContext())
                            .load(photoBytes)
                            .circleCrop()
                            .into(holder.employerLogo);
                }
            }
        }

        switch (status) {
            case 1:
                holder.statusBadge.setText("Pending");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_pending);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#E65100"));
                break;
            case 2:
                holder.statusBadge.setText("Interview");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_interview);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#1565C0"));
                break;
            case 3:
                holder.statusBadge.setText("Rejected");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_rejected);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#B71C1C"));
                break;
            case 5:
                holder.statusBadge.setText("Hired");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            case 6:
                holder.statusBadge.setText("Completed");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_accepted);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#2E7D32"));
                break;
            default:
                holder.statusBadge.setText("Pending");
                holder.statusBadge.setBackgroundResource(R.drawable.bg_status_pending);
                holder.statusBadge.setTextColor(android.graphics.Color.parseColor("#E65100"));
                break;
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onJobClick(application);
            }
        });
    }

    @Override
    public int getItemCount() { return appliedJobs.size(); }

    static class AppliedViewHolder extends RecyclerView.ViewHolder {
        ImageView employerLogo;
        TextView jobTitle, employerName, statusBadge;

        public AppliedViewHolder(@NonNull View itemView) {
            super(itemView);
            employerLogo = itemView.findViewById(R.id.applied_employer_logo);
            jobTitle = itemView.findViewById(R.id.applied_job_title);
            employerName = itemView.findViewById(R.id.applied_employer_name);
            statusBadge = itemView.findViewById(R.id.applied_status_badge);
        }
    }

    public static class ArchiveJobAdapter extends RecyclerView.Adapter<ArchiveJobAdapter.ArchiveJobViewHolder> {

        private List<ArchiveJobModel> archiveJobModelList;

        public ArchiveJobAdapter(List<ArchiveJobModel> archiveJobModelList) {
            this.archiveJobModelList = archiveJobModelList;
        }

        @Override
        public int getItemCount() {
            return archiveJobModelList.size();
        }

        @NonNull
        @Override
        public ArchiveJobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer_view_archived_jobs, parent, false);
            return new ArchiveJobViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ArchiveJobViewHolder holder, int position) {
            ArchiveJobModel jobAtBindingTime = archiveJobModelList.get(position);

            holder.bind(jobAtBindingTime);

            holder.itemView.setOnClickListener(v -> {
                int actualPos = holder.getBindingAdapterPosition();
                if (actualPos == RecyclerView.NO_POSITION) return;

                ArchiveJobModel currentJob = archiveJobModelList.get(actualPos);
                Intent intent = new Intent(v.getContext(), EmployerViewJobPost.class);
                intent.putExtra("JOB_ID", currentJob.getJobId());
                intent.putExtra("JOB_TITLE", currentJob.getJobTitle());
                intent.putExtra("DESCRIPTION", currentJob.getDescription());
                intent.putExtra("LOCATION", currentJob.getLocation());
                intent.putExtra("DURATION", currentJob.getDuration());
                if (currentJob.getSalary() != null) {
                    intent.putExtra("SALARY", currentJob.getSalary());
                }
                if (currentJob.getTags() != null) {
                    intent.putStringArrayListExtra("TAG_IDS", new java.util.ArrayList<>(currentJob.getTags()));
                }
                if (currentJob.getApplicationId() != null) {
                    intent.putExtra("APPLICATION_ID", currentJob.getApplicationId());
                }
                Integer appStatus = currentJob.getApplicationStatus();
                if (appStatus != null) {
                    intent.putExtra("STATUS", appStatus);
                }
                intent.putExtra("EMPLOYER_HAS_COMPLETED", currentJob.isEmployerCompleted());
                intent.putExtra("SEEKER_NAME", currentJob.getSeekerName());
                intent.putExtra("IS_RATE_ENABLED", Boolean.TRUE.equals(currentJob.isRateEnabled()));
                intent.putExtra("IS_OWNER", true);
                intent.putExtra("IS_ARCHIVED", true);
                v.getContext().startActivity(intent);
            });

            holder.repostButton.setOnClickListener(v -> {
                int actualPos = holder.getBindingAdapterPosition();
                if (actualPos == RecyclerView.NO_POSITION) return;

                ArchiveJobModel currentJob = archiveJobModelList.get(actualPos);
                Intent intent = new Intent(v.getContext(), EmployerAddJobActivity.class);
                intent.putExtra("is_repost", true);
                intent.putExtra("repost_title", currentJob.getJobTitle());
                intent.putExtra("repost_description", currentJob.getDescription());
                intent.putExtra("repost_location", currentJob.getLocation());
                if (currentJob.getSalary() != null) {
                    intent.putExtra("repost_salary", currentJob.getSalary().toString());
                }
                intent.putExtra("repost_duration", currentJob.getDuration());
                if (currentJob.getTags() != null) {
                    intent.putStringArrayListExtra("repost_tag_ids", new java.util.ArrayList<>(currentJob.getTags()));
                }
                v.getContext().startActivity(intent);
            });
        }

        static class ArchiveJobViewHolder extends RecyclerView.ViewHolder {
            TextView job_title;
            TextView statusText;
            TextView insightText;
            Button repostButton;

            public ArchiveJobViewHolder(@NonNull View itemView) {
                super(itemView);
                job_title = itemView.findViewById(R.id.text_view_archived_title);
                statusText = itemView.findViewById(R.id.text_view_status_badge);
                insightText = itemView.findViewById(R.id.text_view_insights_text);
                repostButton = itemView.findViewById(R.id.button_repost_archived);
            }

            public void bind(ArchiveJobModel archiveJobBind) {
                job_title.setText(archiveJobBind.getJobTitle());
                String status = archiveJobBind.getStatusText();
                statusText.setText(status);
                insightText.setText(archiveJobBind.getInsightText());
                switch (status) {
                    case "EXPIRED":
                        statusText.setBackgroundResource(R.drawable.bg_expired_red_pill);
                        statusText.setTextColor(android.graphics.Color.parseColor("#B71C1C"));
                        break;
                    case "COMPLETED":
                        statusText.setBackgroundResource(R.drawable.bg_completed_pill);
                        statusText.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.manila_blue));
                        break;
                    default:
                        statusText.setBackgroundResource(R.drawable.bg_expire_pill);
                        statusText.setTextColor(android.graphics.Color.parseColor("#E65100"));
                        break;
                }
            }
        }
    }
}
