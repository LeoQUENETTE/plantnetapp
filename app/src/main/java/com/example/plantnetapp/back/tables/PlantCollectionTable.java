package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

public class PlantCollectionTable extends Table{
    private static PlantCollectionTable INSTANCE = null;
    public static final String TABLE_NAME = "PlantCollection";
    private PlantCollectionTable(SQLiteDatabase db, boolean tableExist) {
        database = db;
        if (!tableExist){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db,  boolean tableExist){
        if (INSTANCE == null){
            INSTANCE = new PlantCollectionTable(db, tableExist);
        }
    }

    public static PlantCollectionTable getInstance(){
        return INSTANCE;
    }

    @Override
    public void createTable() {

    }

    @Override
    public void addData(Entity entity) {

    }

    @Override
    public void deleteData(Entity entity) {

    }

    @Override
    public Entity selectData(int id) throws Exception {
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
