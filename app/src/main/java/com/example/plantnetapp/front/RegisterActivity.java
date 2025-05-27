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
    private EditText etLogin, etFirstName, etLastName, etMdp, etRole, etMail, etPhone;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 1) Cacher la barre d’action
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_register);

        // 2) Liaison des champs
        etLogin     = findViewById(R.id.etLogin);
        etFirstName = findViewById(R.id.etFirstName);
        etLastName  = findViewById(R.id.etLastName);
        etMdp       = findViewById(R.id.etMdp);
        etRole      = findViewById(R.id.etRole);
        etMail      = findViewById(R.id.etMail);
        etPhone     = findViewById(R.id.etPhone);

        // 3) Bouton d’inscription
        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInputs()) {
                    // Ici la logique d’inscription réelle
                    Toast.makeText(RegisterActivity.this, "Inscription réussie !", Toast.LENGTH_SHORT).show();

                    // 4) Redirection vers l'écran de login
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    // On vide la pile pour éviter retour à Register
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(etLogin.getText())   ||
                TextUtils.isEmpty(etFirstName.getText())||
                TextUtils.isEmpty(etLastName.getText()) ||
                TextUtils.isEmpty(etMdp.getText())      ||
                TextUtils.isEmpty(etRole.getText())     ||
                TextUtils.isEmpty(etMail.getText())     ||
                TextUtils.isEmpty(etPhone.getText())) {

            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
