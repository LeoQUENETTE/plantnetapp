// app/src/main/java/com/example/plantnetapp/front/Plant.java
package com.example.plantnetapp.front;

import java.io.Serializable;

public class UselessPlant implements Serializable {
    private String name;
    private String description;
    private String imageUrl;

    public UselessPlant(String name, String description, String imageUrl) {
        this.name        = name;
        this.description = description;
        this.imageUrl    = imageUrl;
    }

    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getImageUrl()    { return imageUrl; }
}
