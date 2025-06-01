package com.example.plantnetapp.back.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;
import com.example.plantnetapp.back.entity.PlantCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlantCollectionTable extends Table{
    private static PlantCollectionTable INSTANCE = null;
    private PlantCollectionTable(SQLiteDatabase db) {
        TABLE_NAME = "PlantCollection";
        database = db;
        if (!tableExist()){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db){
        if (INSTANCE == null){
            INSTANCE = new PlantCollectionTable(db);
        }
    }

    public static PlantCollectionTable getInstance(){
        return INSTANCE;
    }

    @Override
    public void createTable() {
        String query = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+"(" +
                "id TEXT PRIMARY KEY," +
                "userID TEXT NOT NULL," +
                "collectionName TEXT NOT NULL," +
                "FOREIGN KEY (userID) REFERENCES users(id));";
        database.execSQL(query);
    }

    @Override
    public void addData(Entity entity) {
        if (entity instanceof PlantCollection){
            PlantCollection plantCollection = (PlantCollection) entity;
            if (plantCollection.id == null ||Objects.equals(plantCollection.id, "")){
                plantCollection.id =  UUID.randomUUID().toString();
            }
            addCollectionWithID(plantCollection);
        }
    }

    public void addCollectionWithID(PlantCollection col){
        String query = "INSERT INTO "+TABLE_NAME+" (id, userID, collectionName) VALUES (?, ?, ?);";
        Object[] bindArgs = {col.id, col.userID, col.name};
        database.execSQL(query, bindArgs);
    }

    @Override
    public void deleteData(Entity entity) {

    }

    @Override
    public Entity selectData(String id) throws Exception {
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE id=?";
        String[] bindArgs = {String.valueOf(id)};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Table.EmptyTableException("Empty Table "+TABLE_NAME);
        }
        String userID = cursor.getString(1);
        String name = cursor.getString(2);
        PlantCollection col = new PlantCollection(id, userID, name, null);
        cursor.close();
        return col;
    }
    public List<PlantCollection> selectAllWithoutHistory(String userID) throws EmptyTableException {
        List<PlantCollection> collections = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE userID = ? AND name!= ?";
        String[] bindArgs = { userID, "history" };

        Cursor cursor = database.rawQuery(query, bindArgs);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String fetchedUserID = cursor.getString(1);
                String name = cursor.getString(2);

                PlantCollection col = new PlantCollection(id, fetchedUserID, name, null);
                collections.add(col);
            } while (cursor.moveToNext());
        }else{
            throw new Table.EmptyTableException("Empty Table "+TABLE_NAME);
        }
        cursor.close();
        return collections;
    }
    public List<PlantCollection> selectAll(String userID) throws EmptyTableException {
        List<PlantCollection> collections = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE userID = ?";
        String[] bindArgs = { userID};

        Cursor cursor = database.rawQuery(query, bindArgs);
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(0);
                String fetchedUserID = cursor.getString(1);
                String name = cursor.getString(2);

                PlantCollection col = new PlantCollection(id, fetchedUserID, name, null);
                collections.add(col);
            } while (cursor.moveToNext());
        }else{
            throw new Table.EmptyTableException("Empty Table "+TABLE_NAME);
        }
        cursor.close();
        return collections;
    }

    public void deleteCollection(String userID, String collectionName){
        String query = "DELETE FROM " + TABLE_NAME + " WHERE userID = ? AND collectionName = ?";
        Object[] bindArgs = { userID, collectionName };

        database.execSQL(query, bindArgs);

        // Check how many rows are affected
        Cursor cursor = database.rawQuery(
                "SELECT changes()", null
        );
        cursor.close();
    }

    @Override
    public List<Entity> selectAllData() {

        return null;
    }

    @Override
    public void updateData() {

    }
}
