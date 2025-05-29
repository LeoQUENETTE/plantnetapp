package com.example.plantnetapp.front;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;

import java.util.ArrayList;
import java.util.List;

public class HistoriqueActivity extends AppCompatActivity {
    private HistoryAdapter historyAdapter;
    private CollectionAdapter collectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // on masque la barre système
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_historique);

        // 1) Retour
        ImageButton btnBack = findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> finish());

        // 2) Mock historique
        List<HistoryEntry> allHistory = new ArrayList<>();
        allHistory.add(new HistoryEntry("Spergularia media", "01/04/2025"));
        allHistory.add(new HistoryEntry("Carex sempervirens", "15/05/2025"));
        allHistory.add(new HistoryEntry("Plante X", "30/03/2025"));
        allHistory.add(new HistoryEntry("Plante Y", "28/02/2025"));
        allHistory.add(new HistoryEntry("Plante Z", "20/01/2025"));

        RecyclerView rvAll = findViewById(R.id.rvHistoryAll);
        rvAll.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        historyAdapter = new HistoryAdapter(allHistory, entry -> {
            Intent i = new Intent(HistoriqueActivity.this, DetailActivity.class);
            i.putExtra("plantName", entry.getName());
            startActivity(i);
        });
        rvAll.setAdapter(historyAdapter);

        // 3) Mock collections
        List<PlantCollection> collections = new ArrayList<>();
        collections.add(new PlantCollection(1, "Roses"));
        collections.add(new PlantCollection(2, "Fougères"));
        collections.add(new PlantCollection(3, "Cactées"));
        collections.add(new PlantCollection(4, "Succulentes"));

        RecyclerView rvCols = findViewById(R.id.rvCollections);
        rvCols.setLayoutManager(new LinearLayoutManager(this));
        collectionAdapter = new CollectionAdapter(collections, col -> {
            Intent i = new Intent(HistoriqueActivity.this, CollectionDetailActivity.class);
            i.putExtra("collectionId", col.getId());
            i.putExtra("collectionName", col.getName());
            startActivity(i);
        });
        rvCols.setAdapter(collectionAdapter);

        // 4) Barre de recherche unique
        SearchView sv = findViewById(R.id.svGlobal);
        EditText svText = sv.findViewById(
                androidx.appcompat.R.id.search_src_text
        );
        svText.setTextColor(Color.WHITE);
        svText.setHintTextColor(0x88000000);

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                historyAdapter.getFilter().filter(query);
                collectionAdapter.getFilter().filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                historyAdapter.getFilter().filter(newText);
                collectionAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
}
