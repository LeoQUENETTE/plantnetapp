package com.example.plantnetapp.front.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.front.entry.ServiceEntry;
import com.example.plantnetapp.front.adapter.ServiceEntryAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class DetailActivity extends AppCompatActivity {

    private String plantName;
    private User user;
    private PlantCollection collection;
    private Plant plant;
    private PlantTable plantTable;
    private boolean isConnected;
    private boolean noCollection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
        setContentView(R.layout.activity_detail);

        String foundPlantName = (String) getIntent().getSerializableExtra("plantName");
        AtomicReference<PlantCollection> foundCollection = new AtomicReference<>((PlantCollection) getIntent().getSerializableExtra("collection"));
        Boolean noCollection = (Boolean) getIntent().getSerializableExtra("noCollection");
        Boolean fromList = (Boolean) getIntent().getSerializableExtra("fromList");
        Boolean returnMain = (Boolean) getIntent().getSerializableExtra("returnMain");
        User foundUser  = (User) getIntent().getSerializableExtra("user");
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();

        if (foundPlantName == null || foundUser == null){
            if (fromList != null && fromList){
                Plant plantList = (Plant) getIntent().getSerializableExtra("plant");
                createService(plantList);
            }else{
                finish();
                return;
            }

        }
        plantName = foundPlantName;
        user = foundUser;
        plantTable = PlantTable.getInstance();
        if (foundCollection.get() == null && noCollection != null && noCollection){
            this.noCollection = true;
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                foundCollection.set(PlantCollection.getHistory(user.id));
                if (isConnected){
                    plant = Plant.getPlant(foundCollection.get().id, plantName);
                }else{
                    try {
                        plant = (Plant) plantTable.selectData(foundCollection.get().id, plantName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                if (plant == null){
                    return;
                }
                runOnUiThread(() -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(plant.imageData, 0, plant.imageData.length);
                    ImageView imageView = findViewById(R.id.plantImage);
                    imageView.setImageBitmap(bitmap);
                    createService(plant);
                });
            });
        }
        else if(foundCollection.get() == null){
            if (fromList == null || !fromList){
                finish();
                return;
            }
        }else if (fromList == null){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                if (isConnected){
                    plant = Plant.getPlant(foundCollection.get().id, plantName);
                }else{
                    try {
                        plant = (Plant) plantTable.selectData(foundCollection.get().id, plantName);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                runOnUiThread(() -> {
                    if(plant != null) {
                        createService(plant);
                    }
                });
                runOnUiThread(() -> {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(plant.imageData, 0, plant.imageData.length);
                    ImageView imageView = findViewById(R.id.plantImage);
                    imageView.setImageBitmap(bitmap);
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
                if (noCollection || Objects.equals(collection.name, "history")){
                    Toast.makeText(this, getText(R.string.deletionImpossible).toString(), Toast.LENGTH_SHORT).show();
                }else{
                    deletePlantWithConfirmation();
                }
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
                .setTitle(getText(R.string.confirmDeletion).toString())
                .setMessage(getText(R.string.sureDeletion).toString())
                .setPositiveButton("Oui", (dialog, which) -> {
                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        try {
                            boolean success;
                            if (isConnected){
                                success = Plant.deletePlant(collection.id, plantName);
                            }
                            success = plantTable.deletePlant(collection.id, plantName);
                            boolean finalSuccess = success;
                            runOnUiThread(() -> {
                                if (finalSuccess) {
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
                                Toast.makeText(this, getText(R.string.deletionError).toString(), Toast.LENGTH_SHORT).show();
                                setResult(RESULT_CANCELED);
                            });
                        } finally {
                            executor.shutdown();
                        }
                    });
                })
                .setNegativeButton(getText(R.string.no).toString(), null)
                .show();
    }

    private void callTelaBotanica(String specie){
        String url = "https://www.tela-botanica.org/?s=" + specie;
        Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(web);
    }
    private void createService(Plant plant){
        if (plant != null){
            TextView tvNom  = findViewById(R.id.tvNomDetail);
            tvNom.setText(plant.name);
            List<ServiceEntry> services = new ArrayList<>();
            try {
                addToService(services, getText(R.string.nitrogen_provision).toString(), plant.azoteFixing,plant.azoteReliability, plant.culturalCondition);
                addToService(services, getText(R.string.storage_gain_water).toString(), plant.waterFixing, plant.waterReliability, plant.culturalCondition);
                addToService(services, getText(R.string.soil_structuration).toString(), plant.upgradeGrnd, plant.upgradeReliability, plant.culturalCondition);
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
                    getText(R.string.valueText) +  Float.toString(value),
                    getText(R.string.reliabitityText) +Float.toString(reliability),
                    getText(R.string.culturalCondition) + culturalCondition)
            );
        }
    }
}
