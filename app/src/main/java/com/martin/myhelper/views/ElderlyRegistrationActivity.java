package com.martin.myhelper.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.FirebaseDatabaseCRUDHelper;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.ElderlyModel;

public class ElderlyRegistrationActivity extends AppCompatActivity {

    // global variables
    private EditText firstName, lastName, email, mobileNumber, password, retypePassword;
    private final Context context = ElderlyRegistrationActivity.this;
    private FirebaseDatabaseCRUDHelper crudHelper = new FirebaseDatabaseCRUDHelper();
    private DatabaseReference modelDatabaseReference = Utility.getDatabaseReference();
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elderly_registration);

        // open the login activity screen
        this.openElderlyLoginScreen();

        // create the elderly record
        this.createNewElderlyRecord();
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

    private void createNewElderlyRecord(){

        button = (Button) findViewById(R.id.confirmButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set values for validation
                setFieldValues();

                // validate edit fields
                if (Utility.validateElement(firstName, lastName, email, mobileNumber, password, retypePassword)) {

                    // create an instance of the ElderlyModel and assign values to it
                    ElderlyModel elderlyModel = new ElderlyModel(firstName.getText().toString(), lastName.getText().toString(), email.getText().toString(),
                            mobileNumber.getText().toString(), password.getText().toString(), retypePassword.getText().toString());
                            elderlyModel.setElderlyId(Utility.getUUID());

                    // call a method to create a record in the database
                    crudHelper.createRecord(ElderlyRegistrationActivity.this, modelDatabaseReference, elderlyModel);

                   /*Intent intent = new Intent(ElderlyRegistrationActivity.this, ElderlyLoginActivity.class);
                   startActivity(intent);*/
                } else {
//                   Utility.showInfoDialog( (AppCompatActivity) ElderlyRegistrationActivity, "");
                }
            }
        });
    }

    // Get values from some EditTexts and assign or sets their values to
    // other variables
    private void setFieldValues(){
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        email = (EditText) findViewById(R.id.username);
        mobileNumber = (EditText) findViewById(R.id.mobileNumber);
        password = (EditText) findViewById(R.id.password);
        retypePassword = (EditText) findViewById(R.id.retypePassword);
    }
}