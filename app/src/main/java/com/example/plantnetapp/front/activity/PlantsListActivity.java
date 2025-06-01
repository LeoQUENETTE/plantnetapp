package com.example.plantnetapp.front.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.CsvParser;
import com.example.plantnetapp.front.adapter.PlantAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantsListActivity extends AppCompatActivity {
    private PlantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Masquer la barre d’action
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_plants_list);

        // Bouton Retour
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Configuration du SearchView et de son conteneur flou
        SearchView sv = findViewById(R.id.svSearch);

        // 2) Texte et hint en blanc
        EditText searchEditText = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(Color.WHITE);
        searchEditText.setHintTextColor(Color.parseColor("#88FFFFFF"));

        // 3) Filtrage
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
            @Override public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        // Chargement des données depuis le CSV raw (res/raw/data_174412667780.csv)
        List<Plant> plants = new ArrayList<>(Objects.requireNonNull(CsvParser.createInstance(this)).values());

        // RecyclerView + Adapter
        RecyclerView rv = findViewById(R.id.rvPlants);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlantAdapter(plants, plant -> openDetailFor(plant));
        rv.setAdapter(adapter);
    }

    private void openDetailFor(Plant plant) {
        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra("plant", plant);
        startActivity(i);
    }
}
