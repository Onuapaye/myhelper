package com.martin.myhelper.model;

public class ServiceTypeModel {
    private String Id;
    private String serviceTypeName;
    private String serviceTypeDescription;

    public ServiceTypeModel() {
        // needed for firebase
    }

    public ServiceTypeModel(String serviceTypeName, String serviceTypeDescription) {
        this.serviceTypeName = serviceTypeName;
        this.serviceTypeDescription = serviceTypeDescription;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getServiceTypeName() {
        return serviceTypeName;
    }

    public void setServiceTypeName(String serviceTypeName) {
        this.serviceTypeName = serviceTypeName;
    }

    public String getServiceTypeDescription() {
        return serviceTypeDescription;
    }

    public void setServiceTypeDescription(String serviceTypeDescription) {
        this.serviceTypeDescription = serviceTypeDescription;
    }

    @Override
    public String toString() {
        return "ServiceTypeModel{" +
                "Id='" + Id + '\'' +
                ", serviceTypeName='" + serviceTypeName + '\'' +
                ", serviceTypeDescription='" + serviceTypeDescription + '\'' +
                '}';
    }
}
