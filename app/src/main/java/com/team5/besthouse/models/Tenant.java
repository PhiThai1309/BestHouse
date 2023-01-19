package com.team5.besthouse.models;

import java.util.ArrayList;
import java.util.List;

public class Tenant extends User{
    private List<Contract> contractList;
    private int loyalPoint;

    public Tenant(String email, String password, String fullName, String phoneNumber, String imageUrl, int loyalPoint) {
        super(email, password, fullName, phoneNumber, UserRole.TENANT, imageUrl);
        this.contractList = new ArrayList<>();
        this.loyalPoint = loyalPoint;
    }

    public Tenant(String email, String fullName, String phoneNumber, List<Contract> contractList, String imageUrl, int loyalPoint) {
        super(email, fullName, phoneNumber, UserRole.TENANT, imageUrl);
        this.contractList = contractList;
        this.loyalPoint = loyalPoint;
    }

    public int getLoyalPoint() {
        return loyalPoint;
    }

    public void setLoyalPoint(int loyalPoint) {
        this.loyalPoint = loyalPoint;
    }


}
