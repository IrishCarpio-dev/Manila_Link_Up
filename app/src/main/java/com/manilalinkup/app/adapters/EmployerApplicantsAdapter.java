package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.models.EmployerApplicantsModel;
import com.manilalinkup.app.R;

import java.util.List;

public class EmployerApplicantsAdapter extends RecyclerView.Adapter<EmployerApplicantsAdapter.EmployerApplicantsViewHolder> {

    List<EmployerApplicantsModel> employerApplicantsModelList;

    public EmployerApplicantsAdapter(List<EmployerApplicantsModel> employerApplicantsModelList) {
        this.employerApplicantsModelList = employerApplicantsModelList;
    }

    @Override
    public int getItemCount() {
        return employerApplicantsModelList.size();
    }

    @NonNull
    @Override
    public EmployerApplicantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_of_applicants, parent, false);
        return new EmployerApplicantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployerApplicantsViewHolder holder, int position) {
        holder.bind(employerApplicantsModelList.get(position));
    }

    static class EmployerApplicantsViewHolder extends RecyclerView.ViewHolder {

        ImageView profilePhoto;
        TextView firstname;
        TextView lastname;
        TextView age;
        TextView location;

        public EmployerApplicantsViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.applicant_profile_photo);
            firstname = itemView.findViewById(R.id.applicant_first_name);
            lastname = itemView.findViewById(R.id.applicant_last_name);
            age = itemView.findViewById(R.id.applicant_age);
            location = itemView.findViewById(R.id.applicant_location);
        }
        public void bind(EmployerApplicantsModel employerApplicantsModel){
            profilePhoto.setImageResource(employerApplicantsModel.getProfilePhoto());
            firstname.setText(employerApplicantsModel.getFirstname());
            lastname.setText(employerApplicantsModel.getLastname());
            age.setText(String.valueOf(employerApplicantsModel.getAge()) + " years old");
            location.setText(employerApplicantsModel.getLocation());

        }
    }
}
