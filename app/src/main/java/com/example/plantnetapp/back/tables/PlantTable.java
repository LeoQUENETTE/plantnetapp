package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

public class PlantTable extends Table{
    private static PlantTable INSTANCE = null;
    public static String TABLE_NAME = "Plant";
    private PlantTable(SQLiteDatabase db,  boolean tableExist) {
        database = db;
        if (!tableExist){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db ,  boolean tableExist){
        if (INSTANCE == null){
            INSTANCE = new PlantTable(db, tableExist);
        }
    }

    public static PlantTable getInstance(){
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
