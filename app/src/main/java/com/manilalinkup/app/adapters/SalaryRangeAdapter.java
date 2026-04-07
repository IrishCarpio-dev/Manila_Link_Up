package com.manilalinkup.app.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;

import java.util.List;

public class SalaryRangeAdapter extends RecyclerView.Adapter<SalaryRangeAdapter.ViewHolder> {

    // --- 1. THE DATA MODEL ---
    // This allows us to have a Label (String) and a Value (int) for Firebase
    public static class SalaryOption {
        private String displayText;
        private int value;

        public SalaryOption(String displayText, int value) {
            this.displayText = displayText;
            this.value = value;
        }

        public String getDisplayText() { return displayText; }
        public int getValue() { return value; }
    }

    // --- 2. ADAPTER VARIABLES ---
    private List<SalaryOption> rangeList;
    private int selectedPosition = -1;

    public SalaryRangeAdapter(List<SalaryOption> rangeList) {
        this.rangeList = rangeList;
    }

    // Method to swap list data (Hourly vs Daily vs Monthly)
    public void updateData(List<SalaryOption> newList) {
        this.rangeList = newList;
        this.selectedPosition = -1; // Reset selection when type changes
        notifyDataSetChanged();
    }

    // --- 3. RECYCLERVIEW OVERRIDES ---
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Ensure your row layout file is named exactly "item_salary_range"
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_salary_range, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (rangeList == null) return;

        SalaryOption option = rangeList.get(position);

        // Use the String for the RadioButton label
        holder.radioButton.setText(option.getDisplayText());

        // Handle selection state
        holder.radioButton.setChecked(position == selectedPosition);

        holder.radioButton.setOnClickListener(v -> {
            selectedPosition = holder.getAdapterPosition();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return (rangeList != null) ? rangeList.size() : 0;
    }

    // --- 4. DATA RETRIEVAL FOR FIREBASE ---
    // This returns the integer value you need for your database
    public int getSelectedValue() {
        if (selectedPosition != -1 && rangeList != null) {
            return rangeList.get(selectedPosition).getValue();
        }
        return 0; // Return 0 if nothing is selected
    }

    // --- 5. THE VIEWHOLDER ---
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;

        public ViewHolder(View itemView) {
            super(itemView);
            // Ensure the ID inside item_salary_range.xml is "rbSalaryRangeItem"
            radioButton = itemView.findViewById(R.id.rbSalaryRangeItem);
        }
    }
}