package com.example.plantnetapp.front;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;
import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🚩 LOG IMMEDIAT
        Log.d(TAG, ">>> onCreate START");

        super.onCreate(savedInstanceState);
        // Masquer l’ActionBar (s’il existait)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            Log.d(TAG, "ActionBar cachée");
        }

        setContentView(R.layout.activity_login);
        Log.d(TAG, "setContentView terminé");

        EditText username = findViewById(R.id.etUsername);
        EditText pswrd = findViewById(R.id.etPassword);
        // Pour vérifier visuellement
        Toast.makeText(this, "LoginActivity lancé", Toast.LENGTH_SHORT).show();

        // Reste de ton code…
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            Log.d(TAG, "Clique sur Créer un compte");
            startActivity(new Intent(this, RegisterActivity.class));
        });
        Log.d(TAG, "Listener tvRegister configuré");

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            Log.d(TAG, "Clique sur Se connecter");
            login(username.getText().toString(), pswrd.getText().toString());
        });
        Log.d(TAG, "Listener btnLogin configuré");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    private void login(String username, String pswrd){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            User connectionUser = User.login(username,pswrd);
            runOnUiThread(() -> {
                if (connectionUser == null) {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                }
            });
        });
    }
}

