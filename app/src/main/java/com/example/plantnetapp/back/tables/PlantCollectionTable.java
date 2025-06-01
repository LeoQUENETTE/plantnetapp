package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

public class PlantCollectionTable extends Table{
    private static PlantCollectionTable INSTANCE = null;
    private PlantCollectionTable(SQLiteDatabase db) {
        TABLE_NAME = "PlantCollection";
        database = db;
        if (!tableExist()){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db){
        if (INSTANCE == null){
            INSTANCE = new PlantCollectionTable(db);
        }
    }

    public static PlantCollectionTable getInstance(){
        return INSTANCE;
    }

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(" +
                "id TEXT PRIMARY KEY," +
                "userID TEXT NOT NULL," +
                "collectionName TEXT NOT NULL," +
                "FOREIGN KEY (userID) REFERENCES users(id));";
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
