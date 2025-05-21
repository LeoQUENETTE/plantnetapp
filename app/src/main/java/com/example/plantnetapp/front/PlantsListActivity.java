package com.example.plantnetapp.front;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import com.example.plantnetapp.R;

public class PlantsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlantAdapter adapter;
    private List<Plant> plantList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plants_list);

        recyclerView = findViewById(R.id.recyclerViewPlants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation de la liste de plantes avec des données statiques pour la maquette.
        plantList = new ArrayList<>();
        plantList.add(new Plant("Plante A", "Description pour Plante A"));
        plantList.add(new Plant("Plante B", "Description pour Plante B"));
        plantList.add(new Plant("Plante C", "Description pour Plante C"));
        plantList.add(new Plant("Plante D", "Description pour Plante D"));

        adapter = new PlantAdapter(plantList);
        recyclerView.setAdapter(adapter);
    }
}
