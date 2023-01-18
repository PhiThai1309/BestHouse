package com.team5.besthouse.models;

import java.util.ArrayList;
import java.util.List;

public class Tenant extends User{
    private List<Contract> contractList;

    public Tenant(String email, String password, String fullName, String phoneNumber, String imageUrl) {
        super(email, password, fullName, phoneNumber, UserRole.TENANT, imageUrl);
        this.contractList = new ArrayList<>();
    }

    public Tenant(String email, String fullName, String phoneNumber, List<Contract> contractList, String imageUrl) {
        super(email, fullName, phoneNumber, UserRole.TENANT, imageUrl);
        this.contractList = contractList;
    }
}
