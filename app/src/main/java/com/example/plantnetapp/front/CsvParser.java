// app/src/main/java/com/example/plantnetapp/front/CsvParser.java
package com.example.plantnetapp.front;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {
    /**
     * Lit le CSV dans res/raw et retourne une liste de Plant.
     * Chaque ligne : service;species;value;reliability;cultural_condition
     * Ici on utilise species comme name, value comme description, et on laisse imageUrl vide.
     */
    public static List<Plant> parse(Context ctx, int rawResId) {
        List<Plant> list = new ArrayList<>();
        try {
            InputStream is = ctx.getResources().openRawResource(rawResId);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            // Sauter la première ligne
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (cols.length >= 3) {
                    String species = cols[1];
                    String value   = cols[2];
                    // Pas d’URL d’image dans le CSV, on peut laisser vide ou mettre un placeholder
                    list.add(new Plant(species, value, ""));
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
