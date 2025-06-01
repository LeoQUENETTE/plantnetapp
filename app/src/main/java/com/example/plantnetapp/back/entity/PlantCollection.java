package com.example.plantnetapp.back.entity;

import androidx.annotation.NonNull;

import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.api.ReturnType;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlantCollection extends Entity implements Serializable {
    public String id = "";
    public final String userID;
    public List<Plant> plantList = null;
    public final int nbPlant = 0;

    public String name;

    public PlantCollection(String id, String userID,String name, List<Plant> plantList){
        this.id = id;
        this.userID = userID;
        this.name = name;
        this.plantList = plantList;
    }
    public PlantCollection(String userID,String name,List<Plant> plantList){
        this.userID = userID;
        this.name = name;
        this.plantList = plantList;
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
        return Objects.equals(plantList, that.plantList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantList, nbPlant);
    }

    public static PlantCollection plantCollectionFromJSON(JsonObject object){
        String id = object.getAsJsonObject("PlantCollection").get("id").getAsString();
        String userID = object.getAsJsonObject("PlantCollection").get("userID").getAsString();
        String name = object.getAsJsonObject("PlantCollection").get("name").getAsString();
        return new PlantCollection(id,userID, name,null);
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
    public static List<PlantCollection> getAllCollections(String userID){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getAllPlantCollections(userID);
            List<JsonElement> plantCollectionsJsonObject = response.values.getAsJsonArray("PlantCollections").asList();
            if (!plantCollectionsJsonObject.isEmpty()){
                return getPlantCollections(plantCollectionsJsonObject, false);
            }else{
                return null;
            }
        }catch (IOException e){
            return null;
        }
    }

    public static List<PlantCollection> getCollectionsNoHistory(String userID){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.getAllPlantCollections(userID);
            List<JsonElement> plantCollectionsJsonObject = response.values.getAsJsonArray("PlantCollections").asList();
            if (!plantCollectionsJsonObject.isEmpty() && plantCollectionsJsonObject.size() - 1 > 0){
                return getPlantCollections(plantCollectionsJsonObject, true);
            }else{
                return null;
            }
        }catch (IOException e){
            return null;
        }
    }

    @NonNull
    private static ArrayList<PlantCollection> getPlantCollections(List<JsonElement> plantCollectionsJsonObject, boolean noHistory) {
        ArrayList<PlantCollection> plantCollections = new ArrayList<>();
        for (JsonElement plantColJsonEl : plantCollectionsJsonObject){
            JsonObject plantColJsonO = plantColJsonEl.getAsJsonObject();
            String id  = plantColJsonO.get("id").getAsString();
            String userID = plantColJsonO.get("userID").getAsString();
            String name  = plantColJsonO.get("name").getAsString();
            if (noHistory && !Objects.equals(name, "history")){
                PlantCollection plantCol = new PlantCollection(id,userID,name ,null);
                plantCollections.add(plantCol);
            } else if (!noHistory) {
                PlantCollection plantCol = new PlantCollection(id,userID,name ,null);
                plantCollections.add(plantCol);
            }
        }
        return plantCollections;
    }

    public static String addCollection(String userID, String collectionName){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.addPlantCollection(userID, collectionName);
            return response.values.get("id").getAsString();
        }catch (IOException e){
            return null;
        }
    }
    public static void addCollection(String userID, PlantCollection col){
        ExternalBDDApi bdd = ExternalBDDApi.createInstance();
        try{
            ReturnType response = bdd.addPlantCollectionWithID(userID, col);
            response.values.get("id").getAsString();
        }catch (IOException ignored){
            return;
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
