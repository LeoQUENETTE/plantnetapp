package com.example.plantnetapp.back.entity;

public abstract class Entity {
    public int id = -1;
    public abstract boolean equalsWithoutId(Object o);
}
