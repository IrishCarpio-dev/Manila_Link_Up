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
    String profilePhoto;
    Boolean isProfileSet;
    Boolean isVerified;
    Boolean isOpenForWork;
    SeekerPreferencesModel preferences;
    Integer ratingCount;
    Double ratingSum;
    Double bayesianAvg;

    public SeekerProfileModel(
            String firstName,
            String lastName,
            String email,
            String address,
            Date birthDate,
            String location,
            String mobileNumber,
            String profilePhoto,
            Boolean isProfileSet,
            Boolean isVerified,
            Boolean isOpenForWork,
            SeekerPreferencesModel preferences
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.birthDate = birthDate;
        this.location = location;
        this.mobileNumber = mobileNumber;
        this.profilePhoto = profilePhoto;
        this.isProfileSet = isProfileSet;
        this.isVerified = isVerified;
        this.isOpenForWork = isOpenForWork;
        this.preferences = preferences;
    }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public String getEmail() { return email; }

    public String getAddress() { return address; }

    public Date getBirthDate() { return birthDate; }

    public String getLocation() { return location; }

    public String getMobileNumber() { return mobileNumber; }

    public String getProfilePhoto() { return profilePhoto; }

    public Boolean getProfileSet() { return isProfileSet; }

    public Boolean getVerified() { return isVerified; }

    public Boolean getOpenForWork() { return isOpenForWork; }

    public SeekerPreferencesModel getPreferences() { return preferences; }

    public Integer getRatingCount() { return ratingCount; }

    public Double getRatingSum() { return ratingSum; }

    public Double getBayesianAvg() { return bayesianAvg; }
}
