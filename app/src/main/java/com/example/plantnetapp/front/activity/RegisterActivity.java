package com.example.plantnetapp.front.activity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;
import com.example.plantnetapp.back.DBHelper;
import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.entity.User;
import com.example.plantnetapp.back.tables.UserTable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {
    private EditText etUsername, etFirstName, etLastName, etPswrd, etMail, etPhone;
    private static final String TAG = "RegisterActivity";
    private boolean isConnected;
    private UserTable userTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_register);

        DBHelper db = DBHelper.getInstance(this,null);
        userTable = UserTable.getInstance();

        etUsername = findViewById(R.id.etLogin);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName  = findViewById(R.id.etLastName);
        etPswrd = findViewById(R.id.etMdp);
        etMail      = findViewById(R.id.etMail);
        etPhone     = findViewById(R.id.etPhone);

        Button btnRegister = findViewById(R.id.btnRegister);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener( v -> {
            finish();
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Ici la logique d’inscription réelle
                    Toast.makeText(RegisterActivity.this, R.string.registerSucess, Toast.LENGTH_SHORT).show();
                    register(etUsername.getText().toString(),
                            etPswrd.getText().toString(),
                            etFirstName.getText().toString(),
                            etLastName.getText().toString(),
                            etMail.getText().toString(),
                            etPhone.getText().toString());
                }
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etUsername.getText())   ||
                TextUtils.isEmpty(etFirstName.getText())||
                TextUtils.isEmpty(etLastName.getText()) ||
                TextUtils.isEmpty(etPswrd.getText())      ||
                TextUtils.isEmpty(etMail.getText())     ||
                TextUtils.isEmpty(etPhone.getText())) {

            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void register(String username,String  pswrd,String  firstname,String  lastname,String  mail,String phone){
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            User newUser = new User(null, username, pswrd,firstname,lastname,mail, phone);
            ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && activeNetwork.isConnectedOrConnecting();
            if (isConnected){
                newUser.id = User.addUser(newUser);
            }
            try {
                userTable.addData(newUser);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            runOnUiThread(this::finish);
        });
    }
}
