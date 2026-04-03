package com.manilalinkup.app;

public class UserProfileModel {
    public SeekerProfileModel seekers;
    public EmployerProfileModel employers;

    public UserProfileModel(
            SeekerProfileModel seekers,
            EmployerProfileModel employers
    ) {
        this.seekers = seekers;
        this.employers = employers;
    }
}
