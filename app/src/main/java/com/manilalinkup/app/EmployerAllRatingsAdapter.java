package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Make sure to add Glide to your build.gradle
import java.util.List;

public class EmployerAllRatingsAdapter extends RecyclerView.Adapter<EmployerAllRatingsAdapter.ViewHolder> {

    private List<EmployerAllRatingsModel> ratingsList;

    public EmployerAllRatingsAdapter(List<EmployerAllRatingsModel> ratingsList) {
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
        EmployerAllRatingsModel rating = ratingsList.get(position);

        holder.firstname.setText(rating.getFirstname());
        holder.lastname.setText(rating.getLastname());

        holder.ratingMessage.setText(rating.getRatingMessage());
        holder.timestamp.setText(rating.getFormattedDate());
        holder.ratingBar.setRating(rating.getRatingScore());

        // Load Profile Image using Glide
        Glide.with(holder.itemView.getContext())
                .load(rating.getProfilePhoto())
                .placeholder(R.drawable.user_placeholder)
                .circleCrop()
                .into(holder.imageProfile);
    }

    @Override
    public int getItemCount() {
        return ratingsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile;
        TextView firstname, lastname, ratingMessage, timestamp;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.applicant_profile_photo);
            firstname = itemView.findViewById(R.id.rater_firstname);
            lastname = itemView.findViewById(R.id.applicant_last_name);
            ratingMessage = itemView.findViewById(R.id.item_card_rating_message);
            timestamp = itemView.findViewById(R.id.item_rating_timestamp);
            ratingBar = itemView.findViewById(R.id.item_card_rating_bar);
        }
    }
}