package com.team5.besthouse.models;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class Contract implements Serializable {

    private String id;
    private ContractStatus contractStatus;
    private String landlordEmail;
    private String tenantEmail;
    private String propertyId;
    private Timestamp startDate = Timestamp.now();
    private Timestamp endDate = Timestamp.now();


    public Contract(){
        // Default constructor required for calls to DataSnapshot.getValue(Property.class)
        // Do not delete
    }

    public Contract(ContractStatus contractStatus, String landlordEmail, String tenantEmail, String propertyId, Timestamp startDate, Timestamp endDate) {
        this.contractStatus = contractStatus;
        this.landlordEmail = landlordEmail;
        this.tenantEmail = tenantEmail;
        this.propertyId = propertyId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }
}
