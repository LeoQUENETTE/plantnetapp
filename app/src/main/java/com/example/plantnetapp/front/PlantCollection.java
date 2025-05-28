package com.example.plantnetapp.front;

import java.io.Serializable;
import java.util.Objects;

/**
 * Représente une collection de plantes (id + nom).
 */
public class PlantCollection implements Serializable {
    private final int id;
    private final String name;

    public PlantCollection(int id, String name) {
        this.id   = id;
        this.name = name;
    }

    /** L’identifiant unique de la collection */
    public int getId() {
        return id;
    }

    /** Le nom de la collection */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlantCollection)) return false;
        PlantCollection that = (PlantCollection) o;
        return id == that.id &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "PlantCollection{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
