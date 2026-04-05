package com.manilalinkup.app.models;

public class EmployerProfileModel {
    String fullName;
    String email;
    String address;
    String mobileNumber;
    String profilePhotoUrl;
    Boolean isProfileSet;
    Boolean isVerified;

    public EmployerProfileModel(
            String fullName,
            String email,
            String address,
            String mobileNumber,
            String profilePhotoUrl,
            Boolean isProfileSet,
            Boolean isVerified
    ) {
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.profilePhotoUrl = profilePhotoUrl;
        this.isProfileSet = isProfileSet;
        this.isVerified = isVerified;
    }

    public String getFullName() { return fullName; }

    public String getEmail() { return email; }

    public String getAddress() { return address; }

    public String getMobileNumber() { return mobileNumber; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }

    public Boolean getProfileSet() { return isProfileSet; }

    public Boolean getVerified() { return isVerified; }
}
