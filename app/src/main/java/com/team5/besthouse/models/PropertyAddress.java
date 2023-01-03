package com.team5.besthouse.models;

import java.io.Serializable;

public class PropertyAddress implements Serializable {
    private String street;
    private String ward;
    private String city;
    private String floor;
    private String buildingName;
    private String extraNote;
    private Coordinates coordinates;

    public PropertyAddress(){
        // Default constructor required for calls to DataSnapshot.getValue(PropertyAddress.class)
        // Do not delete
    }

    public PropertyAddress(String street, String ward, String city, String floor, String buildingName, String extraNote, Coordinates coordinates) {
        this.street = street;
        this.ward = ward;
        this.city = city;
        this.floor = floor;
        this.buildingName = buildingName;
        this.extraNote = extraNote;
        this.coordinates = coordinates;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getExtraNote() {
        return extraNote;
    }

    public void setExtraNote(String extraNote) {
        this.extraNote = extraNote;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public String toString() {
        return "PropertyAddress{" +
                "street='" + street + '\'' +
                ", ward='" + ward + '\'' +
                ", city='" + city + '\'' +
                ", floor='" + floor + '\'' +
                ", buildingName='" + buildingName + '\'' +
                ", extraNote='" + extraNote + '\'' +
                '}';
    }

    public static PropertyAddress STATICADDRESS = new PropertyAddress("123", "123", "123", "123", "123", "123", Coordinates.STATICCOORD());
}
