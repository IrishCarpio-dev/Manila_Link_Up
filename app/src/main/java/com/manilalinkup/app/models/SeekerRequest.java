package com.manilalinkup.app.models;

public class SeekerRequest {
    private String uid;
    String firstname;
    String middlename;
    String lastname;
    String suffix;
    String email;
    String phone;
    String birthdate;
    private String address = "Not Provided";
    private String location = "Not Provided";

    public SeekerRequest(String uid, String firstname, String middlename, String lastname, String suffix, String email, String phone, String birthdate) {
        this.uid = uid;
        this.firstname = firstname;
        this.middlename = middlename.isEmpty() ? null : middlename;
        this.lastname = lastname;
        this.suffix = suffix.isEmpty() ? null : suffix;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;
    }
}
