package com.martin.myhelper.views;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.martin.myhelper.MainActivity;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyCRUDHelper;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.model.GenericModel;

import java.util.Arrays;
import java.util.List;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.REQUIRED_FIELD_TITLE;

//import com.google.firebase.database.DatabaseReference;

public class ElderlyRegistrationActivity extends AppCompatActivity {

    // global variables
    private EditText firstName, lastName, email, mobileNumber, password, retypePassword;
    private int userType;

    private Context context = ElderlyRegistrationActivity.this;
    private AppCompatActivity appCompatActivity = ElderlyRegistrationActivity.this;

    private ElderlyCRUDHelper crudHelper = new ElderlyCRUDHelper();
    private Button button;

    private Intent intent;

    OpenActivity openActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_registration);

        // open the login activity screen
        this.openLoginScreen();

        // create the elderly record
        this.createFiresStoreUserRecord();

        // monitor the text change event of the email field
        this.validateEmailOnEditTextChange();

        // monitor the text change event of the password field
        this.validatePasswordOnEditTextChange();

        this.validateOtherInputsOnEditTextChange();
    }

    /***
     * Open the elderly login screen when clicked
     */
    private void openLoginScreen(){
        openActivity = new OpenActivity();
        TextView textView = findViewById(R.id.loginHereLink);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(context, LoginActivity.class);
                intent.putExtra("loginPageHeaderTitle", "ELDERLY PERSON");
                intent.putExtra("userType", GenericModel.USER_TYPE_ELDER);
                startActivity(intent);
            }
        });
    }

    private void createFiresStoreUserRecord(){

        button = findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] irishNumberPrefixes = getResources().getStringArray(R.array.irishNumberPrefixes);
                List validList = Arrays.asList(irishNumberPrefixes);

                // set values for validation
                setFieldValues();

                ElderlyModel model = new ElderlyModel();

                //  validate fields
                boolean validationSucceeded = Utility.validateInputsOnCreate(ElderlyRegistrationActivity.this,
                        firstName, lastName, email, mobileNumber, password, retypePassword);

                if ( !validationSucceeded ) {
                    return;
                } else if ( !validList.contains(mobileNumber.getText().toString().substring(0, 3)) ){
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Wrong phone number provided. You must provide a valid Irish phone number", appCompatActivity);
                        return;

                } else {

                    // create an array to hold the data
                    model.setFirstName(firstName.getText().toString().trim());
                    model.setLastName(lastName.getText().toString().trim());
                    model.setEmail(email.getText().toString().trim());
                    model.setMobileNumber(mobileNumber.getText().toString().trim());
                    model.setPassword(password.getText().toString().trim());
                    model.setUserType(Integer.parseInt(String.valueOf(userType)));

                    crudHelper.createElderlyUserRecord(ElderlyRegistrationActivity.this, model);

                    // redirect to login activity
                    intent = new Intent(context, MainActivity.class);
                    intent.putExtra("recordCreated", CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG);
                    intent.putExtra("loginPageHeaderTitle", "ELDERLY PERSON");
                    startActivity(intent);
                }
            }
        });
    }

    private void validateEmailOnEditTextChange(){
        email = findViewById(R.id.email);
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (!Utility.isEmailAddressValid(email.getText().toString().trim())){
                        Utility.showInformationDialog(Utility.INVALID_EMAIL_TITLE,
                                Utility.INVALID_EMAIL_MSG, appCompatActivity);
                        return;
                    }
                }
            }
        });
    }

    private void validateOtherInputsOnEditTextChange(){
        this.setFieldValues();
        final String[] irishNumberPrefixes = getResources().getStringArray(R.array.irishNumberPrefixes);

        // first name
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (firstName.getText() == null || firstName.getText().toString().isEmpty()) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your first name.", appCompatActivity);

                        return;
                    }
                }
            }
        });


        // last name
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(!hasFocus) {
                    if (lastName.getText() == null || lastName.getText().toString().isEmpty()) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your last name.", appCompatActivity);

                        return;
                    }
                }
            }
        });

        // email

        // mobile number
        mobileNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (mobileNumber.getText() == null || mobileNumber.getText().toString().isEmpty()) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter your mobile number.", appCompatActivity);

                        return;
                    }

                    if (mobileNumber.getText().toString().trim().length() != 10) {
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Please enter a mobile number NOT less/greater than 10 characters", appCompatActivity);

                        return;
                    }

                    List validList = Arrays.asList(irishNumberPrefixes);
                    if ( !validList.contains(mobileNumber.getText().toString().substring(0, 3)) ){
                        Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Wrong phone number provided. You must provide a valid Irish phone number", appCompatActivity);
                        return;
                    }
                }
            }
        });

        // password
        retypePassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (Integer.parseInt(String.valueOf(retypePassword.getText().toString().trim().length())) > 0){

                        /*if (!Utility.isPasswordLengthValid(retypePassword.getText().toString().trim())
                                || !Utility.isPasswordHavingNumberAndSymbol(retypePassword.getText().toString().trim())
                                || !Utility.isPasswordHavingLowerCase(retypePassword.getText().toString().trim())
                                || !Utility.isPasswordHavingUpperCase(retypePassword.getText().toString().trim()) ){

                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE,
                                    Utility.INVALID_REPASSWORD_ALL_MSG, appCompatActivity);

                            return;
                        }*/

                        if (!retypePassword.getText().toString().equals(password.getText().toString())){
                            Utility.showInformationDialog(REQUIRED_FIELD_TITLE, "Your password and re-type password do not match. Please try again", appCompatActivity);
                            return;
                        }
                    }
                }

            }
        });
    }

    private void validatePasswordOnEditTextChange(){
        password = findViewById(R.id.password);
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus){
                    if (Integer.parseInt(String.valueOf(password.getText().toString().trim().length())) > 0){

                        if (!Utility.isPasswordLengthValid(password.getText().toString().trim())
                            || !Utility.isPasswordHavingNumberAndSymbol(password.getText().toString().trim())
                            || !Utility.isPasswordHavingLowerCase(password.getText().toString().trim())
                            || !Utility.isPasswordHavingUpperCase(password.getText().toString().trim()) ){

                            Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE,
                                    Utility.INVALID_PASSWORD_ALL_MSG, appCompatActivity);

                            return;
                        }
                    }
                }
            }
        });
    }


    // Get values from some EditTexts and assign or sets their values to
    // other variables
    private void setFieldValues(){
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        mobileNumber = findViewById(R.id.mobileNumber);
        password = findViewById(R.id.password);
        retypePassword = findViewById(R.id.retypePassword);
        userType = GenericModel.USER_TYPE_ELDER;
    }
}