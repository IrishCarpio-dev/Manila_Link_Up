package com.manilalinkup.app.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SeekerPreferencesModel {
    @SerializedName(value = "preferredSalary", alternate = {"preferred_salary"})
    private Double preferredSalary;
    @SerializedName(value = "preferredLocation", alternate = {"preferred_location"})
    private String preferredLocation;
    @SerializedName("tags")
    private List<String> tags;

    public SeekerPreferencesModel(Double preferredSalary, String preferredLocation, List<String> tags) {
        this.preferredSalary = preferredSalary;
        this.preferredLocation = preferredLocation;
        this.tags = tags;
    }

    public Double getPreferredSalary() { return preferredSalary; }
    public String getPreferredLocation() { return preferredLocation; }
    public List<String> getTags() { return tags; }
}
