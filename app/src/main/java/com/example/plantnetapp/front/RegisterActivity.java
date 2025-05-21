package com.example.plantnetapp.front;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;

public class RegisterActivity extends AppCompatActivity {
    EditText etLogin, etFirstName, etLastName, etMdp, etRole, etMail, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etLogin = findViewById(R.id.etLogin);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etMdp = findViewById(R.id.etMdp);
        etRole = findViewById(R.id.etRole);
        etMail = findViewById(R.id.etMail);
        etPhone = findViewById(R.id.etPhone);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()){
                    // Mock de logique d'inscription réussie
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validateInputs(){
        if(TextUtils.isEmpty(etLogin.getText()) || TextUtils.isEmpty(etFirstName.getText()) ||
                TextUtils.isEmpty(etLastName.getText()) || TextUtils.isEmpty(etMdp.getText()) ||
                TextUtils.isEmpty(etRole.getText()) || TextUtils.isEmpty(etMail.getText()) ||
                TextUtils.isEmpty(etPhone.getText())){

            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
