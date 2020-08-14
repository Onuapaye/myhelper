package com.martin.myhelper.model;

import java.util.Arrays;

public class VolunteerServiceProfile {

    private String id;
    private String volunteerId;
    private String descriptionOfService;
    private String[] daysAvailableForService;
    private String[] timesAvailableForService;
    private String[] timesAvailableForClass;

    public VolunteerServiceProfile() {
        // empty constructor for firebase
    }

    public VolunteerServiceProfile(String id, String volunteerId, String descriptionOfService, String[] daysAvailableForService, String[] timesAvailableForService, String[] timesAvailableForClass) {
        this.id = id;
        this.volunteerId = volunteerId;
        this.descriptionOfService = descriptionOfService;
        this.daysAvailableForService = daysAvailableForService;
        this.timesAvailableForService = timesAvailableForService;
        this.timesAvailableForClass = timesAvailableForClass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(String volunteerId) {
        this.volunteerId = volunteerId;
    }

    public String getDescriptionOfService() {
        return descriptionOfService;
    }

    public void setDescriptionOfService(String descriptionOfService) {
        this.descriptionOfService = descriptionOfService;
    }

    public String[] getDaysAvailableForService() {
        return daysAvailableForService;
    }

    public void setDaysAvailableForService(String[] daysAvailableForService) {
        this.daysAvailableForService = daysAvailableForService;
    }

    public String[] getTimesAvailableForService() {
        return timesAvailableForService;
    }

    public void setTimesAvailableForService(String[] timesAvailableForService) {
        this.timesAvailableForService = timesAvailableForService;
    }

    public String[] getTimesAvailableForClass() {
        return timesAvailableForClass;
    }

    public void setTimesAvailableForClass(String[] timesAvailableForClass) {
        this.timesAvailableForClass = timesAvailableForClass;
    }

    @Override
    public String toString() {
        return "VolunteerServiceProfile{" +
                "id='" + id + '\'' +
                ", volunteerId='" + volunteerId + '\'' +
                ", descriptionOfService='" + descriptionOfService + '\'' +
                ", daysAvailableForService=" + Arrays.toString(daysAvailableForService) +
                ", timesAvailableForService=" + Arrays.toString(timesAvailableForService) +
                ", timesAvailableForClass=" + Arrays.toString(timesAvailableForClass) +
                '}';
    }
}
