package com.martin.myhelper.model;

import android.widget.ImageView;

import java.util.ArrayList;

//import com.google.firebase.database.Exclude;

public class VolunteerModel extends GenericModel {
    private String id;
    private String profileId;
    private ImageView profileImage;
    private String descriptionOfService;
    private String serviceTypeId;
    private String imageType;
    /*private String serviceDayId;
    private String serviceTimeId;
    private String serviceCallId;*/
    private ArrayList<String> daysForService;
    private ArrayList<String> timesForService;
    private ArrayList<String> timesForCalls;


    public VolunteerModel() {
    }

    public VolunteerModel(String firstName, String lastName, String email, String mobileNumber, String password,
                          String retypePassword, String volunteerID, ImageView profileImage, int userType) {
        this.id = volunteerID;
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setMobileNumber(mobileNumber);
        this.setPassword(password);
        this.setRetypePassword(retypePassword);
        this.setUserType(userType);
        this.profileImage = profileImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getDescriptionOfService() {
        return descriptionOfService;
    }

    public void setDescriptionOfService(String descriptionOfService) {
        this.descriptionOfService = descriptionOfService;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
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
        return "VolunteerModel{" +
                "id='" + id + '\'' +
                ", profileId='" + profileId + '\'' +
                ", profileImage=" + profileImage +
                ", descriptionOfService='" + descriptionOfService + '\'' +
                ", serviceTypeId='" + serviceTypeId + '\'' +
                ", imageType='" + imageType + '\'' +
                ", daysForService=" + daysForService +
                ", timesForService=" + timesForService +
                ", timesForCalls=" + timesForCalls +
                '}';
    }
}
