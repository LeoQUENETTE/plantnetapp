package com.example.plantnetapp.back.tables;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;
import com.example.plantnetapp.back.entity.Plant;
import com.example.plantnetapp.back.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PlantTable extends Table{
    private static PlantTable INSTANCE = null;
    private PlantTable(SQLiteDatabase db) {
        TABLE_NAME = "Plant";
        database = db;
        if (!tableExist()){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db){
        if (INSTANCE == null){
            INSTANCE = new PlantTable(db);
        }
    }

    public static PlantTable getInstance(){
        return INSTANCE;
    }


    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+ "(" +
                "    id INTEGER PRIMARY KEY," +
                "    plantCollectionID TEXT NOT NULL," +
                "    plantName TEXT NOT NULL," +
                "    culturalCondition TEXT NOT NULL," +
                "    azoteFixing REAL NOT NULL," +
                "    upgradeGround REAL NOT NULL," +
                "    waterFixing REAL NOT NULL," +
                "    azoteFixingR REAL NOT NULL," +
                "    upgradeGroundR REAL NOT NULL," +
                "    waterFixingR REAL NOT NULL," +
                "    image_data BLOB NOT NULL," +
                "    FOREIGN KEY (plantCollectionID) REFERENCES plantCollections(id));";
        database.execSQL(query);
    }
    @Override
    public void addData(Entity entity) {
        if (entity instanceof Plant){
            Plant plant = (Plant) entity;
            if (plant.id == null || Objects.equals(plant.id, "")){
                addPlant(plant);
            }else {
                boolean plantExist = false;
                try {
                    try{
                        plantExist = selectData(plant.collectionID, plant.name) != null;
                    }catch (EmptyTableException ignored){
                    }
                    if (!plantExist){
                        addPlantWithID(plant);
                    }
                }catch (Exception e){
                    return;
                }
            }

        }
    }
    public void addPlant(Plant plant){
        String query = "INSERT INTO "+TABLE_NAME+" (" +
                "plantCollectionID, plantName, culturalCondition," +
                " azoteFixing, upgradeGround, waterFixing ,azoteFixingR," +
                "upgradeGroundR,waterFixingR,image_data)" +
                " VALUES (?, ?, ?," +
                " ?, ?, ?, ?," +
                "?,?,?);";
        Object[] bindArgs = {
                plant.collectionID, plant.name,plant.culturalCondition,
                plant.azoteFixing, plant.waterFixing, plant.upgradeReliability, plant.azoteReliability,
                plant.upgradeReliability, plant.waterReliability, plant.imageData
        };
        database.execSQL(query, bindArgs);
    }

    public void addPlantWithID(Plant plant){
        String query = "INSERT INTO "+TABLE_NAME+" (" +
                "id, plantCollectionID, plantName, culturalCondition," +
                " azoteFixing, upgradeGround, waterFixing ,azoteFixingR," +
                "upgradeGroundR,waterFixingR,image_data)" +
                " VALUES (?, ?, ?, ?," +
                " ?, ?, ?, ?," +
                "?,?,?);";
        Object[] bindArgs = {
                plant.id, plant.collectionID, plant.name,plant.culturalCondition,
                plant.azoteFixing, plant.waterFixing, plant.upgradeGrnd, plant.azoteReliability,
                plant.upgradeReliability, plant.waterReliability, plant.imageData
        };
        database.execSQL(query, bindArgs);
    }

    public boolean deletePlant(String collectionID, String plantName) {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE plantCollectionID = ? AND plantName = ?";
        Object[] bindArgs = { collectionID, plantName };

        database.execSQL(query, bindArgs);

        // Check how many rows are affected
        Cursor cursor = database.rawQuery(
                "SELECT changes()", null
        );

        boolean deleted = false;
        if (cursor.moveToFirst()) {
            int rowsAffected = cursor.getInt(0);
            deleted = rowsAffected > 0;
        }
        cursor.close();
        return deleted;
    }
    @Override
    public void deleteData(Entity entity) {

    }
    public Entity selectData(String collectionID, String plantName) throws Exception {
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE plantCollectionID=? AND plantName=?";
        String[] bindArgs = {collectionID,plantName};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Table.EmptyTableException("Empty Table "+TABLE_NAME);
        }
        String id = cursor.getString(0);
        String colID = cursor.getString(1);
        String name = cursor.getString(2);
        String culturalCondition = cursor.getString(3);
        float azoteFixing = cursor.getFloat(4);
        float waterFixing = cursor.getFloat(5);
        float upgradeGrnd = cursor.getFloat(6);
        float azoteReliability = cursor.getFloat(7);
        float upgradeReliability = cursor.getFloat(8);
        float waterReliability = cursor.getFloat(9);
        byte[] imageData = cursor.getBlob(10);
        Plant plant = new Plant(
                id, colID, name, culturalCondition,
                azoteFixing, waterFixing, upgradeGrnd,
                azoteReliability,upgradeReliability, waterReliability,imageData
        );
        cursor.close();
        return plant;
    }

    @Override
    public Entity selectData(String id) throws Exception {
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE id=?";
        String[] bindArgs = {String.valueOf(id)};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Table.EmptyTableException("Empty Table "+TABLE_NAME);
        }
        String collectionID = cursor.getString(1);
        String name = cursor.getString(2);
        String culturalCondition = cursor.getString(3);
        float azoteFixing = cursor.getFloat(4);
        float waterFixing = cursor.getFloat(5);
        float upgradeGrnd = cursor.getFloat(6);
        float azoteReliability = cursor.getFloat(7);
        float upgradeReliability = cursor.getFloat(8);
        float waterReliability = cursor.getFloat(9);
        byte[] imageData = cursor.getBlob(10);
        Plant plant = new Plant(
                id, collectionID, name, culturalCondition,
                azoteFixing, waterFixing, upgradeGrnd,
                azoteReliability,upgradeReliability, waterReliability,imageData
        );
        cursor.close();
        return plant;
    }

    @Override
    public List<Entity> selectAllData() throws Exception {
        return Collections.emptyList();
    }

    public List<Plant> selectAllData(String collectionID) {
        List<Plant> plants = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE plantCollectionID = ?";
        String[] bindArgs = { collectionID };

        Cursor cursor = database.rawQuery(query, bindArgs);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String colID = cursor.getString(1);
                String name = cursor.getString(2);
                String culturalCondition = cursor.getString(3);
                float azoteFixing = cursor.getFloat(4);
                float waterFixing = cursor.getFloat(5);
                float upgradeGrnd = cursor.getFloat(6);
                float azoteReliability = cursor.getFloat(7);
                float upgradeReliability = cursor.getFloat(8);
                float waterReliability = cursor.getFloat(9);
                byte[] imageData = cursor.getBlob(10);

                Plant plant = new Plant(
                        id, name, colID, culturalCondition,
                        azoteFixing, waterFixing, upgradeGrnd,
                        azoteReliability, upgradeReliability, waterReliability, imageData
                );
                plants.add(plant);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return plants;
    }

    @Override
    public void updateData() {

    }
}
