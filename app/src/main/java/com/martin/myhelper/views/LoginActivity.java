package com.martin.myhelper.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
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

    private TextView registerLinkTextView;
    private EditText email, password;
    private Button _loginButton;
    private OpenActivity openActivity;

    private ElderlyVolunteerCRUDHelper crudHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // monitor onclick event of a text to open elderly registration scree
        this.openRegistrationScreen();

        // login to elderly homepage
        this.loginUserToFireStore();

        // show message for user record creation
        this.checkAndShowUserCreationSuccessMessage();

        // check the onChange event of the email field
        this.validateEmailOnEditTextChange();
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

    private void loginUserToFireStore(){

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        _loginButton = findViewById(R.id.loginButton);
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean isValidationSuccessful = Utility.validateInputsOnUserLogin(LoginActivity.this, email.getText().toString().trim(), password.getText().toString().trim());

                if (!isValidationSuccessful){
                    return;
                }

                Intent intent = getIntent();
                intent.getExtras();

                final int userType = intent.getIntExtra("userType",0);

                String[] _emailPassword = { email.getText().toString(), password.getText().toString() };
                crudHelper = new ElderlyVolunteerCRUDHelper();
Log.i("UTYPE", String.valueOf(userType));
                if(userType == GenericModel.USER_TYPE_ELDER){
                    loginFireStoreUser(LoginActivity.this, ElderlyHomeActivity.class, LoginActivity.this, _emailPassword , "elders");
                } else {
                    loginFireStoreUser(LoginActivity.this, VolunteerHomeActivity.class, LoginActivity.this, _emailPassword , "volunteers");
                }
            }
        });
    }

    private void checkAndShowUserCreationSuccessMessage(){

        Intent intent = getIntent();
        intent.getExtras();

        if (intent.hasExtra("recordCreated")){
            // show a message for successful record recreation
            Utility.showInformationDialog(CREATE_RECORD_SUCCESS_TITLE,
                    CREATE_RECORD_SUCCESS_MSG + "\n" + CREATE_RECORD_EMAIL_SUCCESS_MSG, LoginActivity.this);
        }

        if (intent.hasExtra("loginPageHeaderTitle")){
            TextView textView = findViewById(R.id.pageHeaderTitle);
            textView.setText(intent.getStringExtra("loginPageHeaderTitle"));
        }
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

    /***
     * Logs-in a user into the application after a successful authentication using FirebaseAuth. It opens
     * a new activity or screen if successful else remains on the same page
     * @param sourceActivity
     * @param _emailPassword
     */
    private void loginFireStoreUser(final Context sourceActivity, final Class destinationActivity, final AppCompatActivity appCompatActivity, final String[] _emailPassword, final String  modelTableAsCollection){
        crudHelper = new ElderlyVolunteerCRUDHelper();

        firebaseAuth = Utility.getFirebaseAuthenticationInstance();
        firebaseAuth.signInWithEmailAndPassword(_emailPassword[0], _emailPassword[1]).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                // get and set user type and redirect to the correct page or screen
                firebaseFirestore = Utility.getFirebaseFireStoreInstance();
                final DocumentReference documentReference = firebaseFirestore.collection(modelTableAsCollection).document(crudHelper.getCurrentUserID());
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.exists()){

                                String ut = documentSnapshot.getString("userType");

                                openActivity = new OpenActivity();
                                if (Integer.parseInt(ut) == GenericModel.USER_TYPE_ELDER){
                                    Log.i("ACT", crudHelper.getCurrentUserID());
                                    //openActivity.openAnActivityScreen(sourceActivity, destinationActivity);

                                    Intent intent = new Intent(LoginActivity.this, ElderlyHomeActivity.class);
                                    startActivity(intent);

                                } if (Integer.parseInt(ut) == GenericModel.USER_TYPE_VOLUNTEER){
                                    Intent intent = new Intent(LoginActivity.this, VolunteerHomeActivity.class);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Utility.showInformationDialog("LOGIN ERROR", e.getMessage(), appCompatActivity);
            }
        });
    }
}