package com.martin.myhelper.views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//import com.google.firebase.database.DatabaseReference;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.GenericModel;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;

public class ElderlyRegistrationActivity extends AppCompatActivity {

    // global variables
    private EditText firstName, lastName, email, mobileNumber, password, retypePassword;
    private int userType;

    private Context context = ElderlyRegistrationActivity.this;
    private AppCompatActivity appCompatActivity = ElderlyRegistrationActivity.this;

    private ElderlyVolunteerCRUDHelper crudHelper = new ElderlyVolunteerCRUDHelper();
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
    }

    /***
     * Open the elderly login screen when clicked
     */
    private void openLoginScreen(){
        openActivity = new OpenActivity();
        TextView textView = (TextView) findViewById(R.id.loginHereLink);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openActivity.openAnActivityScreen(ElderlyRegistrationActivity.this, LoginActivity.class);
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

                // set values for validation
                setFieldValues();

                //  validate fields
                boolean validationSucceeded = Utility.validateInputsOnCreate(ElderlyRegistrationActivity.this, firstName, lastName, email, mobileNumber, password, retypePassword);

                if ( !validationSucceeded ) {
                    return;
                } else {

                    // create an array to hold the data
                    String[] modelArray = new String[]{
                            firstName.getText().toString().trim(),
                            lastName.getText().toString().trim(),
                            email.getText().toString().trim(),
                            mobileNumber.getText().toString().trim(),
                            password.getText().toString().trim(),
                            String.valueOf(userType),
                    };

                    //intent = getIntent();
                    //intent.getExtras();

                    //final int userType = intent.getIntExtra("userType",0);
                    //if(userType == GenericModel.USER_TYPE_ELDER) {
                        crudHelper.createUserRecord(ElderlyRegistrationActivity.this, modelArray);
                    //} else {
                    //    crudHelper.createUserRecord(ElderlyRegistrationActivity.this, modelArray, null);
                    //}
                    // redirect to login activity
                    intent = new Intent(context, LoginActivity.class);
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
                    Utility.showInformationDialog(Utility.INVALID_EMAIL_TITLE, Utility.INVALID_EMAIL_MSG, appCompatActivity);
                    //email.getFocusable();
                    email.setFocusable(true);
                    return;
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

                    if (!Utility.isPasswordLengthValid(password.getText().toString().trim())){

                        // show message that password length is invalid
                        Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_LENGTH_MSG, appCompatActivity);
                        password.getFocusable();
                        return;
                    }

                    if (!Utility.isPasswordHavingNumberAndSymbol(password.getText().toString().trim())){

                        // show message that password is not containing a number or symbol
                        Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_NUMBER_SYMBOL_MSG, appCompatActivity);
                        password.getFocusable();
                        return;
                    }

                    if (!Utility.isPasswordHavingLowerCase(password.getText().toString().trim())){

                        // show a message that password does not contain an upper case
                        Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_LOWERCASE_MSG, appCompatActivity);
                        password.getFocusable();
                        return;
                    }

                    if (!Utility.isPasswordHavingUpperCase(password.getText().toString().trim())) {

                        // show a message that password does not contain a lower case
                        Utility.showInformationDialog(Utility.INVALID_PASSWORD_TITLE, Utility.INVALID_PASSWORD_UPPERCASE_MSG, appCompatActivity);
                        password.getFocusable();
                        return;
                    }
                }
            }
            }
        });
    }
    /*private void createNewElderlyRecord(){

        button = (Button) findViewById(R.id.confirmButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set values for validation
                setFieldValues();

                //  validate fields
                Boolean validationSucceeded = Utility.validateInputsOnCreate(ElderlyRegistrationActivity.this, firstName, lastName, email, mobileNumber, password, retypePassword);

                // validate edit fields
                if ( ! validationSucceeded) {
                   /*Intent intent = new Intent(ElderlyRegistrationActivity.this, ElderlyLoginActivity.class);
                   startActivity(intent);
                    Utility.showInformationDialog("VALIDATION FAILED", "Validation failed and record cannot be saved.", ElderlyRegistrationActivity.this);
                    return;
                } else {

                   // create an instance of the ElderlyModel and assign values to it
                   ElderlyModel elderlyModel = new ElderlyModel(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(),
                                                mobileNumber.getText().toString(), password.getText().toString(), retypePassword.getText().toString(), userType);
                                                elderlyModel.setElderlyId(Utility.getUUID());

                   // call a method to create a record in the database
                   crudHelper.createFirebaseRealtimeRecord(ElderlyRegistrationActivity.this, modelDatabaseReference, elderlyModel);
                }
            }
        });
    }*/


    // Get values from some EditTexts and assign or sets their values to
    // other variables
    private void setFieldValues(){
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.email);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText) findViewById(R.id.password);
        retypePassword = (EditText) findViewById(R.id.retypePassword);
        userType = GenericModel.USER_TYPE_ELDER;
    }
}