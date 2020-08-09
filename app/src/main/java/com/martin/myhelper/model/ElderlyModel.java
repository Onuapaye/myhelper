package com.martin.myhelper.model;
//import com.google.firebase.database.Exclude;

//import com.google.firebase.database.Exclude;

public class ElderlyModel extends GenericModel {

    // creation of fields or properties for the model
    private String elderlyId;
    private String key;

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

    public String getElderlyId() {
        return elderlyId;
    }

    public void setElderlyId(String elderlyId) {
        this.elderlyId = elderlyId;
    }

    @Override
    public String toString() {
        return "ElderlyModel{" +
                "elderlyId='" + elderlyId + '\'' +
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
