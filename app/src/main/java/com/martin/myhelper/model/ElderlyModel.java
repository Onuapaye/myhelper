package com.martin.myhelper.model;
//import com.google.firebase.database.Exclude;

import com.google.firebase.database.Exclude;

public class ElderlyModel {

    // creation of fields or properties for the model
    private String elderlyId;
    private String elderlyFirstName;
    private String elderlyLastName;
    private String elderlyEmail;
    private String elderlyMobileNumber;
    private String elderlyPassword;
    private String elderlyRetypePassword;
    private String key;

    // create an empty constructor for firebase
    public ElderlyModel(){
        // used by firebase
    };

    // constructor for the model class
    public ElderlyModel(String elderlyFirstName, String elderlyLastName, String elderlyEmail,
                        String elderlyMobileNumber, String elderlyPassword, String elderlyRetypePassword) {

        this.elderlyFirstName = elderlyFirstName;
        this.elderlyLastName = elderlyLastName;
        this.elderlyEmail = elderlyEmail;
        this.elderlyMobileNumber = elderlyMobileNumber;
        this.elderlyPassword = elderlyPassword;
        this.elderlyRetypePassword = elderlyRetypePassword;
    }


    // generate getters and setters for the properties or fields

    public String getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(String elderlyId) {
        this.elderlyId = elderlyId;
    }

    public String getElderlyFirstName() {
        return elderlyFirstName;
    }

    public void setElderlyFirstName(String elderlyFirstName) {
        this.elderlyFirstName = elderlyFirstName;
    }

    public String getElderlyLastName() {
        return elderlyLastName;
    }

    public void setElderlyLastName(String elderlyLastName) {
        this.elderlyLastName = elderlyLastName;
    }

    public String getElderlyEmail() {
        return elderlyEmail;
    }

    public void setElderlyEmail(String elderlyEmail) {
        this.elderlyEmail = elderlyEmail;
    }

    public String getElderlyMobileNumber() {
        return elderlyMobileNumber;
    }

    public void setElderlyMobileNumber(String elderlyMobileNumber) {
        this.elderlyMobileNumber = elderlyMobileNumber;
    }

    public String getElderlyPassword() {
        return elderlyPassword;
    }

    public void setElderlyPassword(String elderlyPassword) {
        this.elderlyPassword = elderlyPassword;
    }

    public String getElderlyRetypePassword() {
        return elderlyRetypePassword;
    }

    public void setElderlyRetypePassword(String elderlyRetypePassword) {
        this.elderlyRetypePassword = elderlyRetypePassword;
    }

    @Override
    public String toString() {
        return "ElderlyModel{" +
                "elderlyFirstName='" + elderlyFirstName + '\'' +
                ", elderlyLastName='" + elderlyLastName + '\'' +
                ", elderlyEmail='" + elderlyEmail + '\'' +
                ", elderlyMobileNumber='" + elderlyMobileNumber + '\'' +
                ", elderlyPassword='" + elderlyPassword + '\'' +
                ", elderlyRetypePassword='" + elderlyRetypePassword + '\'' +
                '}';
    }

    // the exclude annotation is provided to tell firebase to ignore generating
    // the key automatically

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}
