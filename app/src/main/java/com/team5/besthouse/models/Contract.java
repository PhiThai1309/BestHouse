package com.team5.besthouse.models;

import com.google.type.DateTime;

public class Contract {

    private String id;
    private ContractStatus contractStatus;
    private String landlordEmail;
    private String tenantEmail;
    private String propertyId;
    private DateTime startDate;
    final private int LENGTH_IN_MONTH = 12;

    public Contract(ContractStatus contractStatus, String landlordEmail, String tenantEmail, String propertyId, DateTime startDate) {
        this.contractStatus = contractStatus;
        this.landlordEmail = landlordEmail;
        this.tenantEmail = tenantEmail;
        this.propertyId = propertyId;
        this.startDate = startDate;
    }

    public Contract(String id, ContractStatus contractStatus, String landlordEmail, String tenantEmail, String propertyId, DateTime startDate) {
        this.id = id;
        this.contractStatus = contractStatus;
        this.landlordEmail = landlordEmail;
        this.tenantEmail = tenantEmail;
        this.propertyId = propertyId;
        this.startDate = startDate;
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

    public DateTime getStartDate() {
        return startDate;
    }

    public int getLENGTH_IN_MONTH() {
        return LENGTH_IN_MONTH;
    }
}
