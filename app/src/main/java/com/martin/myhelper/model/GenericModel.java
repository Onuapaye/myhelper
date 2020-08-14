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

    @Override
    public String toString() {
        return "GenericModel{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", password='" + password + '\'' +
                ", retypePassword='" + retypePassword + '\'' +
                ", userType=" + userType +
                '}';
    }


}
