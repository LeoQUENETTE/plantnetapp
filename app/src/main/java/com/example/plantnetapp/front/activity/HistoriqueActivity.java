package com.example.plantnetapp.front.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.PlantCollection;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.PlantCollectionTable;
import com.example.plantnetapp.back.tables.Table;
import com.example.plantnetapp.front.adapter.CollectionAdapter;
import com.example.plantnetapp.front.adapter.HistoryAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoriqueActivity extends AppCompatActivity {
    private HistoryAdapter historyAdapter;
    private static User connectedUser;
    private CollectionAdapter collectionAdapter;
    private PlantCollection history;
    private List<Plant> historyPlants;
    private boolean isconnected;
    private PlantCollectionTable collectionTable;
    private final ActivityResultLauncher<Intent> updateHistoryAdapter =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() != RESULT_OK) {
                return;
            }
            Intent data = result.getData();
            if (data == null){
                return;
            }
            Boolean updateCollection = data.getBooleanExtra("updateCollection", false);
            if (!updateCollection){
                return;
            }
            getHistoryPlants(this::createHistory);
        });
    private final ActivityResultLauncher<Intent> updateCollectionAdapter =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK) {
                            return;
                        }
                        Intent data = result.getData();
                        if (data == null){
                            return;
                        }
                        Boolean updateCollection = data.getBooleanExtra("updateCollection", false);
                        if (!updateCollection){
                            return;
                        }
                        getCollectionPlantCollections(this::createCollections);
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_historique);
        User user = (User) getIntent().getSerializableExtra("connected_user");
        collectionTable = PlantCollectionTable.getInstance();
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isconnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
        if (user != null){
            connectedUser = user;
        }else{
            finish();
        }
        Button btnBack = findViewById(R.id.btnBackHistory);
        btnBack.setOnClickListener(v -> finish());

        setupSearchView();
        launchRunnableInBackground(this::getHistory);
        launchRunnableInBackground(this::createCollections);
    }

    private void getHistory() {
        history = PlantCollection.getHistory(connectedUser.id);
        historyPlants = null;
        if (history != null){
            try {
                historyPlants = Plant.getAllPlants(history.id);
            }catch (Exception e){
                return;
            }
            if (historyPlants == null){
                return;
            }
            List<Plant> finalHistoryPlants = historyPlants;
            runOnUiThread(() -> {
                if (historyPlants != null && history != null){
                    createHistory();
                }

            });

        }
    }

    private void setupSearchView(){
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

    private void createHistory(){
        RecyclerView rvAll = findViewById(R.id.rvHistoryAll);
        rvAll.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        if (historyAdapter == null){
            historyAdapter = new HistoryAdapter(historyPlants, entry -> {
                Intent i = new Intent(HistoriqueActivity.this, DetailActivity.class);
                i.putExtra("plantName", entry.name);
                i.putExtra("noCollection", true);
                i.putExtra("user",connectedUser);
                updateHistoryAdapter.launch(i);
            });
        }
        rvAll.setAdapter(historyAdapter);
    }
    private void launchRunnableInBackground(Runnable func){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(func);
        executor.shutdown();
    }
    private void createCollections(){
        List<PlantCollection> collections;
        if (isconnected){
            collections = PlantCollection.getCollectionsNoHistory(connectedUser.id);
        }else{
            try {
                collections = collectionTable.selectAllWithoutHistory(connectedUser.id);
            }catch (Table.EmptyTableException exception){
                collections =null;
            }
        }
        List<PlantCollection> finalCollections = collections;
        runOnUiThread(() -> {
            RecyclerView rvCols = findViewById(R.id.rvCollections);
            rvCols.setLayoutManager(new LinearLayoutManager(this));
            if (finalCollections != null && !finalCollections.isEmpty() && collectionAdapter == null){
                collectionAdapter = new CollectionAdapter(finalCollections, col -> {
                    Intent i = new Intent(HistoriqueActivity.this, CollectionDetailActivity.class);
                    i.putExtra("selected_collection", col);
                    i.putExtra("connected_user", connectedUser);
                    updateCollectionAdapter.launch(i);
                });
            } else if (collectionAdapter != null) {
                collectionAdapter.updateValue(finalCollections);
            }
            rvCols.setAdapter(collectionAdapter);
            Button btnDelete = findViewById(R.id.btnDelete);
            btnDelete.setOnClickListener(v -> {
                deletePlantCollections(finalCollections);
            });
        });
    }
    private void deletePlantCollections(List<PlantCollection> collections) {
        if (collections == null || connectedUser == null) {
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(
                Math.max(1, Runtime.getRuntime().availableProcessors() - 1)
        );

        List<PlantCollection> selectedCollections = collectionAdapter.getCheckedCollections();
        for (PlantCollection c : selectedCollections) {
            executor.submit(() -> {
                try {
                    if (isconnected){
                        PlantCollection.deleteCollection(connectedUser.id, c.name);
                    }
                    collectionTable.deleteCollection(connectedUser.id, c.name);
                } catch (Exception e) {
                    Log.e("DeleteError", "Failed to delete collection", e);
                }
            });
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, java.util.concurrent.TimeUnit.SECONDS)) {
                Log.d("ERROR", "Timeout when waiting for deletion tasks.");
            }
        } catch (InterruptedException e) {
            Log.d("ERROR", "Interrupted while waiting for deletion.");
        }
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                List<PlantCollection> updatedCollections =
                        PlantCollection.getCollectionsNoHistory(connectedUser.id);

                runOnUiThread(() -> {
                    if (collectionAdapter != null) {
                        collectionAdapter.updateValue(updatedCollections);
                    }
                });
            } catch (Exception e) {
                Log.e("RefreshError", "Failed to refresh collections", e);
            }
        });
    }
    private void getHistoryPlants(Runnable onFinish){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            List<Plant> updatedPlants = Plant.getAllPlants(history.id);
            historyPlants = updatedPlants;
            runOnUiThread(() -> {
                if (historyAdapter != null) {
                    historyAdapter.setPlants(updatedPlants);
                }
                onFinish.run();
            });
            executor.shutdown();
        });
    }
    private void getCollectionPlantCollections(Runnable onFinish){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                onFinish.run();
            }catch (Exception e){
                return;
            }
        });
        executor.shutdown();
    }
}
