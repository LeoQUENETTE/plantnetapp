package com.example.plantnetapp.front;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static User connectedUser;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       User user = (User) getIntent().getSerializableExtra("connected_user");
       if (user != null){
           connectedUser = user;
       }

       if (getSupportActionBar() != null) {
           getSupportActionBar().hide();
       }

       ImageButton btnSettings = findViewById(R.id.btnSettings);
       btnSettings.setOnClickListener(v -> {
           View popupView = LayoutInflater.from(this).inflate(R.layout.settings, null);

           PopupWindow popupWindow = new PopupWindow(popupView,
                   ViewGroup.LayoutParams.WRAP_CONTENT,
                   ViewGroup.LayoutParams.WRAP_CONTENT,
                   true); // Focusable

           popupWindow.setElevation(10);
           popupWindow.showAsDropDown(btnSettings, -20, 10); // offsetX, offsetY

           popupView.findViewById(R.id.language).setOnClickListener(opt -> {
               popupWindow.dismiss();
           });

           popupView.findViewById(R.id.deleteUser).setOnClickListener(opt -> {
               popupWindow.dismiss();
               deleteUser(user);
           });
       });
       Button btnPhoto = findViewById(R.id.btnPhoto);
       btnPhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
               intent.putExtra("connected_user", connectedUser);
               startActivity(intent);
           }
       });

       Button btnHistorique = findViewById(R.id.btnHistorique);
       btnHistorique.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(MainActivity.this, HistoriqueActivity.class);
               intent.putExtra("connected_user", connectedUser);
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
   private void deleteUser(User user){
       ExecutorService executor = Executors.newSingleThreadExecutor();
       executor.submit(() -> {
           boolean result = User.deleteUser(user.id);
           runOnUiThread(() -> {
               if (result){
                   Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                   startActivity(intent);
               }else{
                   Toast.makeText(MainActivity.this,R.string.deleteuserErrorMsg,Toast.LENGTH_SHORT).show();
               }
           });

       });
   }
}
