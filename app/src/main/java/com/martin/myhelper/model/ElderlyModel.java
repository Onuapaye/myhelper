package com.martin.myhelper.model;
//import com.google.firebase.database.Exclude;

//import com.google.firebase.database.Exclude;

import java.util.ArrayList;

public class ElderlyModel extends GenericModel {

    // creation of fields or properties for the model
    private String id;
    private String key;
    private String serviceRequestId;
    private String serviceRequestMessage;
    private String serviceRequestServiceTypeId;
    private String serviceRequestVolunteerId;
    private String serviceRequestVolunteerProfileId;
    private ArrayList<String> daysForService;
    private ArrayList<String> timesForService;
    private ArrayList<String> timesForCalls;

    // create an empty constructor for firebase
    public ElderlyModel(){
        // used by firebase
    };

    // constructor for the model class
    public ElderlyModel(String firstName, String lastName, String email,
                        String mobileNumber, String password, String retypePassword, int userType) {

        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setMobileNumber(mobileNumber);
        this.setPassword(password);
        this.setRetypePassword(retypePassword);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getServiceRequestMessage() {
        return serviceRequestMessage;
    }

    public void setServiceRequestMessage(String serviceRequestMessage) {
        this.serviceRequestMessage = serviceRequestMessage;
    }

    public String getServiceRequestServiceTypeId() {
        return serviceRequestServiceTypeId;
    }

    public void setServiceRequestServiceTypeId(String serviceRequestServiceTypeId) {
        this.serviceRequestServiceTypeId = serviceRequestServiceTypeId;
    }

    public String getServiceRequestVolunteerId() {
        return serviceRequestVolunteerId;
    }

    public void setServiceRequestVolunteerId(String serviceRequestVolunteerId) {
        this.serviceRequestVolunteerId = serviceRequestVolunteerId;
    }

    public String getServiceRequestVolunteerProfileId() {
        return serviceRequestVolunteerProfileId;
    }

    public void setServiceRequestVolunteerProfileId(String serviceRequestVolunteerProfileId) {
        this.serviceRequestVolunteerProfileId = serviceRequestVolunteerProfileId;
    }

    public ArrayList<String> getDaysForService() {
        return daysForService;
    }

    public void setDaysForService(ArrayList<String> daysForService) {
        this.daysForService = daysForService;
    }

    public ArrayList<String> getTimesForService() {
        return timesForService;
    }

    public void setTimesForService(ArrayList<String> timesForService) {
        this.timesForService = timesForService;
    }

    public ArrayList<String> getTimesForCalls() {
        return timesForCalls;
    }

    public void setTimesForCalls(ArrayList<String> timesForCalls) {
        this.timesForCalls = timesForCalls;
    }


    @Override
    public String toString() {
        return "ElderlyModel{" +
                "id='" + id + '\'' +
                ", key='" + key + '\'' +
                '}';
    }

    // the exclude annotation is provided to tell firebase to ignore generating
    // the key automatically
    //@Exclude
    public String getKey() {
        return key;
    }

    //@Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
