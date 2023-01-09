package com.team5.besthouse.models;

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

}