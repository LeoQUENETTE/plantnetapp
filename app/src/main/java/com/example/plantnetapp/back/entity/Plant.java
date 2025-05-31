package com.example.plantnetapp.back.entity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.plantnetapp.R;
import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.api.PlantNetAPI;
import com.example.plantnetapp.back.api.ReturnType;
import com.example.plantnetapp.front.CsvParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Plant  extends Entity implements Serializable {
    public String id = "";
    public final String name;
    public float azoteFixing = 0;
    public float upgradeGrnd = 0;
    public float waterFixing = 0;
    public float azoteReliability = 0;
    public float upgradeReliability = 0;
    public float waterReliability = 0;

    public String culturalCondition = "";
    public byte[] imageData = null;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return Float.compare(azoteFixing, plant.azoteFixing) == 0 && Float.compare(upgradeGrnd, plant.upgradeGrnd) == 0 && Float.compare(waterFixing, plant.waterFixing) == 0 && Objects.equals(name, plant.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, azoteFixing, upgradeGrnd, waterFixing);
    }

    public Plant(String id, String name, float azoteFixing, float upgradeGrnd, float waterFixing) {
        this.id = id;
        this.name = name;
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
    }
    public Plant(String name, float azoteFixing, float upgradeGrnd, float waterFixing, byte[] imageData) {
        this.name = name;
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
        this.imageData = imageData;
    }

    @Override
    public boolean equalsWithoutId(Object o) {
        return false;
    }

    public static Plant plantFromJSON (JsonObject object, Boolean indent){
        JsonObject plantObject = object;
        if (indent){
            plantObject = object.getAsJsonObject("plant");
        }
        String name = plantObject.get("name").getAsString();
        float azote_fixation = plantObject.get("azote_fixation").getAsFloat();
        float upgrade_ground = plantObject.get("upgrade_ground").getAsFloat();
        float water_fixation = plantObject.get("water_fixation").getAsFloat();
        String image_data = plantObject.get("image_data").getAsString();


        byte[] imageBytes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imageBytes = Base64.getDecoder().decode(image_data);
        }
        return new Plant(name,azote_fixation,upgrade_ground,water_fixation, imageBytes);
    }
    public static Plant addPlant(File image,PlantCollection collection,Context context){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        PlantNetAPI api = PlantNetAPI.createInstance();
        try{
            ReturnType apiResponse = api.identify(image,null,null,null,1,null,null,null);
            JsonObject mostPossiblePlant = apiResponse.values.getAsJsonArray("results").get(0).getAsJsonObject();
            String plantName = mostPossiblePlant.getAsJsonObject("species").get("scientificNameWithoutAuthor").getAsString();
            Map<String, Plant> plants = CsvParser.createInstance(context);
            if (plants == null){
                return null;
            }
            Plant plant = plants.get(plantName);
            if (plant == null){
                return null;
            }
            bdd.addPlant(collection.id, plant, image);
            return plant;
        }
        catch (Exception e) {
            return null;
        }
    }
    public static Plant addPlantNoCollection(File image, User currentUser, Context context){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        PlantNetAPI api = PlantNetAPI.createInstance();
        try{
            ReturnType apiResponse = api.identify(image,null,null,null,1,null,null,null);
            JsonObject mostPossiblePlant = apiResponse.values.getAsJsonArray("results").get(0).getAsJsonObject();
            String plantName = mostPossiblePlant.getAsJsonObject("species").get("scientificNameWithoutAuthor").getAsString();
            Map<String, Plant> plants = CsvParser.createInstance(context);
            if (plants == null){
                return null;
            }
            Plant plant = plants.get(plantName);
            if (plant == null){
                return null;
            }
            bdd.addPlantWithoutCollection(currentUser.id, plant, image);
            return plant;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean deletePlant(String collectionID, String plantName){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try {
            ReturnType response = bdd.deletePlant(collectionID, plantName);
            return response != null && response.status == 200;
        } catch (Exception e) {
            Log.e("PlantDelete", "Error deleting plant", e);
            return false;
        }
    }

    public static List<Plant> getAllPlants(String collectionID){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getAllPlants(collectionID);
            List<JsonElement> plantJsonArray = response.values.getAsJsonArray("plants").asList();
            if (!plantJsonArray.isEmpty()){
                ArrayList<Plant> plantList = new ArrayList<>();
                for (JsonElement plantJsonE : plantJsonArray){
                    JsonObject plantJsonO = plantJsonE.getAsJsonObject();
                    Plant newPlant = Plant.plantFromJSON(plantJsonO, false);
                    plantList.add(newPlant);
                }
                return plantList;
            }else{
                return null;
            }

        }catch (Exception e){
            return null;
        }
    }

    public static Plant getPlant(String collectionID, String plantName){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getPlant(collectionID, plantName);
            return Plant.plantFromJSON(response.values, true);
        }catch (IOException e){
            return null;
        }
    }
}
