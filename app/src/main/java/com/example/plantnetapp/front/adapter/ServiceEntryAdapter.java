package com.example.plantnetapp.front.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.plantnetapp.R;
import com.example.plantnetapp.front.entry.ServiceEntry;

import java.util.List;

public class ServiceEntryAdapter
        extends RecyclerView.Adapter<ServiceEntryAdapter.VH> {

    private final List<ServiceEntry> entries;

    public ServiceEntryAdapter(List<ServiceEntry> entries) {
        this.entries = entries;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service_entry, parent, false);
        return new VH(v);
    }

    @SuppressLint("SetTextI18n")
    @Override public void onBindViewHolder(@NonNull VH holder, int pos) {
        ServiceEntry e = entries.get(pos);
        holder.tvService   .setText(e.getService());
        holder.tvValue     .setText(e.getValue());
        holder.tvReliability.setText(e.getReliability() + "%");
        holder.tvCultural  .setText(e.getCulturalCondition());
    }

    @Override public int getItemCount() { return entries.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvService, tvValue, tvReliability, tvCultural;
        VH(View itemView) {
            super(itemView);
            tvService    = itemView.findViewById(R.id.tvService);
            tvValue      = itemView.findViewById(R.id.tvValue);
            tvReliability= itemView.findViewById(R.id.tvReliability);
            tvCultural   = itemView.findViewById(R.id.tvCultural);
        }
    }
}
