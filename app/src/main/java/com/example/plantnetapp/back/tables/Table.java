package com.example.plantnetapp.back.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

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
    public void deleteTable(){
        String query = "DELETE FROM "+TABLE_NAME;
        database.execSQL(query);
    }
    public void dropTable(){
        String query = "DROP TABLE IF EXISTS "+TABLE_NAME;
        database.execSQL(query);
    }
    public abstract void addData(Entity entity) throws Exception;
    public abstract void deleteData(Entity entity) throws Exception;
    public abstract Entity selectData(int id) throws Exception;
    public abstract List<Entity> selectAllData() throws Exception;
    public int getTotalNbRows(String tableName) {
        String query = "SELECT * FROM "+tableName;
        Cursor cursor = database.rawQuery(query, null);
        int value = cursor.getCount();
        cursor.close();
        return value;
    }
    public abstract void updateData();
}
