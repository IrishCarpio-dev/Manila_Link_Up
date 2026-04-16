package com.manilalinkup.app.models;

public class SeekerRequest {
    String firstName;
    String lastName;
    String email;
    String mobileNumber;
    String birthDate;

    public SeekerRequest(String firstName, String lastName, String email, String mobileNumber, String birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.birthDate = birthDate;
    }
}
