package com.example.plantnetapp.front;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.plantnetapp.R;

import java.io.File;

public class PreviewActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private Button btnConfirm, btnRetake, btnCancel;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        // Get the photo path from intent
        photoPath = getIntent().getStringExtra("photo_path");

        // Initialize views
        imagePreview = findViewById(R.id.imagePreview);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnRetake = findViewById(R.id.btnRetake);
        btnCancel = findViewById(R.id.btnCancel);

        // Load and display the image
        displayImage();

        // Set up button listeners
        btnConfirm.setOnClickListener(v -> confirmPhoto());
        btnRetake.setOnClickListener(v -> retakePhoto());
        btnCancel.setOnClickListener(v -> cancelPhoto());
    }

    private void displayImage() {
        if (photoPath != null) {
            // Get the dimensions of the View
            int targetW = imagePreview.getWidth();
            int targetH = imagePreview.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
            imagePreview.setImageBitmap(bitmap);
        }
    }

    private void confirmPhoto() {
        // Return the photo path to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("confirmed_photo_path", photoPath);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void retakePhoto() {
        // Delete the current photo and return to camera
        if (photoPath != null) {
            new File(photoPath).delete();
        }
        finish();
    }

    private void cancelPhoto() {
        // Delete the photo and finish
        if (photoPath != null) {
            new File(photoPath).delete();
        }
        setResult(RESULT_CANCELED);
        finish();
    }
}