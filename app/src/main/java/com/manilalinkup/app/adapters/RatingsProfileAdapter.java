package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;
import com.manilalinkup.app.models.RatingModel;

import java.util.List;

public class RatingsProfileAdapter extends RecyclerView.Adapter<RatingsProfileAdapter.RatingsProfileViewHolder> {

    private final List<RatingModel> ratingsList;

    public RatingsProfileAdapter(List<RatingModel> ratingsList) {
        this.ratingsList = ratingsList;
    }

    @Override
    public int getItemCount() { return ratingsList.size(); }

    @NonNull
    @Override
    public RatingsProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ratings_card, parent, false);
        return new RatingsProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingsProfileViewHolder holder, int position) {
        holder.bind(ratingsList.get(position));
    }

    static class RatingsProfileViewHolder extends RecyclerView.ViewHolder {
        TextView ratingMessage;
        TextView raterName;
        RatingBar ratingScore;

        RatingsProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingMessage = itemView.findViewById(R.id.item_card_rating_message);
            raterName     = itemView.findViewById(R.id.item_card_rater_name);
            ratingScore   = itemView.findViewById(R.id.item_card_rating_bar);
        }

        void bind(RatingModel rating) {
            String comment = rating.getComment();
            if (comment != null && !comment.isEmpty()) {
                ratingMessage.setVisibility(View.VISIBLE);
                ratingMessage.setText(comment);
            } else {
                ratingMessage.setVisibility(View.GONE);
            }
            ratingScore.setRating(rating.getScore());

            String name = "";
            if (rating.getRater() != null && rating.getRater().getName() != null) {
                name = rating.getRater().getName();
            }
            raterName.setText(name);
        }
    }
}
