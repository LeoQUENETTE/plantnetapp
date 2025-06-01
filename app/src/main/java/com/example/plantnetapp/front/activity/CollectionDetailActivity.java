package com.example.plantnetapp.front.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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
import com.example.plantnetapp.front.adapter.PlantAdapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CollectionDetailActivity extends AppCompatActivity {
    private PlantAdapter adapter;
    private User user;
    private PlantCollection collection;
    private List<Plant> plants;
    private final ActivityResultLauncher<Intent> updateCollectionLauncher =
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
                        getCollectionPlants(this::setUp);
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On cache l'ActionBar système
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_collection_detail);

        User foundUser = (User) getIntent().getSerializableExtra("connected_user");
        PlantCollection foundCollection = (PlantCollection) getIntent().getSerializableExtra("selected_collection");
        if (foundUser == null || foundCollection == null){
            finish();
            return;
        }
        try {
            user = foundUser;
            collection = foundCollection;
            getCollectionPlants(this::setUp);

            ImageButton btnBack = findViewById(R.id.btnBackCollection);
            btnBack.setOnClickListener(v -> finish());

            TextView tv = findViewById(R.id.tvCollectionDetailTitle);
            tv.setText(collection.name);
        }catch (Exception e){
            finish();
        }
    }

    private void setUp(){
        setupSearchView();
        if (plants != null){
            setupRecyclerView();
        }
    }
    private void setupSearchView(){
        SearchView sv = findViewById(R.id.svCollection);
        EditText svText = sv.findViewById(
                androidx.appcompat.R.id.search_src_text
        );
        svText.setTextColor(Color.WHITE);
        svText.setHintTextColor(0x88000000);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                adapter.getFilter().filter(q);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String t) {
                adapter.getFilter().filter(t);
                return false;
            }
        });
    }
    private void setupRecyclerView(){
        try {
            RecyclerView rv = findViewById(R.id.rvPlantsInCollection);
            rv.setLayoutManager(new LinearLayoutManager(this));
            if (adapter == null){
                adapter = new PlantAdapter(plants, plant -> {
                    // Au clic sur une plante, renvoyer sur le détail
                    Intent i = new Intent(CollectionDetailActivity.this, DetailActivity.class);
                    i.putExtra("plantName", plant.name);
                    i.putExtra("collection", collection);
                    i.putExtra("user", user);
                    updateCollectionLauncher.launch(i);
                });
            }
            rv.setAdapter(adapter);
        }catch (Exception e){
            finish();
        }
    }

    private void getCollectionPlants(Runnable onFinish){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            List<Plant> updatedPlants = Plant.getAllPlants(collection.id);
            plants = updatedPlants;
            runOnUiThread(() -> {
                if (adapter != null) {
                    adapter.setPlants(updatedPlants);
                }
                onFinish.run();
            });

            executor.shutdown();
        });
    }
}
