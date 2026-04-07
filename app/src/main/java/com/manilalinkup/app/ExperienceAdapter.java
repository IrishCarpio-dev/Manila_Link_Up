package com.manilalinkup.app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ExperienceAdapter extends RecyclerView.Adapter<ExperienceAdapter.ExperienceViewHolder> {

    private List<ExperienceModel> experienceList;

    public ExperienceAdapter(List<ExperienceModel> experienceList) {
        this.experienceList = experienceList;
    }

    @NonNull
    @Override
    public ExperienceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_experience_row, parent, false);
        return new ExperienceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienceViewHolder holder, int position) {
        ExperienceModel experience = experienceList.get(position);
        holder.tvJobTitle.setText(experience.getTitle());
        holder.tvCompanyName.setText(experience.getCompany());
        holder.tvDuration.setText(experience.getDuration());
    }

    @Override
    public int getItemCount() {
        return experienceList.size();
    }

    public static class ExperienceViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvDuration;

        public ExperienceViewHolder(@NonNull View itemView) {
            super(itemView);
            // These IDs must match the ones in your item_experience_row.xml
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvDuration = itemView.findViewById(R.id.tv_duration);
        }
    }
}