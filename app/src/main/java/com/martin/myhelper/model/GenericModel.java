package com.martin.myhelper.model;

import android.app.Activity;
import android.widget.EditText;

import com.martin.myhelper.R;

import java.util.ArrayList;

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

    private ArrayList<String> mondayTimes, mondayCalls, tuesdayTimes, tuesdayCalls, wednesdayTimes,
            wednesdayCalls, thursdayTimes, thursdayCalls, fridayTimes, fridayCalls, saturdayTimes, saturdayCalls, sundayTimes, sundayCalls;

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


    /**/
    public ArrayList<String> getMondayTimes() {
        return mondayTimes;
    }

    public void setMondayTimes(ArrayList<String> mondayTimes) {
        this.mondayTimes = mondayTimes;
    }

    public ArrayList<String> getMondayCalls() {
        return mondayCalls;
    }

    public void setMondayCalls(ArrayList<String> mondayCalls) {
        this.mondayCalls = mondayCalls;
    }

    public ArrayList<String> getTuesdayTimes() {
        return tuesdayTimes;
    }

    public void setTuesdayTimes(ArrayList<String> tuesdayTimes) {
        this.tuesdayTimes = tuesdayTimes;
    }

    public ArrayList<String> getTuesdayCalls() {
        return tuesdayCalls;
    }

    public void setTuesdayCalls(ArrayList<String> tuesdayCalls) {
        this.tuesdayCalls = tuesdayCalls;
    }

    public ArrayList<String> getWednesdayTimes() {
        return wednesdayTimes;
    }

    public void setWednesdayTimes(ArrayList<String> wednesdayTimes) {
        this.wednesdayTimes = wednesdayTimes;
    }

    public ArrayList<String> getWednesdayCalls() {
        return wednesdayCalls;
    }

    public void setWednesdayCalls(ArrayList<String> wednesdayCalls) {
        this.wednesdayCalls = wednesdayCalls;
    }

    public ArrayList<String> getThursdayTimes() {
        return thursdayTimes;
    }

    public void setThursdayTimes(ArrayList<String> thursdayTimes) {
        this.thursdayTimes = thursdayTimes;
    }

    public ArrayList<String> getThursdayCalls() {
        return thursdayCalls;
    }

    public void setThursdayCalls(ArrayList<String> thursdayCalls) {
        this.thursdayCalls = thursdayCalls;
    }

    public ArrayList<String> getFridayTimes() {
        return fridayTimes;
    }

    public void setFridayTimes(ArrayList<String> fridayTimes) {
        this.fridayTimes = fridayTimes;
    }

    public ArrayList<String> getFridayCalls() {
        return fridayCalls;
    }

    public void setFridayCalls(ArrayList<String> fridayCalls) {
        this.fridayCalls = fridayCalls;
    }

    public ArrayList<String> getSaturdayTimes() {
        return saturdayTimes;
    }

    public void setSaturdayTimes(ArrayList<String> saturdayTimes) {
        this.saturdayTimes = saturdayTimes;
    }

    public ArrayList<String> getSaturdayCalls() {
        return saturdayCalls;
    }

    public void setSaturdayCalls(ArrayList<String> saturdayCalls) {
        this.saturdayCalls = saturdayCalls;
    }

    public ArrayList<String> getSundayTimes() {
        return sundayTimes;
    }

    public void setSundayTimes(ArrayList<String> sundayTimes) {
        this.sundayTimes = sundayTimes;
    }

    public ArrayList<String> getSundayCalls() {
        return sundayCalls;
    }

    public void setSundayCalls(ArrayList<String> sundayCalls) {
        this.sundayCalls = sundayCalls;
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
