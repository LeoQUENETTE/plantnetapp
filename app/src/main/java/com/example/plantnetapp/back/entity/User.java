package com.example.plantnetapp.back.entity;

import com.example.plantnetapp.back.api.ExternalBDDApi;
import com.example.plantnetapp.back.api.ReturnType;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public class User extends Entity implements Serializable {
    public String id;
    public final String login;
    public String mdp;
    public final String firstName;
    public final String lastName;
    public final String mail;
    public final String phone;

    public User(String id, String login, String mdp, String firstName, String lastName, String mail, String phone) {
        this.id = id;
        this.login = login;
        this.mdp = mdp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.phone = phone;
    }
    public boolean equalsWithoutId(Object o){
        if (this == o) return false;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(login, user.login) && Objects.equals(mdp, user.mdp) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(mail, user.mail) && Objects.equals(phone, user.phone);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        if (user.id.isEmpty() || Objects.equals(this.id, "")){
            return equalsWithoutId(user);
        }
        return id == user.id && Objects.equals(login, user.login) && Objects.equals(mdp, user.mdp) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(mail, user.mail) && Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, mdp, firstName, lastName, mail, phone);
    }

    public static User userFromJSON(JsonObject object){
        String id = object.getAsJsonObject("User").get("id").getAsString();
        String username = object.getAsJsonObject("User").get("username").getAsString();
        String pswrd = object.getAsJsonObject("User").get("pswrd").getAsString();
        String email = object.getAsJsonObject("User").get("email").getAsString();
        String firstname = object.getAsJsonObject("User").get("firstname").getAsString();
        String lastname = object.getAsJsonObject("User").get("lastname").getAsString();
        String phone = object.getAsJsonObject("User").get("phone").getAsString();
        return new User(id, username, pswrd,firstname, lastname, email, phone);
    }

    public static User login(String username ,String pswrd){
        ExternalBDDApi api = ExternalBDDApi.createInstance();
        try{
            ReturnType response = api.login(username, pswrd);
            if (response.status != 200 || response.values == null){
                return null;
            }
            return User.userFromJSON(response.values);
        }catch (Exception e){
            return null;
        }
    }

    public static boolean deleteUser(String userID){
        ExternalBDDApi api = ExternalBDDApi.createInstance();
        try{
            ReturnType response = api.deleteUser(userID);
            return response.status == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static String addUser(User user){
        ExternalBDDApi api = ExternalBDDApi.createInstance();
        try{
            ReturnType response = api.addUser(user);
            return response.values.get("id").getAsString();
        } catch (Exception e) {
            return null;
        }
    }
    public static void addUserWithID(User user){
        ExternalBDDApi api = ExternalBDDApi.createInstance();
        try{
            ReturnType response = api.addUserWithID(user);
        } catch (IOException e) {
            return;
        }
    }
}
