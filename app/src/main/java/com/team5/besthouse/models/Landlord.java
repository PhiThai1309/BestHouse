package com.team5.besthouse.models;

import java.util.ArrayList;
import java.util.List;

public class Landlord extends User{
    private List<Property> propertyList;
    private List<Contract> contractList;

    public Landlord(String email, String password, String fullName, String phoneNumber, String imageUrl) {
        super(email, password, fullName, phoneNumber, UserRole.LANDLORD, imageUrl);
        propertyList = new ArrayList<>();
        contractList = new ArrayList<>();
    }

    public Landlord(String email, String fullName, String phoneNumber, List<Property> propertyList, List<Contract> contractList, String imageUrl) {
        super(email, fullName, phoneNumber, UserRole.LANDLORD, imageUrl);
        this.propertyList = propertyList;
        this.contractList = contractList;
    }

    public boolean addNewProperty(Property property){
        this.propertyList.add(property);
        return true;
    }

    public boolean addNewContract(Contract contract)
    {
        this.addNewContract(contract);
        return true;
    }

    public List<Property> getPropertyList() {
        return propertyList;
    }

    public List<Contract> getContractList() {
        return contractList;
    }
}
