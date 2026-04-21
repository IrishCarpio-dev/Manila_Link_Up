package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.ApplicantModel;
import com.manilalinkup.app.models.SeekerProfileModel;

import java.util.List;

public class EmployerApplicantsAdapter extends RecyclerView.Adapter<EmployerApplicantsAdapter.ViewHolder> {

    public interface OnActionListener {
        void onInterview(ApplicantModel applicant);
        void onHire(ApplicantModel applicant);
        void onOpenChat(ApplicantModel applicant);
    }

    private final List<ApplicantModel> applicants;
    private final OnActionListener listener;

    public EmployerApplicantsAdapter(List<ApplicantModel> applicants, OnActionListener listener) {
        this.applicants = applicants;
        this.listener = listener;
    }

    @Override
    public int getItemCount() { return applicants.size(); }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_of_applicants, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(applicants.get(position), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePhoto;
        TextView firstName, lastName, location, statusChip;
        Button btnInterview, btnHire, btnChat;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.applicant_profile_photo);
            firstName    = itemView.findViewById(R.id.applicant_first_name);
            lastName     = itemView.findViewById(R.id.applicant_last_name);
            location     = itemView.findViewById(R.id.applicant_location);
            statusChip   = itemView.findViewById(R.id.applicant_status_chip);
            btnInterview = itemView.findViewById(R.id.btn_interview);
            btnHire      = itemView.findViewById(R.id.btn_hire);
            btnChat      = itemView.findViewById(R.id.btn_open_chat);
        }

        void bind(ApplicantModel applicant, OnActionListener listener) {
            SeekerProfileModel seeker = applicant.getSeeker();
            if (seeker != null) {
                firstName.setText(seeker.getFirstName() != null ? seeker.getFirstName() : "");
                lastName.setText(seeker.getLastName() != null ? seeker.getLastName() : "");
                location.setText(seeker.getLocation() != null ? seeker.getLocation() : "");

                if (seeker.getProfilePhotoUrl() != null) {
                    Glide.with(itemView.getContext())
                            .load(seeker.getProfilePhotoUrl())
                            .placeholder(R.drawable.ic_person_placeholder)
                            .circleCrop()
                            .into(profilePhoto);
                } else {
                    profilePhoto.setImageResource(R.drawable.ic_person_placeholder);
                }
            }

            int status = applicant.getStatus() != null ? applicant.getStatus() : 1;
            if (statusChip != null) {
                switch (status) {
                    case 1: statusChip.setText("Pending");   statusChip.setBackgroundResource(R.drawable.bg_status_pending);   break;
                    case 2: statusChip.setText("Interview"); statusChip.setBackgroundResource(R.drawable.bg_status_interview); break;
                    case 3: statusChip.setText("Rejected");  statusChip.setBackgroundResource(R.drawable.bg_status_rejected);  break;
                    case 5: statusChip.setText("Hired");     statusChip.setBackgroundResource(R.drawable.bg_status_accepted);  break;
                    case 6: statusChip.setText("Completed"); statusChip.setBackgroundResource(R.drawable.bg_status_accepted);  break;
                    default: statusChip.setText("—"); break;
                }
            }

            // Show Interview button only for pending applicants
            if (btnInterview != null) {
                btnInterview.setVisibility(status == 1 ? View.VISIBLE : View.GONE);
                btnInterview.setOnClickListener(v -> { if (listener != null) listener.onInterview(applicant); });
            }

            // Show Hire button for pending or interview stage
            if (btnHire != null) {
                btnHire.setVisibility((status == 1 || status == 2) ? View.VISIBLE : View.GONE);
                btnHire.setOnClickListener(v -> { if (listener != null) listener.onHire(applicant); });
            }

            // Show chat button when chat exists (status >= 2)
            if (btnChat != null) {
                btnChat.setVisibility((status >= 2 && applicant.getChatId() != null) ? View.VISIBLE : View.GONE);
                btnChat.setOnClickListener(v -> { if (listener != null) listener.onOpenChat(applicant); });
            }
        }
    }
}
