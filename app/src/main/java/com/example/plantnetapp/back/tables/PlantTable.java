package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

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
                "    id INTEGER PRIMARY KEY AUTOINCREMENT," +
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

    }

    @Override
    public void deleteData(Entity entity) {

    }

    @Override
    public Entity selectData(String id) throws Exception {
        return null;
    }

    @Override
    public List<Entity> selectAllData() {

        return null;
    }

    @Override
    public void updateData() {

    }
}
