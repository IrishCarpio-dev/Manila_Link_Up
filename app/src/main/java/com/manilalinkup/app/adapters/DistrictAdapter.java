package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class DistrictAdapter extends RecyclerView.Adapter<DistrictAdapter.ViewHolder> {
    private List<String> data = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(String value);
    }

    public DistrictAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateList(List<String> newList) {
        this.data = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = data.get(position);
        holder.textView.setText(item);
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override public int getItemCount() { return data.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ViewHolder(View v) { super(v); textView = v.findViewById(R.id.location_text); }
    }
}
