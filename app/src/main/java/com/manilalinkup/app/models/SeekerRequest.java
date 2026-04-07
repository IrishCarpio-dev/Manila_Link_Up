package com.manilalinkup.app.models;

public class SeekerRequest {
    String uid;
    String firstname;
    String lastname;
    String email;
    String address;
    String birthdate;
    String location;
    String phone;
    int salary;
    String profile_picture;
    String clearance_photo;
    int status;
    int verified;

    public SeekerRequest(String uid, String firstname, String lastname, String email,
                         String address, String birthdate, String location, String phone,
                         int salary, String profile_picture, String clearance_photo,
                         int status, int verified) {
        this.uid = uid;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.address = address;
        this.birthdate = birthdate;
        this.location = location;
        this.phone = phone;
        this.salary = salary;
        this.profile_picture = profile_picture;
        this.clearance_photo = clearance_photo;
        this.status = status;
        this.verified = verified;
    }
}
