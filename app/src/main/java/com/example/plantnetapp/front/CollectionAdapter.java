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

public class CollectionAdapter
        extends RecyclerView.Adapter<CollectionAdapter.VH>
        implements Filterable {

    public interface OnCollectionClickListener {
        void onClick(PlantCollection collection);
    }

    private final List<PlantCollection> fullList;
    private List<PlantCollection> filteredList;
    private final OnCollectionClickListener listener;

    public CollectionAdapter(List<PlantCollection> list,
                             OnCollectionClickListener listener) {
        this.fullList     = new ArrayList<>(list);
        this.filteredList = list;
        this.listener     = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        PlantCollection c = filteredList.get(pos);
        holder.tvName.setText(c.getName());
        holder.itemView.setOnClickListener(v -> listener.onClick(c));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName;
        VH(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCollectionName);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                String q = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                List<PlantCollection> result = new ArrayList<>();
                if (q.isEmpty()) {
                    result.addAll(fullList);
                } else {
                    for (PlantCollection c : fullList) {
                        if (c.getName().toLowerCase().contains(q)) {
                            result.add(c);
                        }
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = result;
                return fr;
            }
            @Override @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<PlantCollection>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
