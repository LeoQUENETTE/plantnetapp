package com.example.plantnetapp.front;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.PlantCollection;
import com.example.plantnetapp.back.entity.User;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class DetailActivity extends AppCompatActivity {

    private String plantName;
    private User user;
    private PlantCollection collection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);

        String foundPlantName = (String) getIntent().getSerializableExtra("plantName");
        AtomicReference<PlantCollection> foundCollection = new AtomicReference<>((PlantCollection) getIntent().getSerializableExtra("collection"));
        Boolean noCollection = (Boolean) getIntent().getSerializableExtra("noCollection");
        Boolean returnMain = (Boolean) getIntent().getSerializableExtra("returnMain");
        User foundUser  = (User) getIntent().getSerializableExtra("user");
        if (foundPlantName == null || foundUser == null){
            finish();
            return;
        }
        plantName = foundPlantName;
        user = foundUser;
        if (foundCollection.get() == null && noCollection != null && noCollection){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                foundCollection.set(PlantCollection.getHistory(user.id));
                Plant plant = Plant.getPlant(foundCollection.get().id, plantName);
                runOnUiThread(() -> {
                    createService(plant);
                });
                collection = foundCollection.get();
            });
        }
        else if(foundCollection.get() == null){
            finish();
            return;
        }else{
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                Plant plant = Plant.getPlant(foundCollection.get().id, plantName);
                runOnUiThread(() -> {
                    createService(plant);
                });
            });
        }
        collection = foundCollection.get();
        setUpSettingsDrawer();
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
    }

    private void setUpSettingsDrawer() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton hamburgerBtn = findViewById(R.id.btnDrawer);
        hamburgerBtn.setOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.END);
        });
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int moreInfoBtnId = R.id.nav_more_info;
            int deleteId = R.id.nav_delete;
            if (item.getItemId() == deleteId){
                deletePlantWithConfirmation();
                return true;
            } else if (item.getItemId() == moreInfoBtnId) {
                callTelaBotanica(plantName);
                return true;
            }
            return false;
        });
    }
    private void deletePlantWithConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmer la suppression")
                .setMessage("Voulez-vous vraiment supprimer cette plante ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            boolean success = Plant.deletePlant(collection.id, plantName);
                            runOnUiThread(() -> {
                                if (success) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("updateCollection", true);
                                    setResult(RESULT_OK, resultIntent);
                                } else {
                                    setResult(RESULT_CANCELED);
                                }
                                finish();
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                            });
                        } finally {
                            executor.shutdown();
                        }
                    });
                })
                .setNegativeButton("Non", null)
                .show();
    }

    private void deletePlant(){
        Plant.deletePlant(collection.id,plantName);
    }
    private void goBackAfterDeletion(){
        runOnUiThread(() -> {
            Intent newIntent = new Intent();
            newIntent.putExtra("updateCollection", true);
            setResult(RESULT_OK, newIntent);
            finish();
        });
    }

    private void callTelaBotanica(String specie){
        String url = "https://www.tela-botanica.org/?s=" + specie;
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(web);
    }


    private void createSingleThread(Runnable before, Runnable func, Runnable onFinish){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            before.run();
            func.run();
            onFinish.run();
        });
        executor.shutdown();
    }
    private void createService(Plant plant){
        if (plant != null){
            TextView tvNom  = findViewById(R.id.tvNomDetail);
            tvNom.setText(plant.name);
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

    private void addToService(List<ServiceEntry>  services,String name,Float value,Float reliability,String culturalCondition){
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
