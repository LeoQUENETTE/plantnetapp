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
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.PlantCollection;
import com.example.plantnetapp.back.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoriqueActivity extends AppCompatActivity {
    private HistoryAdapter historyAdapter;
    private static User connectedUser;
    private CollectionAdapter collectionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_historique);

        User user = (User) getIntent().getSerializableExtra("connected_user");
        if (user != null){
            connectedUser = user;
        }else{
            finish();
        }

        // 1) Retour
        ImageButton btnBack = findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> finish());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            PlantCollection history = PlantCollection.getHistory(connectedUser.id);
            if (history != null){
                List<Plant> historyPlants = Plant.getAllPlants(history.id);
                runOnUiThread(() -> {
                    createHistory(history,historyPlants);
                });

            }
        });

        // 3) collections
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            List<PlantCollection> collections = PlantCollection.getAllPlantCollection(connectedUser.id);
            runOnUiThread(() -> {
                createCollections(collections);
            });
        });


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

    private void createHistory(PlantCollection history,List<Plant> historyPlants){
        RecyclerView rvAll = findViewById(R.id.rvHistoryAll);
        rvAll.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        historyAdapter = new HistoryAdapter(historyPlants, entry -> {
            Intent i = new Intent(HistoriqueActivity.this, DetailActivity.class);
            i.putExtra("plantName", entry.name);
            i.putExtra("collection", history);
            i.putExtra("user",connectedUser);
            startActivity(i);
        });
        rvAll.setAdapter(historyAdapter);
    }
    private void createCollections(List<PlantCollection> collections){
        RecyclerView rvCols = findViewById(R.id.rvCollections);
        rvCols.setLayoutManager(new LinearLayoutManager(this));
        if (collections != null && !collections.isEmpty()){
            collectionAdapter = new CollectionAdapter(collections, col -> {
                Intent i = new Intent(HistoriqueActivity.this, CollectionDetailActivity.class);
                i.putExtra("collectionId", col.id);
                i.putExtra("collectionName", col.name);
                startActivity(i);
            });
        }
        rvCols.setAdapter(collectionAdapter);
    }
}
