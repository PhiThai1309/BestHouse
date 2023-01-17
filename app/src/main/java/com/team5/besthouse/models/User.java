package com.team5.besthouse.models;

import java.util.HashMap;

public class User {
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private UserRole role;
    private String imageUrl;

    public User() {}

    public User(String email, String password, String fullName, String phoneNumber, UserRole role, String imageUrl) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.imageUrl = imageUrl;
    }

    public User(String email, String fullName, String phoneNumber, UserRole role, String imageUrl) {
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
