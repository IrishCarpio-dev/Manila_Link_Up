package com.manilalinkup.app.models;

public class EmployerProfileModel {
    String uid;
    String fullName;
    String email;
    String address;
    String mobileNumber;
    String profilePhoto;
    Boolean isProfileSet;
    Boolean isVerified;
    Integer ratingCount;
    Double ratingSum;
    Double bayesianAvg;

    public EmployerProfileModel(
            String fullName,
            String email,
            String address,
            String mobileNumber,
            String profilePhoto,
            Boolean isProfileSet,
            Boolean isVerified
    ) {
        this.fullName = fullName;
        this.email = email;
        this.address = address;
        this.mobileNumber = mobileNumber;
        this.profilePhoto = profilePhoto;
        this.isProfileSet = isProfileSet;
        this.isVerified = isVerified;
    }

    public String getUid() { return uid; }

    public String getFullName() { return fullName; }

    public String getEmail() { return email; }

    public String getAddress() { return address; }

    public String getMobileNumber() { return mobileNumber; }

    public String getProfilePhoto() { return profilePhoto; }

    public Boolean getProfileSet() { return isProfileSet; }

    public Boolean getVerified() { return isVerified; }

    public Integer getRatingCount() { return ratingCount; }

    public Double getRatingSum() { return ratingSum; }

    public Double getBayesianAvg() { return bayesianAvg; }
}
