package com.manilalinkup.app.models;

public class JobModel {
    String id, title, description, expiresAt, duration, location;
    Double salary;

    public JobModel(
            String id,
            String title,
            String description,
            String expiresAt,
            String duration,
            String location,
            Double salary
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.expiresAt = expiresAt;
        this.duration = duration;
        this.location = location;
        this.salary = salary;
    }

    public String getId() { return id; }

    public String getTitle() { return title; }

    public String getDescription() { return description; }

    public String getExpiresAt() { return expiresAt; }

    public String getDuration() { return duration; }

    public String getLocation() { return location; }

    public Double getSalary() { return salary; }
}
