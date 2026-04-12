package com.manilalinkup.app.models;

public class CreateJobRequest {
    String title, description, employer, expiresAt, duration, location;

    Double salary;

    public CreateJobRequest(
            String title,
            String description,
            String employer,
            String expiresAt,
            String duration,
            String location,
            Double salary
    ) {
        this.title = title;
        this.description = description;
        this.employer = employer;
        this.expiresAt = expiresAt;
        this.duration = duration;
        this.location = location;
        this.salary = salary;
    }

}
