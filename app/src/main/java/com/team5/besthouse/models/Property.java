package com.team5.besthouse.models;
import java.io.Serializable;
import java.util.ArrayList;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Property implements Serializable {
    private String id;
    private String landlordEmail;
    private String propertyName;
    private String propertyDescription;
    private PropertyAddress address;
    private ArrayList<String> imageURLList;
    private PropertyStatus status;
    private PropertyType propertyType;
    private int bedrooms;
    private int bathrooms;
    private List<Utilities> utilities;
    private float monthlyPrice;
    private float area;

    public Property(){
        // Default constructor required for calls to DataSnapshot.getValue(Property.class)
        // Do not delete
    }

    public Property(String id, String propertyName, String landlordEmail, PropertyAddress address, PropertyType propertyType, int bedrooms, int bathrooms, List<Utilities> utilities, float monthlyPrice, float area) {
        this.id = id;
        this.propertyName = propertyName;
        this.landlordEmail = landlordEmail;
        this.address = address;
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
        return id.equals(property.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean setImageURLList(ArrayList<String> imageURLList)
    {
       if(imageURLList.size() == 0)
       {
            return false;
       }
       else
       {
           this.imageURLList = imageURLList;
           return true;
       }
    }

    public void setStatus(PropertyStatus pstatus)
    {
        this.status = pstatus;
    }

    public ArrayList<String> getImageURLList()
    {
        return this.imageURLList;
    }
    public String getId() {
        return id;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public void setLandlordEmail(String landlordEmail) {
        this.landlordEmail = landlordEmail;
    }

    public void setPropertyDescription(String pd)
    {
        this.propertyDescription = pd;
    }
    public String getPropertyDescription()
    {
        return this.getPropertyDescription();
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public PropertyAddress getAddress() {
        return address;
    }

    public void setAddress(PropertyAddress address) {
        this.address = address;
    }

    public PropertyStatus getStatus() {
        return status;
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
    public static Property STATICPROPERTY = new Property(
            "213",
            "123",
            "213",
            PropertyAddress.STATICADDRESS,
            PropertyType.APARTMENT,
            12,
            12,
            Collections.singletonList(Utilities.ELECTRIC),
            12.0f,
            12.0f
    );
}
