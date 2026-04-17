package com.manilalinkup.app.models;

public class SeekerRequest {
    String uid;
    String firstName;
    String middleName;
    String lastName;
    String suffix;
    String email;
    String address;
    String birthdate;
    String location;
    String mobileNumber;
    int salary;
    String profilePhotoUrl;
    String clearance_photo;
    Boolean isOpenForWork;
    Boolean isVerified;

    public SeekerRequest(String uid, String firstName, String middleName, String lastName, String suffix,
                         String email, String address, String birthdate, String location, String mobileNumber,
                         int salary, String profilePhotoUrl, String clearance_photo,
                         Boolean  isOpenForWork, Boolean isVerified) {
        this.uid = uid;
        this.firstName = firstName;
        this.middleName = middleName.isEmpty() ? null : middleName;
        this.lastName = lastName;
        this.suffix = suffix.isEmpty() ? null : suffix;
        this.email = email;
        this.address = address;
        this.birthdate = birthdate;
        this.location = location;
        this.mobileNumber = mobileNumber;
        this.salary = salary;
        this.profilePhotoUrl = profilePhotoUrl;
        this.clearance_photo = clearance_photo;
        this. isOpenForWork =  isOpenForWork;
        this.isVerified = isVerified;
    }
}
