package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.FirebaseDatabaseCRUDHelper;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.ElderlyModel;
import com.martin.myhelper.model.GenericModel;

public class ElderlyRegistrationActivity extends AppCompatActivity {

    // global variables
    private EditText firstName, lastName, email, mobileNumber, password, retypePassword;
    private int userType;
    private final Context context = ElderlyRegistrationActivity.this;
    private FirebaseDatabaseCRUDHelper crudHelper = new FirebaseDatabaseCRUDHelper();
    //private DatabaseReference modelDatabaseReference = Utility.getFirebaseDatabaseReference();
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_registration);

        // open the login activity screen
        this.openElderlyLoginScreen();

        // create the elderly record
        this.createFiresStoreUserRecord();
    }

    /***
     * Open the elderly login screen when clicked
     */
    private void openElderlyLoginScreen(){

        TextView textView = (TextView) findViewById(R.id.loginHereLink);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenActivity.openAnActivityScreen(ElderlyRegistrationActivity.this, ElderlyLoginActivity.class);
               //Intent intent = new Intent(ElderlyRegistrationActivity.this, ElderlyLoginActivity.class);
               //startActivity(intent);
            }
        });
    }

    private void createFiresStoreUserRecord(){
        button = (Button) findViewById(R.id.confirmButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // set values for validation
                setFieldValues();

                //  validate fields
                Boolean validationSucceeded = Utility.validateInputsOnCreate(ElderlyRegistrationActivity.this, firstName, lastName, email, mobileNumber, password, retypePassword);

                if ( !validationSucceeded ) {
                    Utility.showInformationDialog("VALIDATION FAILED", "Validation failed and record cannot be saved.", ElderlyRegistrationActivity.this);
                    return;
                } else {

                    // create an array to hold the data
                    String[] modelArray = new String[]{
                            firstName.getText().toString(),
                            lastName.getText().toString(),
                            email.getText().toString(),
                            mobileNumber.getText().toString(),
                            String.valueOf(userType),
                    };

                    FirebaseAuth firebaseAuth = Utility.getFirebaseAuthenticationReference();
                    FirebaseDatabaseCRUDHelper crudHelper = new FirebaseDatabaseCRUDHelper();

                    crudHelper.createUserRecord(ElderlyRegistrationActivity.this, firebaseAuth, ElderlyLoginActivity.class,
                            email.getText().toString(), password.getText().toString(), "elders", modelArray);
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