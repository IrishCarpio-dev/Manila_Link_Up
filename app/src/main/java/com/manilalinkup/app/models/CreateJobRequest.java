package com.manilalinkup.app.models;

import java.util.List;

public class CreateJobRequest {
    String title, description, employer, expiresAt, duration, location;
    Double salary;
    List<String> tags;

    public CreateJobRequest(
            String title,
            String description,
            String employer,
            String expiresAt,
            String duration,
            String location,
            Double salary,
            List<String> tags
    ) {
        this.title = title;
        this.description = description;
        this.employer = employer;
        this.expiresAt = expiresAt;
        this.duration = duration;
        this.location = location;
        this.salary = salary;
        this.tags = tags;
    }
}
