package com.manilalinkup.app;

public class EmployerRequest {

    String uid;
    String employerName;
    String email;
    String phoneNumber;
    String address;
    String birthdate;
    String location;
    String profile_picture;
    String clearance_photo;
    int status;
    int verified;

    public EmployerRequest(String uid, String employerName, String email, String phoneNumber, String address, String birthdate, String location, String profile_picture, String clearance_photo, int status, int verified) {
        this.uid = uid;
        this.employerName = employerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.birthdate = birthdate;
        this.location = location;
        this.profile_picture = profile_picture;
        this.clearance_photo = clearance_photo;
        this.status = status;
        this.verified = verified;
    }

}
