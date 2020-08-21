package com.martin.myhelper.model;

import android.app.Activity;
import android.widget.EditText;

import com.martin.myhelper.R;

public class GenericModel {

    public static final int USER_TYPE_ELDER = 1;
    public static final int USER_TYPE_VOLUNTEER = 2;
    public static final int USER_TYPE_ADMIN = 0;

    public static final String ELDERS = "elders";
    public static final String VOLUNTEERS = "volunteers";
    public static final String ADMINISTRATORS = "administrators";
    public static int GLOBAL_USERTYPE;
    public static final int PICK_IMAGE_REQUEST = 1;

    private EditText firstNameTextEdit, lastNameTextEdit, emailTextEdit, mobileNumberTextEdit, passwordTextEdit, retypePasswordTextEdit;

    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private String password;
    private String retypePassword;
    private int userType;

    private String feedBackId;
    private String feedBackMessage;
    private int feedBackRating;
    private String feedBackVolunteerId;
    private String feedBackElderlyId;
    private String feedBackRequestId;
    private String feedBackServiceTypeId;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getFeedBackId() {
        return feedBackId;
    }

    public void setFeedBackId(String feedBackId) {
        this.feedBackId = feedBackId;
    }

    public String getFeedBackMessage() {
        return feedBackMessage;
    }

    public void setFeedBackMessage(String feedBackMessage) {
        this.feedBackMessage = feedBackMessage;
    }

    public int getFeedBackRating() {
        return feedBackRating;
    }

    public void setFeedBackRating(int feedBackRating) {
        this.feedBackRating = feedBackRating;
    }

    public String getFeedBackVolunteerId() {
        return feedBackVolunteerId;
    }

    public void setFeedBackVolunteerId(String feedBackVolunteerId) {
        this.feedBackVolunteerId = feedBackVolunteerId;
    }

    public String getFeedBackElderlyId() {
        return feedBackElderlyId;
    }

    public void setFeedBackElderlyId(String feedBackElderlyId) {
        this.feedBackElderlyId = feedBackElderlyId;
    }

    public String getFeedBackRequestId() {
        return feedBackRequestId;
    }

    public void setFeedBackRequestId(String feedBackRequestId) {
        this.feedBackRequestId = feedBackRequestId;
    }

    public String getFeedBackServiceTypeId() {
        return feedBackServiceTypeId;
    }

    public void setFeedBackServiceTypeId(String feedBackServiceTypeId) {
        this.feedBackServiceTypeId = feedBackServiceTypeId;
    }

    @Override
    public String toString() {
        return "GenericModel{" +
                "firstNameTextEdit=" + firstNameTextEdit +
                ", lastNameTextEdit=" + lastNameTextEdit +
                ", emailTextEdit=" + emailTextEdit +
                ", mobileNumberTextEdit=" + mobileNumberTextEdit +
                ", passwordTextEdit=" + passwordTextEdit +
                ", retypePasswordTextEdit=" + retypePasswordTextEdit +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", password='" + password + '\'' +
                ", retypePassword='" + retypePassword + '\'' +
                ", userType=" + userType +
                ", feedBackId='" + feedBackId + '\'' +
                ", feedBackMessage='" + feedBackMessage + '\'' +
                ", feedBackRating=" + feedBackRating +
                ", feedBackVolunteerId='" + feedBackVolunteerId + '\'' +
                ", feedBackElderlyId='" + feedBackElderlyId + '\'' +
                ", feedBackRequestId='" + feedBackRequestId + '\'' +
                ", feedBackServiceTypeId='" + feedBackServiceTypeId + '\'' +
                '}';
    }


}
