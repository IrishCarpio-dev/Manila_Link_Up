package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.manilalinkup.app.R;

public class SavedJobsAdapter extends RecyclerView.Adapter<SavedJobsAdapter.ViewHolder> {

    public SavedJobsAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myactivity_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // No data to bind for now as requested to remove missing models
    }

    @Override
    public int getItemCount() {
        return 0; // Returning 0 as there's no data model
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}