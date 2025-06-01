// app/src/main/java/com/example/plantnetapp/front/CsvParser.java
package com.example.plantnetapp.back;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.entity.Plant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class CsvParser {
    /**ligne : service;species;value;reliability;cultural_condition
     */
    private static Boolean INSTANCE = false;
    private static final int RESSOURCES_FILE = R.raw.data_1744126677780;
    private static Map<String, Plant> plantInfoMap;
    public static Map<String, Plant> createInstance(Context context){
        if (!INSTANCE) {
            INSTANCE = true;
            plantInfoMap = parse(context);
        }
        return plantInfoMap;
    }
    private static Map<String, Plant> parse(Context ctx) {
        Map<String, Plant> plantMap = new HashMap<>();

        try (InputStream is = ctx.getResources().openRawResource(RESSOURCES_FILE);
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            // Skip header line
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";", -1);  // -1 keeps empty values
                if (cols.length < 5) continue;  // Skip incomplete lines

                String service = cols[0].trim();
                String species = cols[1].trim();
                String valueStr = cols[2].trim();
                String reliabilityStr = cols[3].trim();
                String culturalCondition = cols[4].trim();

                try {
                    float value = Float.parseFloat(valueStr);
                    float reliability = Float.parseFloat(reliabilityStr);
                    Plant plant = plantMap.computeIfAbsent(species,
                            k -> new Plant(null, species, 0, 0, 0));
                    if (plant.culturalCondition.trim().isEmpty()){
                        plant.culturalCondition = culturalCondition;
                    }
                    // Update plant properties based on service type
                    switch (service) {
                        case "storage_and_return_water":
                            plant.waterFixing = value;
                            plant.waterReliability = reliability;
                            break;
                        case "nitrogen_provision":
                            plant.azoteFixing = value;
                            plant.azoteReliability = reliability;
                            break;
                        case "soil_structuration":
                            plant.upgradeGrnd = value;
                            plant.upgradeReliability = reliability;
                            break;
                        default:
                            continue;  // Skip unknown service types
                    }
                } catch (NumberFormatException e) {
                    // Skip lines with invalid numeric values
                    continue;
                }
            }
            return plantMap;

        } catch (Resources.NotFoundException e) {
            Log.e("PlantParser", "Resource not found: " + RESSOURCES_FILE, e);
        } catch (IOException e) {
            Log.e("PlantParser", "Error reading file", e);
        } catch (Exception e) {
            Log.e("PlantParser", "Unexpected error", e);
        }
        return null;
    }
}
