package com.team5.besthouse.models;

import android.content.Context;
import android.location.Geocoder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import com.google.firebase.firestore.Exclude;
import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Property implements Serializable {
    private String id;
    private String landlordEmail;
    private String propertyName;
    private String propertyDescription;
    private double latitude;
    private double longitude;
    private List<String> imageURLList;
    private PropertyStatus status;
    private PropertyType propertyType;
    private int bedrooms;
    private int bathrooms;
    private List<Utilities> utilities;
    private float monthlyPrice;
    private float area;

    public Property() {
        // Default constructor required for calls to DataSnapshot.getValue(Property.class)
        // Do not delete
    }

    public Property(String id, String propertyName, String landlordEmail, LatLng coordinates, PropertyType propertyType, int bedrooms, int bathrooms, List<Utilities> utilities, float monthlyPrice, float area) {
        this.id = id;
        this.propertyName = propertyName;
        this.landlordEmail = landlordEmail;
        this.latitude = coordinates.latitude;
        this.longitude = coordinates.longitude;
        this.propertyType = propertyType;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.utilities = utilities;
        this.monthlyPrice = monthlyPrice;
        this.area = area;
    }

    public Property(String id, String landlordEmail, String propertyName, String propertyDescription, double latitude, double longitude, List<String> imageURLList, PropertyStatus status, PropertyType propertyType, int bedrooms, int bathrooms, List<Utilities> utilities, float monthlyPrice, float area) {
        this.id = id;
        this.landlordEmail = landlordEmail;
        this.propertyName = propertyName;
        this.propertyDescription = propertyDescription;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageURLList = imageURLList;
        this.status = status;
        this.propertyType = propertyType;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.utilities = utilities;
        this.monthlyPrice = monthlyPrice;
        this.area = area;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        if (id == null || property.getId() == null) return false;
        return Objects.equals(id, property.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean setImageURLList(ArrayList<String> imageURLList) {
        if (imageURLList.size() == 0) {
            return false;
        } else {
            this.imageURLList = imageURLList;
            return true;
        }
    }

    @Exclude
    public static Property STATICPROPERTY = new Property(
            "213",
            "123",
            "213",
            new LatLng(-34, 151),
            PropertyType.APARTMENT,
            12,
            12,
            Collections.singletonList(Utilities.ELECTRIC),
            12.0f,
            12.0f
    );

    @Override
    public String toString() {
        return "Property{" +
                "propertyName='" + propertyName + '\'' +
                ", propertyDescription='" + propertyDescription + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public void setLandlordEmail(String landlordEmail) {
        this.landlordEmail = landlordEmail;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Exclude
    public LatLng getcoordinates() {
        return new LatLng(latitude, longitude);
    }

    public void setcoordinates(LatLng coordinates) {
        this.latitude = coordinates.latitude; this.longitude = coordinates.longitude;
    }

    public String getAddress(Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            return geocoder.getFromLocation(latitude, longitude, 1).get(0).getAddressLine(0);
        } catch (Exception e){
            return "Address not found";
        }
    }

    public List<String> getImageURLList() {
        return imageURLList;
    }

    public void setImageURLList(List<String> imageURLList) {
        this.imageURLList = imageURLList;
    }

    public PropertyStatus getStatus() {
        return status;
    }

    public void setStatus(PropertyStatus status) {
        this.status = status;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public List<Utilities> getUtilities() {
        return utilities;
    }

    public void setUtilities(List<Utilities> utilities) {
        this.utilities = utilities;
    }

    public float getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(float monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public float getArea() {
        return area;
    }

    public void setArea(float area) {
        this.area = area;
    }

    @Exclude
    public static LatLng STATICCOORD = new LatLng(10.7640637,106.6820005);
}
