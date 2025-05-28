package com.example.plantnetapp.back.entity;

import java.util.Objects;

public class User extends Entity{
    public final String login;
    public final String mdp;
    public final String firstName;
    public final String lastName;
    public final String mail;
    public final String phone;

    public User(int id, String login, String mdp, String firstName, String lastName, String mail, String phone) {
        this.id = id;
        this.login = login;
        this.mdp = mdp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.phone = phone;
    }
    public User(String login, String mdp, String firstName, String lastName, String mail, String phone) {
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
        if (user.id == -1 || this.id == -1){
            return equalsWithoutId(user);
        }
        return id == user.id && Objects.equals(login, user.login) && Objects.equals(mdp, user.mdp) && Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName) && Objects.equals(mail, user.mail) && Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, mdp, firstName, lastName, mail, phone);
    }
}
