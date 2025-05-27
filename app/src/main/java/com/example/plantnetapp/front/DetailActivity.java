package com.example.plantnetapp.front;

import android.content.Intent;
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
        // on cache l'ActionBar
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);

        // 1) Bouton Retour
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Simplement fermer cette activité pour revenir à la précédente (MainActivity)
                finish();
            }
        });

        // 2) Récupérer et afficher le Plant (ou ses données passées)
        Plant plant = (Plant) getIntent().getSerializableExtra("plant");
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
