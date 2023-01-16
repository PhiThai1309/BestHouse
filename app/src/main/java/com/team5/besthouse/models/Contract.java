package com.team5.besthouse.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Objects;

public class Contract implements Parcelable {

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

    protected Contract(Parcel in) {
        id = in.readString();
        landlordEmail = in.readString();
        tenantEmail = in.readString();
        propertyId = in.readString();
        startDate = in.readParcelable(Timestamp.class.getClassLoader());
        endDate = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<Contract> CREATOR = new Creator<Contract>() {
        @Override
        public Contract createFromParcel(Parcel in) {
            return new Contract(in);
        }

        @Override
        public Contract[] newArray(int size) {
            return new Contract[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contract contract = (Contract) o;
        return Objects.equals(id, contract.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(landlordEmail);
        parcel.writeString(tenantEmail);
        parcel.writeString(propertyId);
        parcel.writeParcelable(startDate, i);
        parcel.writeParcelable(endDate, i);
    }
}
