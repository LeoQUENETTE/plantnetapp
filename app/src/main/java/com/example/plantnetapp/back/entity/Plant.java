package com.example.plantnetapp.back.entity;

import android.os.Build;
import android.util.Log;

import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

public class Plant  extends Entity{
    public final String name;
    public float azoteFixing = 0;
    public float upgradeGrnd = 0;
    public float waterFixing = 0;
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

    public static Plant plantFromJSON (File directory, JsonObject object){
        JsonObject plantObject = object.getAsJsonObject("plant");
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
}
