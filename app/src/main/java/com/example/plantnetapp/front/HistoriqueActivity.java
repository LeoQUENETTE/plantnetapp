package com.example.plantnetapp.front;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;

import java.util.ArrayList;
import java.util.List;

public class HistoriqueActivity extends AppCompatActivity {
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_historique);

        // Retour
        Button btnBack = findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> finish());

        // Mock de données
        List<HistoryEntry> data = new ArrayList<>();
        data.add(new HistoryEntry("Spergularia media", "01/04/2025"));
        data.add(new HistoryEntry("Plante X",           "30/03/2025"));
        // …

        // RecyclerView + Adapter
        RecyclerView rv = findViewById(R.id.rvHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(data, entry -> {
            Intent i = new Intent(HistoriqueActivity.this, DetailActivity.class);
            i.putExtra("plantName", entry.getName());
            i.putExtra("plantDesc", entry.getName());
            startActivity(i);
        });
        rv.setAdapter(adapter);

        // SearchView — **plus de blur ici**
        SearchView sv = findViewById(R.id.svHistory);
        EditText et = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        et.setTextColor(0xFFFFFFFF);
        et.setHintTextColor(0x88FFFFFF);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) {
                adapter.getFilter().filter(q);
                return false;
            }
            @Override public boolean onQueryTextChange(String t) {
                adapter.getFilter().filter(t);
                return false;
            }
        });
    }
}
