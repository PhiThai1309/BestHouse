package com.team5.besthouse.models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class PropertyAddress implements Serializable {
    private String street;
    private Coordinates coordinates;

    public PropertyAddress(){
        // Default constructor required for calls to DataSnapshot.getValue(PropertyAddress.class)
        // Do not delete
    }


    public PropertyAddress(String street, Coordinates coordinates) {
        this.street = street;
        this.coordinates = coordinates;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }


    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Exclude
    public static PropertyAddress STATICADDRESS = new PropertyAddress("235 Đ. Nguyễn Văn Cừ, Phường 4, Quận 5, Thành phố Hồ Chí Minh 70000, Vietnam", Coordinates.STATICCOORD());

}