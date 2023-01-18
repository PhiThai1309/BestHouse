package com.team5.besthouse.models;

import java.util.Objects;

public class PropertyDAO {
    Property property;
    int numOfContracts;

    public PropertyDAO(Property property, int numOfContracts) {
        this.property = property;
        this.numOfContracts = numOfContracts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyDAO that = (PropertyDAO) o;
        return Objects.equals(property, that.property);
    }

    @Override
    public int hashCode() {
        return Objects.hash(property);
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public int getNumOfContracts() {
        return numOfContracts;
    }

    public void setNumOfContracts(int numOfContracts) {
        this.numOfContracts = numOfContracts;
    }
}
