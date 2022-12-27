package com.team5.besthouse.models;

import java.util.ArrayList;
import java.util.List;

public class Tenant extends User{
    private List<Contract> contractList;

    public Tenant(String email, String password, String fullName, String phoneNumber) {
        super(email, password, fullName, phoneNumber, UserRole.TENANT);
        this.contractList = new ArrayList<>();
    }

    public Tenant(String email, String fullName, String phoneNumber, List<Contract> contractList) {
        super(email, fullName, phoneNumber, UserRole.TENANT);
        this.contractList = contractList;
    }
}
