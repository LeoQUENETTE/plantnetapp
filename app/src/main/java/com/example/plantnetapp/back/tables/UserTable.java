package com.example.plantnetapp.back.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.entity.Entity;
import com.example.plantnetapp.back.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserTable extends Table{
    private static UserTable INSTANCE = null;
    public static final String TABLE_NAME = "User";
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

    public static void destroyInstance(){
        INSTANCE = null;
    }

    public static UserTable getInstance(){
        return INSTANCE;
    }
    @Override
    public void createTable() {
        String query = "CREATE TABLE "+TABLE_NAME+" ("
                +"id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"login TEXT NOT NULL,"
                +"mdp TEXT NOT NULL,"
                +"firstName TEXT,"
                +"lastName TEXT,"
                +"role TEXT NOT NULL,"
                +"mail TEXT,"
                +"phone TEXT"
                +")";
        database.execSQL(query);
    }

    public static User login(String login, String mdp) throws Exception {
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE login=? AND mdp=?";
        String[] bindArgs = {login, mdp};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Exception("Empty Table "+TABLE_NAME);
        }
        int id = cursor.getInt(0);
        String firstName = cursor.getString(3);
        String lastName = cursor.getString(4);
        String role = cursor.getString(5);
        String mail = cursor.getString(6);
        String phone = cursor.getString(7);
        User user = new User(id, login, mdp, firstName, lastName, role, mail, phone);
        cursor.close();
        return user;
    }

    @Override
    public void addData(Entity entity) throws Exception {
        if (entity.getClass() != User.class){
            throw new Exception("You can only inserts User type inside the User table.");
        }
        User user = (User) entity;
        String query = "INSERT INTO "+TABLE_NAME+" (login, mdp, firstName,lastName,role, mail, phone) VALUES (?, ?, ?, ?, ?, ?, ?);";
        Object[] bindArgs = {user.login, user.mdp, user.firstName, user.lastName, user.role,user.mail, user.phone};
        database.execSQL(query, bindArgs);
    }

    @Override
    public void deleteData(Entity entity) throws Exception {
        if (entity.getClass() != User.class){
            throw new Exception("You can only use User type to delete data inside User table.");
        }
        User user = (User) entity;
        String query;
        Object[] bindArgs;
        if (user.id == -1){
            query = "DELETE FROM " + TABLE_NAME + " WHERE login=? AND mdp =? AND firstName=? AND lastName=? AND role=? AND mail=? AND phone=?";
            bindArgs = new Object[]{user.login, user.mdp, user.firstName, user.lastName, user.role, user.mail, user.phone};
        }else{
            query = "DELETE FROM " + TABLE_NAME + " WHERE id=?";
            bindArgs = new Object[]{user.id};
        }
        database.execSQL(query, bindArgs);
    }

    @Override
    public Entity selectData(int id) throws Exception{
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE id=?";
        String[] bindArgs = {String.valueOf(id)};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Exception("Empty Table "+TABLE_NAME);
        }
        String login = cursor.getString(1);
        String mdp = cursor.getString(2);
        String firstName = cursor.getString(3);
        String lastName = cursor.getString(4);
        String role = cursor.getString(5);
        String mail = cursor.getString(6);
        String phone = cursor.getString(7);
        User user = new User(id, login, mdp, firstName, lastName, role, mail, phone);
        cursor.close();
        return user;
    }

    @Override
    public List<Entity> selectAllData() throws Exception {
        List<Entity> userList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        if (!cursor.moveToFirst()){
            throw new Exception("Empty Table "+TABLE_NAME);
        }
        for (int i = 0; i < cursor.getCount(); i++){
            int id = cursor.getInt(0);
            String login = cursor.getString(1);
            String mdp = cursor.getString(2);
            String firstName = cursor.getString(3);
            String lastName = cursor.getString(4);
            String role = cursor.getString(5);
            String mail = cursor.getString(6);
            String phone = cursor.getString(7);
            User user = new User(id, login, mdp, firstName, lastName, role, mail, phone);
            userList.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return userList;
    }

    @Override
    public void updateData() {

    }
}
