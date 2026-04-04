package com.manilalinkup.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.manilalinkup.app.models.PhotonResponseModel;
import com.manilalinkup.app.R;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {
    private List<PhotonResponseModel.Feature> locations = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(String fullAddress);
    }

    public LocationAdapter(OnItemClickListener listener) { this.listener = listener; }

    public void setList(List<PhotonResponseModel.Feature> newList) {
        this.locations = newList;
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
        PhotonResponseModel.Properties p = locations.get(position).properties;

        StringBuilder sb = new StringBuilder(p.name);
        if (p.city != null) sb.append(", ").append(p.city);
        if (p.country != null) sb.append(", ").append(p.country);
        String fullAddress = sb.toString();

        holder.name.setText(fullAddress);
        holder.itemView.setOnClickListener(v -> listener.onClick(fullAddress));
    }

    @Override public int getItemCount() { return locations.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ViewHolder(View v) { super(v); name = v.findViewById(R.id.location_text); }
    }
}
