package com.team5.besthouse.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Chat implements Serializable {
    private String id;
    private String contractId;

    public Chat() {
    }

    public Chat(String contractId) {
        this.contractId = contractId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
}
