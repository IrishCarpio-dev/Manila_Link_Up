package com.manilalinkup.app.models;

import java.util.Date;

public class SeekerProfileModel {
    String firstName;
    String lastName;
    String email;
    String mobileNumber;
    String address;
    Date birthDate;
    String location;
    String profilePhotoUrl;
    Boolean isProfileSet;
    Boolean isVerified;
    Boolean isOpenForWork;

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

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

    public String getAddress() { return address; }

    public Date getBirthDate() { return birthDate; }

    public String getLocation() { return location; }

    public String getMobileNumber() { return mobileNumber; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }

    public Boolean getProfileSet() { return isProfileSet; }

    public Boolean getVerified() { return isVerified; }

    public Boolean getOpenForWork() { return isOpenForWork; }
}
