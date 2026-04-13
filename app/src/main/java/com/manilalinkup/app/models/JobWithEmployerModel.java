package com.manilalinkup.app.models;

public class JobWithEmployerModel extends JobModel {
    EmployerProfileModel employer;

    public JobWithEmployerModel(
            String id,
            String title,
            String description,
            String expiresAt,
            String duration,
            String location,
            Double salary,
            EmployerProfileModel employer
    ) {
        super(id, title, description, expiresAt, duration, location, salary);
        this.employer = employer;
    }

    public EmployerProfileModel getEmployer() { return employer; }
}
