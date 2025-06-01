package com.example.plantnetapp.front.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.DBHelper;
import com.example.plantnetapp.back.api.PlantNetAPI;
import com.example.plantnetapp.back.api.ReturnType;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.PlantCollection;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.PlantCollectionTable;
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.front.adapter.CollectionAdapterCheckbox;
import com.example.plantnetapp.back.CsvParser;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class PhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.CAMERA};
    private User user;
    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Executor cameraExecutor;
    private TextView loadingMessageView;
    private AlertDialog loadingDialog;
    private AlertDialog addCollectionDialog;
    private List<String> collectionName;
    private File outputFile;
    private Plant plant;
    private PlantTable plantTable;
    private PlantCollectionTable collectionTable;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        User foundUser = (User) getIntent().getSerializableExtra("connected_user");
        if (foundUser == null){
            finish();
            return;
        }
        user = foundUser;
        DBHelper db = DBHelper.getInstance(PhotoActivity.this, null);
        plantTable = PlantTable.getInstance();
        collectionTable = PlantCollectionTable.getInstance();
        if(plantTable.tableExist()){
            db.initializeTables();
        };
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getCollectionNames(null);
        previewView = findViewById(R.id.previewView);
        Button btnCapture = findViewById(R.id.btnCapture);

        cameraExecutor = ContextCompat.getMainExecutor(this);

        // Demande de permission caméra si nécessaire
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }



        btnCapture.setOnClickListener(v -> takePhoto());
    }

    private void getCollectionNames(Runnable onfinish) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            List<PlantCollection> collection = PlantCollection.getCollectionsNoHistory(user.id);
            if (collection == null || collection.isEmpty()){
                collectionName = new ArrayList<>();
            }else{
                collectionName = collection.stream().map(c -> c.name).collect(Collectors.toList());
            }
            executor.shutdown();

            runOnUiThread(onfinish);
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, cameraExecutor);
    }

    private void takePhoto() {
        if (imageCapture == null) return;

        // Fichier de sortie (dans getFilesDir())
        AtomicReference<File> photoFile = new AtomicReference<>(new File(getFilesDir(),
                "plantnet_" + System.currentTimeMillis() + ".jpg"));

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(photoFile.get()).build();
        Log.d("CAMERA","Photo Prise");
        imageCapture.takePicture(
                options,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        showLoadingDialog(getString(R.string.loadingStartPhoto));
                        Bitmap bitmap = BitmapFactory.decodeResource(PhotoActivity.this.getResources(), R.drawable.juncusfiliformis_1);
                        outputFile = new File(PhotoActivity.this.getCacheDir(), "juncusfiliformis_1");
                        try (FileOutputStream out = new FileOutputStream(outputFile)) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // ou JPEG selon besoin
                            out.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.submit(() -> {
                            updateLoadingMessage(getString(R.string.loadingAnalysePhoto));
                            plant = Plant.addPlantNoCollection(outputFile, // TODO change with actual photo, only for the test
                                    user, PhotoActivity.this);
                            PlantCollection history = PlantCollection.getHistory(user.id);
                            if (history != null){
                                plant = Plant.getPlant(history.id, plant.name);
                                if (plant != null){
                                    plantTable.addData(plant);
                                }
                            }
                            runOnUiThread(() -> {
                                dismissLoadingDialog();
                                showAddToCollectionDialog();
                            });
                        });
                        executor.shutdown();


                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        runOnUiThread(() ->
                                Toast.makeText(PhotoActivity.this,
                                        "Error saving photo: " + exception.getMessage(),
                                        Toast.LENGTH_SHORT).show());
                        exception.printStackTrace();
                    }
                }
        );
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                finish(); // ou affiche un message
            }
        }
    }
    private void updateLoadingMessage(String message) {
        runOnUiThread(() -> {
            if (loadingMessageView != null) {
                loadingMessageView.setText(message);
            }
        });
    }

    private void dismissLoadingDialog() {
        runOnUiThread(() -> {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        });
    }
    private void showLoadingDialog(String initialMessage) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
        loadingMessageView = dialogView.findViewById(R.id.loadingMessage);
        loadingMessageView.setText(initialMessage);

        loadingDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        loadingDialog.show();
    }

    private void showAddToCollectionDialog(){
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_to_collection, null);
        RecyclerView recyclerView = dialogView.findViewById(R.id.collectionRecyclerView);
        CollectionAdapterCheckbox adapter = new CollectionAdapterCheckbox(collectionName,() -> {
            addCollectionDialog.cancel();
            getNewCollectionName();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        addCollectionDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK", (d, w) -> {
                    addPlantInCollections(adapter.getCheckedItems());
                })
                .setNegativeButton("Cancel", null)
                .create();

        addCollectionDialog.show();
    }
    private void getNewCollectionName(){
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_collection, null);
        AlertDialog addCollectionDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        addCollectionDialog.show();
        EditText etCollectionName = addCollectionDialog.findViewById(R.id.textCollection);
        Button submitBtn = addCollectionDialog.findViewById(R.id.addCollection);
        if (submitBtn == null){
            addCollectionDialog.cancel();
            return;
        }
        submitBtn.setOnClickListener(v -> {
            if (etCollectionName != null && !etCollectionName.getText().toString().trim().isEmpty()){
                showLoadingDialog(getText(R.string.loadingCreateCollection).toString());
                addCollection(etCollectionName.getText().toString());
                dismissLoadingDialog();
                addCollectionDialog.cancel();
                getCollectionNames(this::showAddToCollectionDialog);
            } else if (etCollectionName.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, getText(R.string.noCollectionName), Toast.LENGTH_SHORT).show();
            }
        });
        Button cancelBtn = addCollectionDialog.findViewById(R.id.cancelAddCollection);
        if (cancelBtn == null){
            addCollectionDialog.cancel();
            return;
        }
        cancelBtn.setOnClickListener(v -> {
            finish();
            addCollectionDialog.cancel();
        });
    }
    private void addCollection(String collectionName){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String collectionID = null;
            if (isConnected){
                collectionID = PlantCollection.addCollection(user.id,collectionName);
                if (collectionID != null){
                    Toast.makeText(this, getString(R.string.addCollectionSuccess),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, getString(R.string.addCollectionFailed),Toast.LENGTH_SHORT).show();
                }
            }
            PlantCollection newCol = new PlantCollection(collectionID, user.id, collectionName, null);
            collectionTable.addData(newCol);
        });
        executor.shutdown();
    }
    private void addPlantInCollections(Set<Integer> selected){
        if (selected.isEmpty()){
            showPlant(plant);
            return;
        }
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(availableProcessors);
        for (int index : selected) {
            executor.submit(() -> {
                String name = collectionName.get(index);
                if (isConnected){
                    PlantCollection collection = PlantCollection.getCollection(user.id,name);
                    PlantNetAPI api = PlantNetAPI.createInstance();
                    ReturnType apiResponse = null;
                    try {
                        apiResponse = api.identify(outputFile,null,null,null,1,null,null,null);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    JsonObject mostPossiblePlant = apiResponse.values.getAsJsonArray("results").get(0).getAsJsonObject();
                    String plantName = mostPossiblePlant.getAsJsonObject("species").get("scientificNameWithoutAuthor").getAsString();
                    Map<String, Plant> plants = CsvParser.createInstance(PhotoActivity.this);
                    Plant plant = plants.get(plantName);
                    if (collection == null){
                        return;
                    }
                    Plant.addPlant(plant,outputFile,collection);
                    Plant newPlant= Plant.getPlant(collection.id,plantName);
                    plantTable.addData(newPlant);
                }
            });
        }
        showPlant(plant);
        executor.shutdown();
    }
    private void showPlant(Plant plant){
        if (plant != null){
            Intent intent = new Intent(PhotoActivity.this, DetailActivity.class);
            intent.putExtra("returnMain", true);
            intent.putExtra("user",user);
            intent.putExtra("plantName", plant.name);
            intent.putExtra("noCollection", true);
            dismissLoadingDialog();
            startActivity(intent);
        }
    }
}
