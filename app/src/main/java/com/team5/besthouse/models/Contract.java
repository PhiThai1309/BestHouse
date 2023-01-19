package com.team5.besthouse.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

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
        contractStatus = ContractStatus.valueOf(in.readString());
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ContractStatus getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(ContractStatus contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getLandlordEmail() {
        return landlordEmail;
    }

    public void setLandlordEmail(String landlordEmail) {
        this.landlordEmail = landlordEmail;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    public void setTenantEmail(String tenantEmail) {
        this.tenantEmail = tenantEmail;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
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

    @Exclude
    public String getFormattedStartDate(){
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("US").build();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        return dateFormat.format(getStartDate().toDate());
    }

    @Exclude
    public String getFormattedEndDate(){
        Locale locale = new Locale.Builder().setLanguage("en").setRegion("US").build();
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        return dateFormat.format(getEndDate().toDate());
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
        parcel.writeString(contractStatus.toString());
    }

    @Exclude
    public static Contract STATICCONTRACT = new Contract(
            null,
            "",
            "",
            "",
            null,
            null
    );
}
