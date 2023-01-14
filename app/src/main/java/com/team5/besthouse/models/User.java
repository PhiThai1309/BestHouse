package com.team5.besthouse.models;

import java.util.HashMap;

public abstract class User {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private UserRole role;

    public User() {}

    public User(String email, String password, String fullName, String phoneNumber, UserRole role) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public User(String email, String fullName, String phoneNumber, UserRole role) {
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }


    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }
}
