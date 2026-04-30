package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Base64;
import com.manilalinkup.app.utilities.ImageUtils;

import com.bumptech.glide.Glide;
import com.manilalinkup.app.R;
import com.manilalinkup.app.models.RatingModel;

import java.util.List;

public class EmployerAllRatingsAdapter extends RecyclerView.Adapter<EmployerAllRatingsAdapter.ViewHolder> {

    private final List<RatingModel> ratingsList;

    public EmployerAllRatingsAdapter(List<RatingModel> ratingsList) {
        this.ratingsList = ratingsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_all_ratings, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RatingModel rating = ratingsList.get(position);

        RatingModel.RaterInfo rater = rating.getRater();
        if (rater != null) {
            String name = rater.getName() != null ? rater.getName() : "";
            String[] parts = name.split(" ", 2);
            holder.firstname.setText(parts.length > 0 ? parts[0] : "");
            holder.lastname.setText(parts.length > 1 ? parts[1] : "");
            if (rater.getProfilePhoto() != null) {
                byte[] photoBytes = ImageUtils.decodeBase64Safe(rater.getProfilePhoto());
                Glide.with(holder.itemView.getContext())
                        .load(photoBytes)
                        .placeholder(R.drawable.ic_person_placeholder)
                        .circleCrop()
                        .into(holder.imageProfile);
            } else {
                holder.imageProfile.setImageResource(R.drawable.ic_person_placeholder);
            }
        }

        holder.ratingMessage.setText(rating.getComment() != null ? rating.getComment() : "");
        holder.ratingBar.setRating(rating.getScore());

        RatingModel.JobInfo job = rating.getJob();
        if (holder.jobTitle != null && job != null) {
            holder.jobTitle.setText(job.getTitle() != null ? job.getTitle() : "");
        }

        holder.timestamp.setText(rating.getCreatedAt() != null ? rating.getCreatedAt() : "");
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView firstname, lastname, ratingMessage, timestamp, jobTitle;
        RatingBar ratingBar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.applicant_profile_photo);
            firstname = itemView.findViewById(R.id.rater_firstname);
            lastname = itemView.findViewById(R.id.applicant_last_name);
            ratingMessage = itemView.findViewById(R.id.item_card_rating_message);
            timestamp = itemView.findViewById(R.id.item_rating_timestamp);
            ratingBar = itemView.findViewById(R.id.item_card_rating_bar);
            jobTitle = itemView.findViewById(R.id.item_rating_job_title);
        }
    }
}
