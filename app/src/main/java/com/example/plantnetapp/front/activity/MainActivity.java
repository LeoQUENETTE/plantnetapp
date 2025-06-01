package com.example.plantnetapp.front.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;
import com.example.plantnetapp.back.SyncDatabase;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.PlantCollection;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.PlantCollectionTable;
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.back.tables.Table;
import com.example.plantnetapp.back.tables.UserTable;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static User connectedUser;
    private boolean isConnected;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_main);

       User user = (User) getIntent().getSerializableExtra("connected_user");
       if (user != null){
           connectedUser = user;
       }

       ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
       NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
       isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
       if (isConnected){
           ExecutorService executor = Executors.newSingleThreadExecutor();
           executor.submit(() -> {
               SyncDatabase.syncEI(connectedUser);
           });
           executor.shutdown();
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
               changeLanguage(this);
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
               intent.putExtra("connected_user", connectedUser);
               startActivity(intent);
           }
       });
   }

   private void changeLanguage(Context context){
       final String[] languages = {context.getString(R.string.english), context.getString(R.string.french)};
       final String[] languageCodes = {"en", "fr"};
       final int[] selected = {-1}; // default selection

       AlertDialog.Builder builder = new AlertDialog.Builder(context);
       builder.setTitle(context.getString(R.string.language_title));

       builder.setSingleChoiceItems(languages, -1, (dialog, which) -> {
           selected[0] = which;
       });

       builder.setNegativeButton(context.getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

       builder.setPositiveButton(context.getString(R.string.confirm), (dialog, which) -> {
           if (selected[0] != -1) {
               setLocale(context, languageCodes[selected[0]]);
               // Restart activity to apply change
               Intent intent = new Intent(context, context.getClass());
               context.startActivity(intent);
               if (context instanceof Activity) {
                   ((Activity) context).finish();
               }
           }
       });

       builder.show();
   }
    private void setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        SharedPreferences prefs = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        prefs.edit().putString("App_Lang", languageCode).apply();
    }
   private void deleteUser(User user){
       ExecutorService executor = Executors.newSingleThreadExecutor();
       executor.submit(() -> {
           boolean result;
           UserTable userTable = UserTable.getInstance();
           if (isConnected){
               result = User.deleteUser(user.id);
           }
           result = userTable.deleteUser(user.id);
           boolean finalResult = result;
           runOnUiThread(() -> {
               if (finalResult){
                   Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                   startActivity(intent);
               }else{
                   Toast.makeText(MainActivity.this,R.string.deleteuserErrorMsg,Toast.LENGTH_SHORT).show();
               }
           });

       });
   }

}
