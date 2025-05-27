package com.example.plantnetapp.front;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;

import java.util.ArrayList;
import java.util.List;


public class PlantAdapter extends RecyclerView.Adapter<PlantAdapter.VH> implements Filterable {
    private List<Plant> fullList;
    private List<Plant> filteredList;
    private OnPlantClickListener listener;

    public PlantAdapter(List<Plant> plants, OnPlantClickListener listener) {
        this.fullList = new ArrayList<>(plants);
        this.filteredList = plants;
        this.listener = listener;
    }

    @Override public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_plant, parent,false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(VH holder, int pos) {
        Plant p = filteredList.get(pos);
        holder.tvName.setText(p.getName());
        holder.tvDesc.setText(p.getDescription());
        // Glide.with(holder.iv.getContext()).load(p.getImageUrl()).into(holder.iv);
        holder.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override public int getItemCount() {
        return filteredList.size();
    }

    // VueHolder
    static class VH extends RecyclerView.ViewHolder {
        ImageView iv;
        TextView tvName, tvDesc;
        VH(View itemView) {
            super(itemView);
            iv      = itemView.findViewById(R.id.ivPlante);
            tvName  = itemView.findViewById(R.id.tvNomPlante);
            tvDesc  = itemView.findViewById(R.id.tvDescPlante);
        }
    }

    // Interface pour le clic
    public interface OnPlantClickListener {
        void onClick(Plant plant);
    }

    // ----- Filterable -----
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint == null ? "" : constraint.toString().toLowerCase().trim();
                List<Plant> result = new ArrayList<>();
                if (query.isEmpty()) {
                    result = fullList;
                } else {
                    for (Plant p : fullList) {
                        if (p.getName().toLowerCase().contains(query)) {
                            result.add(p);
                        }
                    }
                }
                FilterResults fr = new FilterResults();
                fr.values = result;
                return fr;
            }
            @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<Plant>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
