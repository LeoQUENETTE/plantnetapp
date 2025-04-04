package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

public class UserTable extends Table{
    private static UserTable INSTANCE = null;
    public static String TABLE_NAME = "User";
    private UserTable(SQLiteDatabase db) {
        database = db;
    }
    public static void createInstance(SQLiteDatabase db){
        if (INSTANCE == null){
            INSTANCE = new UserTable(db);
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
