package com.example.plantnetapp.back.tables;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.plantnetapp.back.DBHelper;
import com.example.plantnetapp.back.entity.Entity;
import com.example.plantnetapp.back.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserTable extends Table{
    private static UserTable INSTANCE = null;
    private UserTable(SQLiteDatabase db) {
       TABLE_NAME = "User";
        database = db;
        if (!tableExist()){
            createTable();
        }
    }
    public static void createInstance(SQLiteDatabase db){
        if (INSTANCE == null){
            INSTANCE = new UserTable(db);
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
                +"id TEXT PRIMARY KEY,"
                +"login TEXT NOT NULL,"
                +"mdp TEXT NOT NULL,"
                +"firstName TEXT,"
                +"lastName TEXT,"
                +"mail TEXT,"
                +"phone TEXT);";
        database.execSQL(query);
    }

    public User login(String login, String mdp) throws Exception {
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE login=? AND mdp=?";
        String[] bindArgs = {login, mdp};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Exception("Empty Table "+TABLE_NAME);
        }
        String id = cursor.getString(0);
        String firstName = cursor.getString(3);
        String lastName = cursor.getString(4);
        String role = cursor.getString(5);
        String mail = cursor.getString(6);
        String phone = cursor.getString(7);
        User user = new User(id, login, mdp, firstName, lastName, mail, phone);
        cursor.close();
        return user;
    }

    @Override
    public void addData(Entity entity) throws Exception {
        if (!(entity instanceof User)){
            throw new Exception("You can only inserts User type inside the User table.");
        }
        User user = (User) entity;
        if (user.id == null || user.id.isEmpty()){
            user.id = UUID.randomUUID().toString();
        }
        addUserWithID(user);
    }

    public void addUserWithID(User user){
        String query = "INSERT INTO "+TABLE_NAME+" (id, login, mdp, firstName,lastName, mail, phone) VALUES (?, ?, ?, ?, ?, ?, ?);";
        Object[] bindArgs = {user.id, user.login, user.mdp, user.firstName, user.lastName,user.mail, user.phone};
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
        if (Objects.equals(user.id, "")){
            query = "DELETE FROM " + TABLE_NAME + " WHERE login=? AND mdp =? AND firstName=? AND lastName=? AND role=? AND mail=? AND phone=?";
            bindArgs = new Object[]{user.login, user.mdp, user.firstName, user.lastName, "", user.mail, user.phone};
        }else{
            query = "DELETE FROM " + TABLE_NAME + " WHERE id=?";
            bindArgs = new Object[]{user.id};
        }
        database.execSQL(query, bindArgs);
    }

    @Override
    public Entity selectData(String id) throws Exception{
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE id=?";
        String[] bindArgs = {String.valueOf(id)};
        Cursor cursor = database.rawQuery(query, bindArgs);
        if (!cursor.moveToFirst()){
            throw new Table.EmptyTableException("Empty Table "+TABLE_NAME);
        }
        String login = cursor.getString(1);
        String mdp = cursor.getString(2);
        String firstName = cursor.getString(3);
        String lastName = cursor.getString(4);
        String mail = cursor.getString(5);
        String phone = cursor.getString(6);
        User user = new User(id, login, mdp, firstName, lastName, mail, phone);
        cursor.close();
        return user;
    }

    @Override
    public List<Entity> selectAllData() throws Exception {
        List<Entity> userList = new ArrayList<>();
        String query = "SELECT * FROM "+TABLE_NAME;
        Cursor cursor = database.rawQuery(query, null);
        if (!cursor.moveToFirst()){
            throw new EmptyTableException("Empty Table "+TABLE_NAME);
        }
        for (int i = 0; i < cursor.getCount(); i++){
            String id = cursor.getString(0);
            String login = cursor.getString(1);
            String mdp = cursor.getString(2);
            String firstName = cursor.getString(3);
            String lastName = cursor.getString(4);
            String mail = cursor.getString(5);
            String phone = cursor.getString(6);
            User user = new User(id, login, mdp, firstName, lastName, mail, phone);
            userList.add(user);
            cursor.moveToNext();
        }
        cursor.close();
        return userList;
    }

    public boolean deleteUser(String userID){
        String query = "DELETE FROM " + TABLE_NAME + " WHERE id = ?";
        Object[] bindArgs = { userID };

        database.execSQL(query, bindArgs);

        // Check how many rows are affected
        Cursor cursor = database.rawQuery(
                "SELECT changes()", null
        );

        boolean deleted = false;
        if (cursor.moveToFirst()) {
            int rowsAffected = cursor.getInt(0);
            deleted = rowsAffected > 0;
        }
        cursor.close();
        return deleted;
    }

    @Override
    public void updateData() {

    }
}
