package com.example.plantnetapp.front;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.User;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class PhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;

    private static User connectedUser;
    private ImageCapture imageCapture;
    private Executor cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        User user = (User) getIntent().getSerializableExtra("connected_user");
        if (user != null){
            connectedUser = user;
        }

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

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Préview
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                // Use case de capture d’image
                imageCapture = new ImageCapture.Builder().build();

                // Sélectionne la caméra arrière
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                // Bind to lifecycle
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
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.submit(() -> {
                            Bitmap bitmap = BitmapFactory.decodeResource(PhotoActivity.this.getResources(), R.drawable.juncusfiliformis_1);
                            File outputFile = new File(PhotoActivity.this.getCacheDir(), "juncusfiliformis_1");
                            try (FileOutputStream out = new FileOutputStream(outputFile)) {
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // ou JPEG selon besoin
                                out.flush();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            Plant plant = Plant.addPlantNoCollection(outputFile, // TODO change with actual photo, only for the test
                                    connectedUser, PhotoActivity.this);
                            runOnUiThread(() -> {
                                if (plant != null){
                                    Intent intent = new Intent(PhotoActivity.this, DetailActivity.class);
                                    intent.putExtra("returnMain", true);
                                    intent.putExtra("user",connectedUser);
                                    intent.putExtra("plantName", plant.name);
                                    intent.putExtra("noCollection", true);
                                    startActivity(intent);
                                }
                            });
                        });
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
}
