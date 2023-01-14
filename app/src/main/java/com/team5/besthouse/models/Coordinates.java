package com.team5.besthouse.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double elevation;
    private double longitude;
    private double latitude;

    public Coordinates() {
        // Default constructor required for calls to DataSnapshot.getValue(Coordinates.class)
        // Do not delete
    }

    public Coordinates(double latitude, double longitude, double elevation) {
        this.elevation = elevation;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @NonNull
    @Override
    @Exclude
    public String toString() {
        return "Coordinates{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    public static Coordinates STATICCOORD() {
        return new Coordinates(10.7640637,106.6820005, 12);
    }
}