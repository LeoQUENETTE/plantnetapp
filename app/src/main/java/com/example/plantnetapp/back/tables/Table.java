package com.example.plantnetapp.back.tables;

import android.database.sqlite.SQLiteDatabase;

public abstract class Table {
    protected SQLiteDatabase database = null;
    public static String TABLE_NAME;
    public void setDatabase(SQLiteDatabase db){
        database = db;
    }
    public boolean isDatabaseSet(){
        return database != null;
    }
    public abstract void createTable();
    public abstract void dropTable();
    public abstract void addData();
    public abstract void deleteData();
    public abstract void selectData();
    public abstract void updateData();
}
