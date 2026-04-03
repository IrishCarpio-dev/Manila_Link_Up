package com.manilalinkup.app;

import java.util.Date;

public class SeekerProfileModel {
    public String firstName;
    public String lastName;
    public String email;
    public String mobileNumber;
    public String address;
    public Date birthDate;
    public String location;
    public String profilePhotoUrl;
    public Boolean isProfileSet;
    public Boolean isVerified;
    public Boolean isOpenForWork;

    public SeekerProfileModel(
            String firstName,
            String lastName,
            String email,
            String address,
            Date birthDate,
            String location,
            String mobileNumber,
            String profilePhotoUrl,
            Boolean isProfileSet,
            Boolean isVerified,
            Boolean isOpenForWork
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.birthDate = birthDate;
        this.location = location;
        this.mobileNumber = mobileNumber;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isProfileSet = isProfileSet;
        this.isVerified = isVerified;
        this.isOpenForWork = isOpenForWork;
    }
}
