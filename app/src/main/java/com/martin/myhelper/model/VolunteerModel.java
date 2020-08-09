package com.martin.myhelper.model;

import android.widget.ImageView;

//import com.google.firebase.database.Exclude;

public class VolunteerModel extends GenericModel {
    private String volunteerID;
    private String volunteerKey;
    private ImageView profileImage;

    public VolunteerModel() {
    }

    public VolunteerModel(String firstName, String lastName, String email, String mobileNumber, String password,
                          String retypePassword, String volunteerID, String volunteerKey, ImageView profileImage, int userType) {
        this.volunteerID = volunteerID;
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setEmail(email);
        this.setMobileNumber(mobileNumber);
        this.setPassword(password);
        this.setRetypePassword(retypePassword);
        this.setUserType(userType);
        this.volunteerKey = volunteerKey;
        this.profileImage = profileImage;
    }

    public String getVolunteerID() {
        return volunteerID;
    }

    public void setVolunteerID(String volunteerID) {
        this.volunteerID = volunteerID;
    }

    public ImageView getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(ImageView profileImage) {
        this.profileImage = profileImage;
    }

    //@Exclude
    public String getVolunteerKey() {
        return volunteerKey;
    }

    //@Exclude
    public void setVolunteerKey(String volunteerKey) {
        this.volunteerKey = volunteerKey;
    }

    @Override
    public String toString() {
        return "VolunteerModel{" +
                "volunteerID='" + volunteerID + '\'' +
                ", volunteerKey='" + volunteerKey + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
