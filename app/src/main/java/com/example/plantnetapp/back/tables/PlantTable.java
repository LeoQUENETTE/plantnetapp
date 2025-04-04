package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

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
