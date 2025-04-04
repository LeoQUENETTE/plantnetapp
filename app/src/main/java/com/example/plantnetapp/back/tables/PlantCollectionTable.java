package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

public class PlantCollectionTable extends Table{
    private static PlantCollectionTable INSTANCE = null;
    public static String TABLE_NAME = "PlantCollection";
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
    public void dropTable() {

    }

    @Override
    public void addData() {

    }

    @Override
    public void deleteData() {

    }

    @Override
    public void selectData() {

    }

    @Override
    public void updateData() {

    }
}
