package com.example.plantnetapp.front;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Cacher l'ActionBar
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);

        // Récupérer la plante (via Serializable ou via String extras)
        Plant plant = (Plant) getIntent().getSerializableExtra("plant");
        String species = plant != null ? plant.getName() :
                getIntent().getStringExtra("plantName");

        // 1) Bouton Retour
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // 2) Bouton Web
        Button btnWeb = findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(v -> {
            // Transformer "Carex sempervirens" → "Carex+sempervirens"
            String query = species.trim().replace(" ", "+");
            String url = "https://www.tela-botanica.org/?s=" + query;
            // Ouvrir le navigateur
            Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(web);
        });

        // 2) Récupérer et afficher le Plant (ou ses données passées)
        Plant plants = (Plant) getIntent().getSerializableExtra("plant");
        if (plant == null) {
            // Si vous passiez "plantName" et "plantDesc" à la place, adaptez ici :
            String name = getIntent().getStringExtra("plantName");
            String desc = getIntent().getStringExtra("plantDesc");
            plant = new Plant(name == null ? "" : name,
                    desc == null ? "" : desc,
                    "");
        }

        TextView tvNom  = findViewById(R.id.tvNomDetail);
        TextView tvDesc = findViewById(R.id.tvDescDetail);
        tvNom.setText(plant.getName());
        tvDesc.setText(plant.getDescription());

        // 3) Charger les services depuis le CSV et filtrer par espèce
        List<ServiceEntry> services = new ArrayList<>();
        try {
            InputStream is = getResources().openRawResource(R.raw.data_1744126677780);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            br.readLine(); // sauter l'en-tête
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (cols.length >= 5 && cols[1].equalsIgnoreCase(plant.getName())) {
                    services.add(new ServiceEntry(
                            cols[0],    // service
                            cols[2],    // value
                            cols[3],    // reliability
                            cols[4]     // cultural_condition
                    ));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 4) Afficher les services dans le RecyclerView
        RecyclerView rv = findViewById(R.id.rvServices);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setNestedScrollingEnabled(false);
        rv.setAdapter(new ServiceEntryAdapter(services));
    }
}
