package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RatingsProfileAdapter extends RecyclerView.Adapter<RatingsProfileAdapter.RatingsProfileViewHolder> {

    private List<RatingsProfileModel> ratingsProfileModelList;
    public RatingsProfileAdapter(List<RatingsProfileModel> ratingsProfileModelList) {
        this.ratingsProfileModelList = ratingsProfileModelList;
    }

    @Override
    public int getItemCount() {
        return ratingsProfileModelList.size();
    }

    @NonNull
    @Override
    public RatingsProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratings_card, parent, false);
        return new RatingsProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingsProfileViewHolder holder, int position) {
        holder.bind(ratingsProfileModelList.get(position));
    }

    static class RatingsProfileViewHolder extends RecyclerView.ViewHolder {
        TextView ratingMessage;
        TextView raterName;
        RatingBar ratingScore;

        public RatingsProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingMessage = itemView.findViewById(R.id.item_card_rating_message);
            raterName = itemView.findViewById(R.id.item_card_rater_name);
            ratingScore = itemView.findViewById(R.id.item_card_rating_bar);
        }


        public void bind(RatingsProfileModel ratingsProfileModel){
            ratingMessage.setText(ratingsProfileModel.ratingMessage);
            raterName.setText(ratingsProfileModel.raterName);
            ratingScore.setRating(ratingsProfileModel.ratingScore);

        }
    }
}
