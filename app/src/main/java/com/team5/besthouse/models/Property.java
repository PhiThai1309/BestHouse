package com.team5.besthouse.models;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Property implements  Serializable {
    private String id;
    private String landlordEmail;
    private String propertyName;
    private String propertyDescription;
    private PropertyAddress address;
    private List<String> imageURLList;
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

    public Property()
    {

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

    public PropertyAddress getAddress() {
        return address;
    }

    public void setAddress(PropertyAddress address) {
        this.address = address;
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

    public PropertyType getType() {
        return type;
    }

    public void setType(PropertyType type) {
        this.type = type;
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
}
