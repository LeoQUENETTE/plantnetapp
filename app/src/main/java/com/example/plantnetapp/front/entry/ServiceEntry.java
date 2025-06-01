package com.example.plantnetapp.front.entry;

import java.io.Serializable;

public class ServiceEntry implements Serializable {
    private final String service;
    private final String value;
    private final String reliability;
    private final String culturalCondition;

    public ServiceEntry(String service, String value, String reliability, String culturalCondition) {
        this.service = service;
        this.value = value;
        this.reliability = reliability;
        this.culturalCondition = culturalCondition;
    }
    public String getService()           { return service; }
    public String getValue()             { return value; }
    public String getReliability()       { return reliability; }
    public String getCulturalCondition() { return culturalCondition; }
}
