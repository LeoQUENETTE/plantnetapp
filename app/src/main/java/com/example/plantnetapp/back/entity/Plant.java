package com.example.plantnetapp.back.entity;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.api.PlantNetAPI;
import com.example.plantnetapp.back.api.ReturnType;
import com.example.plantnetapp.back.CsvParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Plant  extends Entity implements Serializable {
    public String id = "";
    public final String name;
    public final String collectionID;
    public float azoteFixing;
    public float upgradeGrnd;
    public float waterFixing;
    public float azoteReliability;
    public float upgradeReliability;
    public float waterReliability;

    public String culturalCondition;
    public byte[] imageData;
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
        this.collectionID = "";
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
        this.azoteReliability = 0F;
        this.upgradeReliability = 0F;
        this.waterReliability = 0F;
        this.culturalCondition = "";
        this.imageData = null;
    }
    /** Add plant from external BDD to internal BDD**/
    public Plant(String id, String name, String collectionID,String culturalCondition,float azoteFixing, float upgradeGrnd, float waterFixing, float azoteFixingR, float upgradeGrndR, float waterFixingR,byte[] imageData) {
        this.id = id;
        this.name = name;
        this.collectionID = collectionID;
        this.culturalCondition = culturalCondition;
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
        this.azoteReliability = azoteFixingR;
        this.upgradeReliability = upgradeGrndR;
        this.waterReliability = waterFixingR;
        this.imageData = imageData;

    }
    public Plant(String name, String collectionID,String culturalCondition,float azoteFixing, float upgradeGrnd, float waterFixing, float azoteFixingR, float upgradeGrndR, float waterFixingR,byte[] imageData) {
        this.name = name;
        this.collectionID = collectionID;
        this.culturalCondition = culturalCondition;
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
        this.azoteReliability = azoteFixingR;
        this.upgradeReliability = upgradeGrndR;
        this.waterReliability = waterFixingR;
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
        String id = plantObject.get("id").getAsString();
        String collectionID = plantObject.get("collectionID").getAsString();
        String name = plantObject.get("name").getAsString();
        String cultural_condition = plantObject.get("cultural_condition").getAsString();
        float azote_fixation = plantObject.get("azote_fixation").getAsFloat();
        float upgrade_ground = plantObject.get("upgrade_ground").getAsFloat();
        float water_fixation = plantObject.get("water_fixation").getAsFloat();
        float azote_fixation_r = plantObject.get("azote_fixation_r").getAsFloat();
        float upgrade_ground_r = plantObject.get("upgrade_ground_r").getAsFloat();
        float water_fixation_r = plantObject.get("water_fixation_r").getAsFloat();
        String image_data = plantObject.get("image_data").getAsString();


        byte[] imageBytes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            imageBytes = Base64.getDecoder().decode(image_data);
        }
        return new Plant(id, name, collectionID,cultural_condition,azote_fixation,upgrade_ground,water_fixation,azote_fixation_r,upgrade_ground_r,water_fixation_r,imageBytes);
    }
    public static void addPlant(Plant plant, File image, PlantCollection collection){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            if (plant == null){
                return;
            }
            bdd.addPlant(collection.id, plant, image);
        }
        catch (Exception ignored) {
        }
    }
    public static void addPlant(Plant plant, PlantCollection collection){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            if (plant == null){
                return;
            }
            File imageFile = File.createTempFile("plant_image_", ".png");

            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(plant.imageData);
            }
            bdd.addPlant(collection.id, plant, imageFile);
        }
        catch (Exception ignored) {
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
        }catch (Exception e){
            return null;
        }
    }
}
