package com.example.plantnetapp.front.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CollectionAdapterCheckbox extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BUTTON = 0;
    private static final int TYPE_ITEM = 1;

    private final List<String> collections;
    private final Set<Integer> checkedItems = new HashSet<>();
    private final Runnable onCreateNewCollection;

    public CollectionAdapterCheckbox(List<String> collections, Runnable onCreateNewCollection) {
        this.collections = collections;
        this.onCreateNewCollection = onCreateNewCollection;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_BUTTON : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_BUTTON) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_collection, parent, false);
            return new ButtonViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection_checkbox, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ButtonViewHolder) {
            ((ButtonViewHolder) holder).button.setOnClickListener(v -> onCreateNewCollection.run());
        } else {
            int actualPosition = position - 1;
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.name.setText(collections.get(actualPosition));
            itemHolder.checkbox.setChecked(checkedItems.contains(actualPosition));
            itemHolder.checkbox.setOnCheckedChangeListener((b, isChecked) -> {
                if (isChecked) checkedItems.add(actualPosition);
                else checkedItems.remove(actualPosition);
            });
        }
    }

    @Override
    public int getItemCount() {
        return collections.size() + 1; // +1 for button
    }

    public Set<Integer> getCheckedItems() {
        return checkedItems;
    }

    static class ButtonViewHolder extends RecyclerView.ViewHolder {
        Button button;
        ButtonViewHolder(View view) {
            super(view);
            button = view.findViewById(R.id.btnNewCollection);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkbox;
        TextView name;
        LinearLayout ll;

        ItemViewHolder(View view) {
            super(view);
            checkbox = view.findViewById(R.id.checkbox);
            name = view.findViewById(R.id.collectionName);
            ll = view.findViewById(R.id.item);
        }
    }
}
