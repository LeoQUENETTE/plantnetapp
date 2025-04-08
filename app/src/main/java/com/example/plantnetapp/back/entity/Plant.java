package com.example.plantnetapp.back.entity;

import java.util.Objects;

public class Plant  extends Entity{
    public String name;
    public float azoteFixing = 0;
    public float upgradeGrnd = 0;
    public float waterFixing = 0;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plant plant = (Plant) o;
        return Float.compare(azoteFixing, plant.azoteFixing) == 0 && Float.compare(upgradeGrnd, plant.upgradeGrnd) == 0 && Float.compare(waterFixing, plant.waterFixing) == 0 && Objects.equals(name, plant.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, azoteFixing, upgradeGrnd, waterFixing);
    }

    public Plant(int id, String name, float azoteFixing, float upgradeGrnd, float waterFixing) {
        this.id = id;
        this.name = name;
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
    }
    public Plant(String name, float azoteFixing, float upgradeGrnd, float waterFixing) {
        this.name = name;
        this.azoteFixing = azoteFixing;
        this.upgradeGrnd = upgradeGrnd;
        this.waterFixing = waterFixing;
    }

    @Override
    public boolean equalsWithoutId(Object o) {
        return false;
    }
}
