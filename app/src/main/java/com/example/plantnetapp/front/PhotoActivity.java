package com.example.plantnetapp.front;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
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
import com.google.common.util.concurrent.ListenableFuture;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class PhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS =
            new String[]{Manifest.permission.CAMERA};

    private PreviewView previewView;
    private ImageCapture imageCapture;
    private Executor cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

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
        File photoFile = new File(getFilesDir(),
                "plantnet_" + System.currentTimeMillis() + ".jpg");

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();

        imageCapture.takePicture(
                options,
                cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        runOnUiThread(() -> {
                            // Start PreviewActivity with the image path
                            Intent intent = new Intent(PhotoActivity.this, PreviewActivity.class);
                            intent.putExtra("photo_path", photoFile.getAbsolutePath());
                            startActivity(intent);
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
