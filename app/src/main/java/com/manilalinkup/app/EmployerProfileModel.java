package com.manilalinkup.app;

public class EmployerProfileModel {
    public String fullName;
    public String email;
    public String address;
    public String mobileNumber;
    public String profilePhotoUrl;
    public Boolean isProfileSet;
    public Boolean isVerified;

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
}
