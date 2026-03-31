package com.manilalinkup.app;

public class EmployerRequest {
    String employerName;
    String email;
    String phoneNumber;

    public EmployerRequest(
            String employerName,
            String email,
            String phoneNumber
    ) {
        this.employerName = employerName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

}
