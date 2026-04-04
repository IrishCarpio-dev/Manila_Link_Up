package com.manilalinkup.app.models;

public class UserProfileModel {
    SeekerProfileModel seekers;
    EmployerProfileModel employers;

    public UserProfileModel(
            SeekerProfileModel seekers,
            EmployerProfileModel employers
    ) {
        this.seekers = seekers;
        this.employers = employers;
    }

    public SeekerProfileModel getSeekers() { return seekers; }

    public EmployerProfileModel getEmployers() { return employers; }
}
