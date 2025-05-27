package com.example.plantnetapp.front;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter
        extends RecyclerView.Adapter<HistoryAdapter.VH>
        implements Filterable {

    private final List<HistoryEntry> fullList;
    private List<HistoryEntry> filteredList;

    public HistoryAdapter(List<HistoryEntry> list) {
        this.fullList     = new ArrayList<>(list);
        this.filteredList = list;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_entry, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        HistoryEntry e = filteredList.get(pos);
        holder.tvName .setText(e.getName());
        holder.tvDate .setText(e.getDate());
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
                List<HistoryEntry> result = new ArrayList<>();
                if (query.isEmpty()) {
                    result = fullList;
                } else {
                    for (HistoryEntry e : fullList) {
                        if (e.getName().toLowerCase().contains(query)
                                || e.getDate().toLowerCase().contains(query)) {
                            result.add(e);
                        }
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = result;
                return fr;
            }
            @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<HistoryEntry>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
