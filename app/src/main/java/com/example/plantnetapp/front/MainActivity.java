package com.example.plantnetapp.front;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       if (getSupportActionBar() != null) {
           getSupportActionBar().hide();
           Log.d(TAG, "ActionBar cachée");
       }

       Button btnPhoto = findViewById(R.id.btnPhoto);
       btnPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
               startActivity(intent);
           }
       });

       Button btnHistorique = findViewById(R.id.btnHistorique);
       btnHistorique.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, HistoriqueActivity.class);
               startActivity(intent);
           }
       });

       Button btnInfos = findViewById(R.id.btnInfos);
       btnInfos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, PlantsListActivity.class);
               startActivity(intent);
           }
       });
   }
}
