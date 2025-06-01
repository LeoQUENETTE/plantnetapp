package com.example.plantnetapp.back;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.plantnetapp.back.tables.PlantCollectionTable;
import com.example.plantnetapp.back.tables.PlantTable;
import com.example.plantnetapp.back.tables.Table;
import com.example.plantnetapp.back.tables.UserTable;

import java.util.Objects;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper INSTANCE = null;
    private static SQLiteDatabase database = null;
    private static final String BDD_NAME = "PlantNet_BDD";
    private static final int BDD_VERSION = 1;
    private DBHelper(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, BDD_NAME, factory, BDD_VERSION);
        database =this.getWritableDatabase();
        initializeTables();
    }

    public static DBHelper getInstance(@Nullable Context context, @Nullable SQLiteDatabase.CursorFactory factory){
        if (INSTANCE == null){
            INSTANCE = new DBHelper(context, factory);
        }
        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public static void onClose() {
        if (database != null && database.isOpen()){
            database.close();
        }
    }

    public void initializeTables(){
        UserTable.createInstance(database);
        PlantTable.createInstance(database);
        PlantCollectionTable.createInstance(database);
    }

    public void dropTables(){
        try{
            if (UserTable.getInstance().tableExist()){
                UserTable.dropTable(UserTable.getInstance());
            }
            if (PlantTable.getInstance().tableExist()){
                PlantTable.dropTable(PlantTable.getInstance());
            }
            if (PlantCollectionTable.getInstance().tableExist()){
                PlantCollectionTable.dropTable(PlantCollectionTable.getInstance());
            }
        }catch (Exception e){
            Log.d("DROP TABLE ERROR", Objects.requireNonNull(e.getMessage()));
        }

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        database = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
