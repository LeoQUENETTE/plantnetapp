package com.example.plantnetapp.front.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;
import com.example.plantnetapp.back.DBHelper;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.CsvParser;
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.back.tables.UserTable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextView loadingMessageView;
    private AlertDialog loadingDialog;

    private DBHelper db;
    private UserTable userTable;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, ">>> onCreate START");

        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        CsvParser.createInstance(this);
        db = DBHelper.getInstance(this,null);
        userTable = UserTable.getInstance();
        if(!userTable.tableExist()){
            db.initializeTables();
        };
        db.dropTables();
//        db.initializeTables();
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();

        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.etUsername);
        EditText pswrd = findViewById(R.id.etPassword);

        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            if (isConnected){
                login(username.getText().toString(), pswrd.getText().toString());
            }else{
                try {
                    userTable.login(username.getText().toString(), pswrd.getText().toString());
                } catch (Exception e) {
                    return;
                }
            }

        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void login(String username, String pswrd){
        showLoadingDialog("Connecting to server...");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            updateLoadingMessage("Authenticating user...");
            User connectionUser = User.login("aa","a");
            updateLoadingMessage("Finalizing...");
            runOnUiThread(() -> {
                dismissLoadingDialog();
                if (connectionUser == null) {
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(this, MainActivity.class);
                    i.putExtra("connected_user", connectionUser);
                    startActivity(i);
                }
            });
        });
    }
}

