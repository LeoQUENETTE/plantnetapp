package com.example.plantnetapp.front.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.Plant;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.VH>
        implements Filterable {

    public interface OnHistoryClickListener {
        void onItemClick(Plant entry);
    }

    private List<Plant> fullList;
    private List<Plant> filteredList;
    private final OnHistoryClickListener listener;

    // ← On ajoute bien le listener ici
    public HistoryAdapter(List<Plant> list, OnHistoryClickListener listener) {
        this.fullList     = new ArrayList<>(list);
        this.filteredList = new ArrayList<>(list);
        this.listener     = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_entry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        Plant e = filteredList.get(pos);
        holder.tvName.setText(e.name);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(e));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvDate;
        VH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvHistoryName);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                List<Plant> result = new ArrayList<>();
                if (query.isEmpty()) {
                    result.addAll(fullList);
                } else {
                    for (Plant e : fullList) {
                        if (e.name.toLowerCase().contains(query)) {
                            result.add(e);
                        }
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = result;
                return fr;
            }
            @SuppressLint("NotifyDataSetChanged")
            @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                filteredList = (List<Plant>) results.values;
                notifyDataSetChanged();
            }
        };
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setPlants(List<Plant> newPlants) {
        if (newPlants == null){
            fullList = new ArrayList<>();
            filteredList = new ArrayList<>();
            notifyDataSetChanged();
            return;
        }
        fullList = newPlants;
        filteredList = newPlants;
        notifyDataSetChanged();
    }
}
