package com.example.plantnetapp.front;

import java.io.Serializable;

/**
 * Représente une entrée d'historique : nom de la plante + date de photo.
 */
public class HistoryEntry implements Serializable {
    private final String name;
    private final String date;

    public HistoryEntry(String name, String date) {
        this.name = name;
        this.date = date;
    }

    /** Getter pour le nom de la plante */
    public String getName() {
        return name;
    }

    /** Getter pour la date */
    public String getDate() {
        return date;
    }
}
