package com.martin.myhelper.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.martin.myhelper.R;
import com.martin.myhelper.helpers.ElderlyVolunteerCRUDHelper;
import com.martin.myhelper.helpers.OpenActivity;
import com.martin.myhelper.helpers.Utility;
import com.martin.myhelper.model.GenericModel;

import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_EMAIL_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_MSG;
import static com.martin.myhelper.helpers.Utility.CREATE_RECORD_SUCCESS_TITLE;

public class LoginActivity extends AppCompatActivity {

    private AppCompatActivity appCompatActivity = LoginActivity.this;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private TextView registerLinkTextView, resetPasswordTextView;
    private EditText email, password;
    private Button _loginButton;

    private ElderlyVolunteerCRUDHelper crudHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // monitor onclick event of a text to open elderly registration scree
        this.openRegistrationScreen();

        // login to elderly homepage
        this.loginUserToFireStore();

        // check the onChange event of the email field
        this.validateEmailOnEditTextChange();

        this.handlePasswordResetLinkOnClick();

        this.showPageHeaderTitleText();
    }

    /**
     * Open the elderly registration screen when clicked
     */
    private void openRegistrationScreen(){

        Intent intent = getIntent();
        intent.getExtras();

        final int userType = intent.getIntExtra("userType",0);

        registerLinkTextView = findViewById(R.id.registerLink);
        registerLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userType == GenericModel.USER_TYPE_ELDER){
                    Intent intent = new Intent(LoginActivity.this, ElderlyRegistrationActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.this, VolunteerRegistrationActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void showPageHeaderTitleText(){
        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("loginPageHeaderTitle")){
            TextView _pageTitleView = findViewById(R.id.pageHeaderTitle);
            _pageTitleView.setText(intent.getStringExtra("loginPageHeaderTitle"));
        }
    }

    private void handlePasswordResetLinkOnClick(){

        Intent intent = getIntent();
        intent.getExtras();

        final int userType = intent.getIntExtra("userType",0);

        resetPasswordTextView = findViewById(R.id.resetPasswordLink);
        resetPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, PasswordResetActivity.class);

                if(userType == GenericModel.USER_TYPE_ELDER){
                    intent.putExtra("userType", GenericModel.USER_TYPE_ELDER);
                } else {
                    intent.putExtra("userType", GenericModel.USER_TYPE_VOLUNTEER);
                }

                startActivity(intent);
            }
        });
    }

    private void loginUserToFireStore(){

        _loginButton = findViewById(R.id.loginButton);
       _loginButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               email = findViewById(R.id.email);
               password = findViewById(R.id.password);

               String _email = email.getText().toString();
               String _password = password.getText().toString();

               crudHelper = new ElderlyVolunteerCRUDHelper();

               boolean isValidationSuccessful = Utility.validateInputsOnUserLogin(LoginActivity.this, _email, _password);

               if (!isValidationSuccessful){
                   return;
               }

               Intent intent = getIntent();
               intent.getExtras();

               int userType = intent.getIntExtra("userType",0);

               if(userType == GenericModel.USER_TYPE_ELDER){
                   loginFireStoreUser(LoginActivity.this, _email, _password, userType);
               } else if (userType == GenericModel.USER_TYPE_VOLUNTEER) {
                   loginFireStoreUser(LoginActivity.this,  _email, _password , userType);
               }
           }
       });
    }


    /***
     * Logs-in a user into the application after a successful authentication using FirebaseAuth. It opens
     * a new activity or screen if successful else remains on the same page
     * @param appCompatActivity
     * @param _email
     * @param _password
     */
    private void loginFireStoreUser(final AppCompatActivity appCompatActivity, final String _email, String _password, final int userTYPE){

        crudHelper = new ElderlyVolunteerCRUDHelper();

        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseAuth.signInWithEmailAndPassword(_email, _password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                Intent intent;

                if (userTYPE == GenericModel.USER_TYPE_ELDER){

                    intent = new Intent(LoginActivity.this, ElderlyHomeActivity.class);
                    startActivity(intent);

                } else if (userTYPE == GenericModel.USER_TYPE_VOLUNTEER){
                    intent = new Intent(LoginActivity.this, VolunteerHomeActivity.class);
                    startActivity(intent);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("LOGIN ERROR", e.getMessage(), appCompatActivity);
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

}