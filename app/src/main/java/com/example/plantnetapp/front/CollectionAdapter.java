package com.example.plantnetapp.front;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.PlantCollection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.VH> implements Filterable {

    public interface OnCollectionClickListener {
        void onClick(PlantCollection collection);
    }

    private List<PlantCollection> fullList;
    private List<PlantCollection> filteredList;
    private final OnCollectionClickListener listener;

    public Set<Integer> getCheckedItems() {
        return checkedItems;
    }

    private final Set<Integer> checkedItems = new HashSet<>();

    public CollectionAdapter(List<PlantCollection> list, OnCollectionClickListener listener) {
        this.fullList = new ArrayList<>(list);
        this.filteredList = new ArrayList<>(list);
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_checkbox, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PlantCollection collection = filteredList.get(position);
        holder.name.setText(collection.name);
        holder.checkbox.setOnCheckedChangeListener(null); // évite le recyclage indésirable
        holder.checkbox.setChecked(checkedItems.contains(position));
        holder.checkbox.setOnCheckedChangeListener((b, isChecked) -> {
            if (isChecked) checkedItems.add(position);
            else checkedItems.remove(position);
        });

        holder.itemView.setOnClickListener(v -> listener.onClick(collection));
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView name;

        VH(View view) {
            super(view);
            checkbox = view.findViewById(R.id.checkbox);
            name = view.findViewById(R.id.collectionName);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                List<PlantCollection> result = new ArrayList<>();
                if (query.isEmpty()) {
                    result.addAll(fullList);
                } else {
                    for (PlantCollection c : fullList) {
                        if (c.name.toLowerCase().contains(query)) {
                            result.add(c);
                        }
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = result;
                return fr;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<PlantCollection>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public List<PlantCollection> getCheckedCollections() {
        List<PlantCollection> selected = new ArrayList<>();
        for (Integer i : checkedItems) {
            if (i >= 0 && i < filteredList.size()) {
                selected.add(filteredList.get(i));
            }
        }
        return selected;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateValue(List<PlantCollection> newValues){
        checkedItems.clear();
        if (newValues == null){
            this.fullList = new ArrayList<>();
            this.filteredList = new ArrayList<>();
            notifyDataSetChanged();
            return;
        }
        this.fullList = new ArrayList<>(newValues);
        this.filteredList = new ArrayList<>(newValues);
        notifyDataSetChanged();
    }
}
