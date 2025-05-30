package com.example.plantnetapp.front;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.concurrent.atomic.AtomicReference;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);

        String plantName = (String) getIntent().getSerializableExtra("plantName");
        AtomicReference<PlantCollection> collection = new AtomicReference<>((PlantCollection) getIntent().getSerializableExtra("collection"));
        Boolean noCollection = (Boolean) getIntent().getSerializableExtra("noCollection");
        Boolean returnMain = (Boolean) getIntent().getSerializableExtra("returnMain");
        User user  = (User) getIntent().getSerializableExtra("user");
        if (plantName == null || user == null){
            finish();
            return;
        }
        if (collection.get() == null && noCollection != null && noCollection){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                collection.set(PlantCollection.getHistory(user.id));
                Plant plant = Plant.getPlant(collection.get().id, plantName);
                runOnUiThread(() -> {
                    createService(plant);
                });
            });
        }
        else if(collection.get() == null){
            finish();
            return;
        }else{
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                Plant plant = Plant.getPlant(collection.get().id, plantName);
                runOnUiThread(() -> {
                    createService(plant);
                });
            });
        }

        // 1) Bouton Retour
        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
                    if (Boolean.TRUE.equals(returnMain)){
                        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                        intent.putExtra("user",user);
                        startActivity(intent);
                    }
                    finish();
                }
        );

        // 2) Bouton Web
        Button btnWeb = findViewById(R.id.btnWeb);
        btnWeb.setOnClickListener(v -> {
            callTelaBotanica(plantName);
        });


    }

    public void callTelaBotanica(String specie){
        String url = "https://www.tela-botanica.org/?s=" + specie;
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(web);
    }

    public void createService(Plant plant){
        if (plant != null){
            TextView tvNom  = findViewById(R.id.tvNomDetail);
            TextView tvDesc = findViewById(R.id.tvDescDetail);
            tvNom.setText(plant.name);
            tvDesc.setText("");
            List<ServiceEntry> services = new ArrayList<>();
            try {
                addToService(services, "nitrogen provision", plant.azoteFixing,plant.azoteReliability, plant.culturalCondition);
                addToService(services, "storage and return water", plant.waterFixing, plant.waterReliability, plant.culturalCondition);
                addToService(services, "soil structuration", plant.upgradeGrnd, plant.upgradeReliability, plant.culturalCondition);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RecyclerView rv = findViewById(R.id.rvServices);
            rv.setLayoutManager(new LinearLayoutManager(this));
            rv.setNestedScrollingEnabled(false);
            rv.setAdapter(new ServiceEntryAdapter(services));
        }
    }

    public void addToService(List<ServiceEntry>  services,String name,Float value,Float reliability,String culturalCondition){
        if (value > 0){
            services.add(new ServiceEntry(
                    name,
                    Float.toString(value),
                    Float.toString(reliability),
                    culturalCondition)
            );
        }
    }
}
