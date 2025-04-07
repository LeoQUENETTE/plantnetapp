package com.example.plantnetapp.back.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

public abstract class Table {
    protected static SQLiteDatabase database = null;
    public static String TABLE_NAME;
    public void setDatabase(SQLiteDatabase db){
        database = db;
    }
    public boolean isDatabaseSet(){
        return database != null;
    }
    public abstract void createTable();
    public static void deleteTable(String tableName){
        String query = "DELETE FROM "+tableName;
        database.execSQL(query);
    }
    public static void dropTable(String tableName){
        String query = "DROP TABLE IF EXISTS "+tableName;
        database.execSQL(query);
    }
    public abstract void addData(Entity entity) throws Exception;
    public abstract void deleteData(Entity entity) throws Exception;
    public abstract Entity selectData(int id) throws Exception;
    public abstract List<Entity> selectAllData() throws Exception;
    public static int getTotalNbRows(String tableName) {
        String query = "SELECT * FROM "+tableName;
        Cursor cursor = database.rawQuery(query, null);
        int value = cursor.getCount();
        cursor.close();
        return value;
    }
    public abstract void updateData();
}
