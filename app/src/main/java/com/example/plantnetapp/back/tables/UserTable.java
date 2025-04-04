package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

public class UserTable extends Table{
    private static UserTable INSTANCE = null;
    public static String TABLE_NAME = "User";
    private UserTable(SQLiteDatabase db,  boolean tableExist) {
        database = db;
        if (!tableExist){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db, boolean tableExist){
        if (INSTANCE == null){
            INSTANCE = new UserTable(db, tableExist);
        }
    }

    public static UserTable getInstance(){
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
