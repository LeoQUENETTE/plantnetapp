package com.example.plantnetapp.back.entity;

import java.util.List;
import java.util.Objects;

public class PlantCollection extends Entity{
    public List<Plant> plantList = null;
    public final int nbPlant = 0;

    public String name;

    public PlantCollection(int id, String name, List<Plant> plantList){
        this.id = id;
        this.plantList = plantList;
        this.name = name;
    }
    public PlantCollection(String name,List<Plant> plantList){
        this.plantList = plantList;
        this.name = name;
    }
    @Override
    public boolean equalsWithoutId(Object o) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantCollection that = (PlantCollection) o;
        return nbPlant == that.nbPlant && Objects.equals(plantList, that.plantList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plantList, nbPlant);
    }
}
