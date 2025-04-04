package com.example.plantnetapp.back;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.plantnetapp.back.tables.PlantCollectionTable;
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.back.tables.UserTable;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper INSTANCE = null;
    private SQLiteDatabase database = null;
    private static final String BDD_NAME = "PlantNet_BDD";
    private static final int BDD_VERSION = 1;
    private DBHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, BDD_NAME, factory, BDD_VERSION);
    }

    public static DBHelper getInstance(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory){
        if (INSTANCE == null){
            INSTANCE = new DBHelper(context, factory);
        }
        return INSTANCE;
    }

    public void onClose() {
        if (database != null){
            database.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.database = db;
        UserTable.createInstance(db, tableExist(UserTable.TABLE_NAME));
        PlantTable.createInstance(db, tableExist(PlantTable.TABLE_NAME));
        PlantCollectionTable.createInstance(db, tableExist(PlantCollectionTable.TABLE_NAME));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private boolean tableExist(String tableName){
        String query = "SELECT name FROM sqlite_master WHERE type='table' AND name='?';";
        try (Cursor cursor = database.rawQuery(
                query,
                new String[]{tableName}
        )) {
            return cursor.moveToFirst();
        } catch (Exception e) {
            throw e;
        }
    }
}
