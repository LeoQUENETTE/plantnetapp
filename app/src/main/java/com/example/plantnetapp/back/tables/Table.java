package com.example.plantnetapp.back.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.plantnetapp.back.entity.Entity;

import java.util.List;

public abstract class Table {
    protected static SQLiteDatabase database = null;
    public String TABLE_NAME;
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
    public static void dropTable(Table table){
        String query = "DROP TABLE IF EXISTS "+ table.TABLE_NAME;
        database.execSQL(query);
    }
    public abstract void addData(Entity entity) throws Exception;
    public abstract void deleteData(Entity entity) throws Exception;
    public abstract Entity selectData(String id) throws Exception;
    public abstract List<Entity> selectAllData() throws Exception;
    public static int getTotalNbRows(String tableName) {
        String query = "SELECT * FROM "+tableName;
        Cursor cursor = database.rawQuery(query, null);
        int value = cursor.getCount();
        cursor.close();
        return value;
    }
    public String getTableName(){
        return this.TABLE_NAME;
    }
    public abstract void updateData();
    public boolean tableExist(){
        if (database == null || !database.isOpen()) {
            return false;
        }
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name=?";
        try (Cursor cursor = database.rawQuery(query, new String[]{TABLE_NAME})) {
            return cursor.moveToFirst();
        } catch(Exception e) {
            Log.e("DBHelper", "Error checking table existence", e);
            return false;
        }
    }
}
