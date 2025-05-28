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

public class CollectionDetailActivity extends AppCompatActivity {
    private PlantAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // On cache l'ActionBar système
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_collection_detail);

        // 1) Bouton Retour
        ImageButton btnBack = findViewById(R.id.btnBackCollection);
        btnBack.setOnClickListener(v -> finish());

        // 2) Récupère le nom/id de la collection (passés par Intent)
        String collectionName = getIntent().getStringExtra("collectionName");
        findViewById(R.id.tvCollectionDetailTitle)
                .setTag(collectionName); // si tu veux l'afficher ailleurs

        // 3) Mock de la liste des plantes de la collection
        List<Plant> plants = new ArrayList<>();
        plants.add(new Plant("Rose", "Belle fleur rouge", "url_rose.png"));
        plants.add(new Plant("Tulipe", "Fleur du printemps", "url_tulipe.png"));
        plants.add(new Plant("Orchidée", "Fleur exotique", "url_orchidee.png"));
        // … ajoute autant que tu veux

        // 4) Setup RecyclerView
        RecyclerView rv = findViewById(R.id.rvPlantsInCollection);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PlantAdapter(plants, plant -> {
            // Au clic sur une plante, renvoyer sur le détail
            Intent i = new Intent(CollectionDetailActivity.this, DetailActivity.class);
            i.putExtra("plant", plant);
            startActivity(i);
        });
        rv.setAdapter(adapter);

        // 5) Barre de recherche filtrant la liste des plantes
        SearchView sv = findViewById(R.id.svCollection);
        // texte en blanc
        EditText et = sv.findViewById(androidx.appcompat.R.id.search_src_text);
        et.setTextColor(Color.WHITE);
        et.setHintTextColor(0x88FFFFFF);
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
}
