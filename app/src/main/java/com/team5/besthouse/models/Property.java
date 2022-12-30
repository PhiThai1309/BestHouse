package com.team5.besthouse.models;

import java.util.List;

public class Property {

    private String id;
    private String landlordEmail;
    private String propertyName;
    private PropertyAddress address;
    private PropertyStatus status;
    private PropertyType type;
    private int bedrooms;
    private int bathrooms;
    private List<Utilities> utilities;
    private float monthlyPrice;
    private float area;

    public Property(String id, String propertyName, String landlordEmail, PropertyAddress address, PropertyType type, int bedrooms, int bathrooms, List<Utilities> utilities, float monthlyPrice, float area) {
        this.id = id;
        this.propertyName = propertyName;
        this.landlordEmail = landlordEmail;
        this.address = address;
        this.type = type;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.utilities = utilities;
        this.monthlyPrice = monthlyPrice;
        this.area = area;
    }

    public String getId() {
        return id;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public PropertyAddress getAddress() {
        return address;
    }

    public PropertyType getPropertyType() {
        return type;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public List<Utilities> getUtilities() {
        return utilities;
    }

    public float getMonthlyPrice() {
        return monthlyPrice;
    }

    public float getArea() {
        return area;
    }
}
