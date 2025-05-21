package com.example.plantnetapp.front;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantnetapp.R;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Pour la maquette, les informations affichées sont statiques.
        // Plus tard, on mettra à jour dynamiquement le résultat de l'identification.
    }
}
