package com.manilalinkup.app;

public class EmployerRequest {
    String fullName;
    String email;
    String mobileNumber;

    public EmployerRequest(
            String fullName,
            String email,
            String mobileNumber
    ) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
    }

}
