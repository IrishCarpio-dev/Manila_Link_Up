package com.manilalinkup.app.models;

import java.util.List;

public class SeekerPreferencesModel {
    private Double preferredSalary;
    private String preferredDuration;
    private String preferredLocation;
    private List<String> serviceTags;

    public Double getPreferredSalary() { return preferredSalary; }
    public String getPreferredDuration() { return preferredDuration; }
    public String getPreferredLocation() { return preferredLocation; }
    public List<String> getServiceTags() { return serviceTags; }
}
