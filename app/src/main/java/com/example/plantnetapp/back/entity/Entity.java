package com.example.plantnetapp.back.entity;

public abstract class Entity {
    public String id = "";
    public abstract boolean equalsWithoutId(Object o);
}
