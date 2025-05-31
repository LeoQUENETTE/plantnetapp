package com.example.plantnetapp.back.entity;

import android.util.ArraySet;

import androidx.annotation.NonNull;

import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.api.ReturnType;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantCollection extends Entity implements Serializable {
    public String id = "";
    public List<Plant> plantList = null;
    public final int nbPlant = 0;

    public String name;

    public PlantCollection(String id, String name, List<Plant> plantList){
        this.id = id;
        this.plantList = plantList;
        this.name = name;
    }
    public PlantCollection(String name,List<Plant> plantList){
        this.plantList = plantList;
        this.name = name;
    }
    @Override
    public boolean equalsWithoutId(Object o) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantCollection that = (PlantCollection) o;
        return nbPlant == that.nbPlant && Objects.equals(plantList, that.plantList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantList, nbPlant);
    }

    public static PlantCollection plantCollectionFromJSON(JsonObject object){
        String id = object.getAsJsonObject("PlantCollection").get("id").getAsString();
        String name = object.getAsJsonObject("PlantCollection").get("name").getAsString();
        return new PlantCollection(id, name,null);
    }

    public static PlantCollection getHistory(String userID){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getPlantCollection(userID,"history");
            return PlantCollection.plantCollectionFromJSON(response.values);
        }catch (Exception e){
            return null;
        }
    }
    public static PlantCollection getCollection(String userID, String collectionName){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getPlantCollection(userID,collectionName);
            return PlantCollection.plantCollectionFromJSON(response.values);
        }catch (Exception e){
            return null;
        }
    }

    public static List<PlantCollection> getAllPlantCollection(String userID){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getAllPlantCollections(userID);
            List<JsonElement> plantCollectionsJsonObject = response.values.getAsJsonArray("PlantCollections").asList();
            if (!plantCollectionsJsonObject.isEmpty() && plantCollectionsJsonObject.size() - 1 > 0){
                return getPlantCollections(plantCollectionsJsonObject);
            }else{
                return null;
            }
        }catch (IOException e){
            return null;
        }
    }

    @NonNull
    private static ArrayList<PlantCollection> getPlantCollections(List<JsonElement> plantCollectionsJsonObject) {
        ArrayList<PlantCollection> plantCollections = new ArrayList<>();
        for (JsonElement plantColJsonEl : plantCollectionsJsonObject){
            JsonObject plantColJsonO = plantColJsonEl.getAsJsonObject();
            String id  = plantColJsonO.get("id").getAsString();
            String name  = plantColJsonO.get("name").getAsString();
            if (!Objects.equals(name, "history")){
                PlantCollection plantCol = new PlantCollection(id,name ,null);
                plantCollections.add(plantCol);
            }
        }
        return plantCollections;
    }

    public static boolean addCollection(String userID, String collectionName){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.addPlantCollection(userID, collectionName);
            return response.status == 201;
        }catch (IOException e){
            return false;
        }
    }
    public static boolean deleteCollection(String userID, String collectionName){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.deletePlantCollection(userID, collectionName);
            return response.status == 200;
        }catch (Exception e){
            return false;
        }
    }
}
